package com.gzzm.platform.message.sms;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.message.Message;
import com.gzzm.platform.organ.*;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 短消息服务，集成了和短信操作相关的方法
 *
 * @author camel
 * @date 2010-5-23
 */
public class SmsService
{
    @Inject
    private static Provider<SmsService> serviceProvider;

    @Inject
    private static Provider<SmsSender> senderProvider;

    @Inject
    private static Provider<ReceiptReceiver> receiverProvider;

    private static SmsSender sender;

    private static ReceiptReceiver receiver;

    private static List<SmsReceiptProcessor> receiptProcessors = new ArrayList<SmsReceiptProcessor>(2);

    private static Map<String, SmsReplyProcessor> replyProcessors;

    static
    {
        //修改短信日志的状态
        receiptProcessors.add(new SmsReceiptProcessor()
        {
            public void process(ReceiptInfo receipt, SmsService service) throws Exception
            {
                SmsLog sms = null;

                if (!StringUtils.isEmpty(receipt.getSerial()))
                {
                    sms = service.dao.getSmsLogBySerial(receipt.getSerial(), receipt.getPhone());
                }

                if (sms == null)
                {
                    sms = service.dao.getSmsLogByMessageId(receipt.getMessageId(), receipt.getPhone());
                }

                if (sms != null)
                {
                    sms.setState(receipt.getType() == 2 ? SmsState.error : SmsState.accepted);

                    if (receipt.getType() != 2 && sms.getReceiveTime() == null)
                    {
                        sms.setReceiveTime(receipt.getTime());
                    }

                    service.dao.update(sms);

                    if (receipt.getType() == 1)
                    {
                        //记录回复日志
                        SmsReceiptLog receiptLog = new SmsReceiptLog();
                        receiptLog.setSmsId(sms.getSmsId());
                        receiptLog.setContent(receipt.getContent());
                        receiptLog.setPhone(receipt.getPhone());
                        receiptLog.setReplyTime(receipt.getTime());
                        service.dao.add(receiptLog);
                    }

                    SmsReceipt smsReceipt = service.dao.getReceiptByLogId(sms.getSmsId());
                    if (smsReceipt != null)
                    {
                        switch (receipt.getType())
                        {
                            case 0:
                                if (smsReceipt.getReceiveTime() == null)
                                {
                                    smsReceipt.setReceiveTime(receipt.getTime());
                                    service.dao.update(smsReceipt);
                                }
                                break;
                            case 1:
                                if (smsReceipt.getReceiveTime() == null)
                                    smsReceipt.setReceiveTime(receipt.getTime());

                                if (!StringUtils.isEmpty(receipt.getContent()))
                                {
                                    int replyCount =
                                            smsReceipt.getReplyCount() == null ? 0 : smsReceipt.getReplyCount();

                                    //如果之前发送错误，清除错误
                                    smsReceipt.setError("");

                                    //回复的数量+1
                                    smsReceipt.setReplyCount(replyCount + 1);

                                    if (replyCount == 0)
                                    {
                                        smsReceipt.setContent(receipt.getContent());

                                        //首次回复，记录首次回复的时间
                                        smsReceipt.setFirstReplyTime(receipt.getTime());
                                    }
                                    else
                                    {
                                        //之前已经回复过，则将新的内容叠加到原来的内容后面，
                                        // 并每次回复后面加上时间，可以根据此区分多次回复的内容
                                        StringBuilder content = new StringBuilder(
                                                smsReceipt.getContent() == null ? "" : smsReceipt.getContent());

                                        if (replyCount == 1)
                                        {
                                            //第二次回复，则之前第一次没有在后面加上时间，加之
                                            content.append(" (")
                                                    .append(DateUtils.toString(smsReceipt.getLastReplyTime()))
                                                    .append(")");
                                        }

                                        //加上新回复的内容及时间
                                        content.append("\n");

                                        if (receipt.getContent() != null)
                                            content.append(receipt.getContent());

                                        content.append(" (").append(DateUtils.toString(receipt.getTime())).append(")");

                                        smsReceipt.setContent(content.toString());
                                    }

                                    //记录最后一次回复的时间
                                    smsReceipt.setLastReplyTime(receipt.getTime());
                                }

                                service.dao.update(smsReceipt);
                                break;
                            case 2:
                                smsReceipt.setError(receipt.getContent());
                                service.dao.update(smsReceipt);
                        }
                    }

                    Map<String, SmsReplyProcessor> replyProcessors = SmsService.replyProcessors;
                    if (replyProcessors != null)
                    {
                        //转发消息回复给各模块定义的处理器
                        String code = sms.getMessageCode();
                        if (code != null && code.length() > 3)
                        {
                            String type = code.substring(0, 3);
                            SmsReplyProcessor processor = replyProcessors.get(type);
                            if (processor != null)
                            {
                                SmsReply reply = new SmsReply();
                                reply.setCode(code.substring(3));
                                reply.setContent(receipt.getContent());
                                reply.setPhone(receipt.getPhone());
                                reply.setTime(receipt.getTime());
                                reply.setType(receipt.getType());

                                processor.process(reply);
                            }
                        }
                    }
                }
            }
        });

        //手机投票
        addReplyProcessor(SmsVoteReplyProcessor.VOT, new SmsVoteReplyProcessor());
    }

    @Inject
    private SmsDao dao;

    public SmsService()
    {
    }

    public static SmsService getService()
    {
        return serviceProvider.get();
    }

    private static synchronized SmsSender getSender()
    {
        if (sender == null)
            sender = senderProvider.get();

        return sender;
    }

    private static synchronized ReceiptReceiver getReceiver()
    {
        if (receiver == null)
            receiver = receiverProvider.get();

        return receiver;
    }

    public SmsDao getDao()
    {
        return dao;
    }

    /**
     * 通过手机短信发送一条消息
     *
     * @param message 要发送的消息
     * @return 消息ID，由短息服务提供
     * @throws Exception 发送消息错误
     */
    public String send(Message message) throws Exception
    {
        SmsLog sms = new SmsLog();
        sms.setMessageCode(message.getCode());
        sms.setPhone(message.getPhone());
        sms.setContent(message.getContent());
        sms.setUserId(message.getUserId());
        sms.setSender(message.getSender());
        sms.setFromDeptId(message.getFromDeptId());
        sms.setToDeptId(message.getToDeptId());
        sms.setSendTime(new Date());
        sms.setState(SmsState.notsended);
        sms.setFixedTime(message.getSendTime());

        dao.add(sms);
        String smsId = sms.getSmsId();

        send(smsId, message.getSendTime());

        return smsId;
    }

    private void send(final String smsId, Date sendTime) throws Exception
    {
        if (sendTime == null || (System.currentTimeMillis() - sendTime.getTime()) > -60 * 1000 * 2)
        {
            send0(smsId);
        }
        else
        {
            Jobs.addJob(new Runnable()
            {
                public void run()
                {
                    try
                    {
                        getService().send0(smsId);
                    }
                    catch (Throwable ex)
                    {
                        Tools.log("send sms fail,smsId:" + smsId, ex);
                    }
                }
            }, sendTime, "sms_send_" + smsId);
        }
    }

    public void send0(String smsId) throws Exception
    {
        try
        {
            SmsLog sms = dao.getSmsLog(smsId);

            if (sms.getState() == SmsState.notsended)
            {
                getSender().send(sms);

                if (sms.getMessageId() != null)
                {
                    sms.setState(SmsState.sended);
                    dao.update(sms);
                }
            }
        }
        catch (Throwable ex)
        {
            try
            {
                SmsLog sms = new SmsLog();
                sms.setSmsId(smsId);
                sms.setState(SmsState.error);
                dao.update(sms);
            }
            catch (Throwable ex1)
            {
                //不影响主错误
            }

            if (ex instanceof Error)
                throw (Error) ex;
            else
                throw (Exception) ex;
        }
    }

    /**
     * 发送手机短信给若干接收者
     *
     * @param sendInfo 发送短消息所需要的全部信息
     * @throws Exception 发送短信异常，或者记录短信到数据库错误
     */
    @Transactional
    public void send(SmsSendInfo sendInfo) throws Exception
    {
        Long smsId = sendInfo.getSmsId();

        String content;
        Integer sender;
        Integer deptId = sendInfo.getDeptId();
        Date time = new Date();
        Date fixedTime;
        boolean sign;
        boolean dept;
        boolean requireReply;

        if (smsId == null)
        {
            sender = sendInfo.getSender();
            content = sendInfo.getContent();
            dept = sendInfo.isDept();
            fixedTime = sendInfo.getFixedTime();
            sign = sendInfo.isSign();
            requireReply = sendInfo.isRequireReply();

            Sms sms = new Sms();
            sms.setContent(content);
            sms.setUserId(sendInfo.getSender());
            if (dept)
                sms.setDeptId(deptId);
            sms.setSendTime(time);
            sms.setFixedTime(sendInfo.getFixedTime());
            sms.setSign(sendInfo.isSign());
            sms.setRequireReply(sendInfo.isRequireReply());

            dao.add(sms);

            smsId = sms.getSmsId();
        }
        else
        {
            Sms sms = dao.getSms(smsId);

            content = sms.getContent();
            dept = sms.getDeptId() != null;
            sender = sms.getUserId();
            fixedTime = sms.getFixedTime();
            sign = sms.isSign() == null || sms.isSign();
            requireReply = sms.getRequireReply() == null || sms.getRequireReply();

            if (dept)
                deptId = sms.getDeptId();
        }

        if (sign)
        {
            if (dept)
            {
                content += Tools.getMessage("com.gzzm.platform.message.sms.deptsign", dao.load(Dept.class, deptId));
            }
            else
            {
                User user = dao.getUser(sender);
                content += Tools.getMessage("com.gzzm.platform.message.sms.usersign", user);
            }
        }

        for (SmsReceiver receiver : sendInfo.getReceivers())
        {
            //发送给每一个接收者
            String phone;
            String name;
            Integer userId;

            if (receiver.getUserId() != null)
            {
                userId = receiver.getUserId();
                User user = dao.getUser(userId);
                phone = user.getPhone();
                name = user.getUserName();
                receiver.setName(name);
                receiver.setPhone(phone);
            }
            else
            {
                userId = null;
                phone = receiver.getPhone();
                name = receiver.getName();
            }

            Message message = new Message();
            message.setUserId(userId);
            message.setPhone(phone);
            message.setMessage(content);
            message.setFromDeptId(deptId);
            message.setSendTime(fixedTime);
            message.setSender(sender);

            if (requireReply)
                message.setCode("sms");

            String logId = null;

            try
            {
                logId = send(message);
            }
            catch (Throwable ex)
            {
                //发送一条消息失败不影响下一条
                Tools.log(ex);
            }

            //往数据库写入回执
            SmsReceipt smsReceipt = new SmsReceipt();
            smsReceipt.setPhone(phone);
            smsReceipt.setUserName(name);
            smsReceipt.setSmsId(smsId);
            smsReceipt.setReplyCount(0);
            smsReceipt.setLogId(logId);
            smsReceipt.setUserId(userId);
            dao.add(smsReceipt);

            sendInfo.sendNext();
        }
    }

    public void receive() throws Exception
    {
        ReceiptReceiver receiver = getReceiver();
        if (receiver != null)
        {
            Collection<ReceiptInfo> receipts = receiver.receive();
            Date time = null;

            if (receipts != null)
            {
                //接收到回执，逐条处理回执
                for (ReceiptInfo receipt : receipts)
                {
                    if (receipt.getTime() == null)
                    {
                        //如果接口不支持短信回执时间，用当前时间代替
                        if (time == null)
                            time = new Date();

                        receipt.setTime(time);
                    }

                    process(receipt);
                }
            }
        }
    }

    private void process(ReceiptInfo receipt)
    {
        List<SmsReceiptProcessor> receiptProcessors = SmsService.receiptProcessors;

        if (receiptProcessors != null)
        {
            for (SmsReceiptProcessor processor : receiptProcessors)
            {
                try
                {
                    processor.process(receipt, this);
                }
                catch (Throwable ex)
                {
                    //处理一个短信回执错误，不影响接下来的处理，将异常catch掉
                    Tools.log("process sms receipt fail,messageId:" + receipt.getMessageId(), ex);
                }
            }
        }
    }

    public Sms getSms(Long smsId) throws Exception
    {
        return dao.getSms(smsId);
    }

    public void sendNoSendedSms() throws Exception
    {
        List<SmsLog> smsLogs = dao.queryNoSendedSms();

        for (SmsLog sms : smsLogs)
        {
            try
            {
                send(sms.getSmsId(), sms.getFixedTime());
            }
            catch (Throwable ex)
            {
                Tools.log(ex);
            }
        }
    }

    public static synchronized void addReceiptProcessor(SmsReceiptProcessor processor)
    {
        List<SmsReceiptProcessor> receiptProcessors = new ArrayList<SmsReceiptProcessor>(SmsService.receiptProcessors);
        receiptProcessors.add(processor);

        SmsService.receiptProcessors = receiptProcessors;
    }

    public static synchronized void addReplyProcessor(String type, SmsReplyProcessor processor)
    {
        if (replyProcessors == null)
        {
            replyProcessors = Collections.singletonMap(type, processor);
        }
        else
        {
            Map<String, SmsReplyProcessor> replyProcessors =
                    new HashMap<String, SmsReplyProcessor>(SmsService.replyProcessors);

            replyProcessors.put(type, processor);

            SmsService.replyProcessors = replyProcessors;
        }
    }
}
