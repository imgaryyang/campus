package com.gzzm.sms;

import com.gzzm.platform.commons.*;
import com.gzzm.platform.organ.*;
import net.cyan.commons.collections.JobQueue;
import net.cyan.commons.util.*;
import net.cyan.commons.util.io.WebUtils;
import net.cyan.nest.annotation.Inject;
import net.cyan.sms.*;

import java.util.*;
import java.util.regex.Pattern;

/**
 * 短消息服务
 *
 * @author camel
 * @date 2010-11-15
 */
public class SmsService
{
    private static final Pattern PHONE_PATTERN = Pattern.compile(Patterns.MOIBLE_PHONE);

    @Inject
    private static Provider<SmsService> serviceProvider;

    @Inject
    private static Provider<DeptService> deptServiceProvider;

    @Inject
    private SmsClients clients;

    @Inject
    private SmsDao dao;

    @Inject
    private SmsConfig config;

    private static final Map<Integer, JobQueue<Long>> sendJobQueues = new HashMap<Integer, JobQueue<Long>>();

    private static Thread sendThread = null;

    private static class SendTask implements Runnable
    {
        @SuppressWarnings("InfiniteLoopStatement")
        public void run()
        {
            while (true)
            {
                try
                {
                    serviceProvider.get().sendSmss(DateUtils.truncate(DateUtils.addDate(new Date(), -1)), null);
                }
                catch (Throwable ex)
                {
                    //发送一个短信失败，已经是最外层，不能再throw异常，记录日志
                    Tools.log(ex);
                }

                try
                {
                    synchronized (SendTask.class)
                    {
                        try
                        {
                            SendTask.class.wait(10000);
                        }
                        catch (InterruptedException ex)
                        {
                            //被中断，继续下次循环
                        }
                    }
                }
                catch (Throwable ex)
                {
                    //出现错误，不要让线程停止
                    Tools.log(ex);
                }
            }
        }
    }

    public SmsService()
    {
    }

    public SmsDao getDao()
    {
        return dao;
    }

    public SmsUser getUserByLoginName(String userName) throws Exception
    {
        return dao.getUserByLoginName(userName);
    }

    public SmsUser getUserByDeptId(Integer deptId) throws Exception
    {
        SmsUser user = dao.getUserByDeptId(deptId);
        if (user != null)
            return user;

        if (deptId == 1)
            return null;

        DeptInfo dept = deptServiceProvider.get().getDept(deptId);
        if (dept == null)
            return null;

        while (dept.getDeptId() != 1)
        {
            user = dao.getUserByDeptId(dept.getParentDeptId());
            if (user != null && user.isInheritable() != null && user.isInheritable())
                return user;

            dept = dept.parentDept();
        }

        return null;
    }

    //    @Transactional
    public List<SendResultItem> sendSms(String content, String[] phone, String serial, Integer userId,
                                        String clientCode) throws Exception
    {
        return sendSms(content, phone, serial, dao.getSmsUser(userId), clientCode);
    }

    /**
     * 发送短消息
     *
     * @param content    短消息的内容
     * @param phone      接收者的电话号码
     * @param serial     短消息序号
     * @param user       发送短信的用户
     * @param clientCode 客户端编码，可以为空
     * @return 每个电话号码的发送结果和对应在数据库中的id号
     * @throws Exception 发送短消息错误
     */
//    @Transactional
    public List<SendResultItem> sendSms(String content, String[] phone, String serial, SmsUser user, String clientCode)
            throws Exception
    {
        List<SendResultItem> result = new ArrayList<SendResultItem>(phone.length);

        for (String s : phone)
        {
            if (!StringUtils.isEmpty(s))
            {
                SmsMt sms = dao.getSmsMtByPhoneAndContent(s, content);
                SendResultItem item = new SendResultItem();
                item.setPhone(s);

                if (sms == null)
                {
                    sms = new SmsMt();
                    sms.setSerial(serial);
                    sms.setPhone(s);
                    sms.setUserId(user.getUserId());
                    sms.setContent(content.toCharArray());
                    sms.setSendTime(new Date());
                    sms.setClientCode(clientCode);

                    if (!PHONE_PATTERN.matcher(s).matches())
                    {
                        String error = "手机号码不正确";
                        sms.setError(error);
                        item.setError(error);
                    }
                    else
                    {
                        GatewayType gatewayType = config.getGatewayType(s);
                        if (gatewayType == null)
                            throw new SystemException("no gateway for " + s);

                        sms.setGatewayType(gatewayType);

                        UserGateway gateway = user.getGateway(gatewayType);
                        if (gateway == null)
                            throw new SystemException("no gateway for " + s);

                        sms.setGatewayId(gateway.getGatewayId());
                    }

                    dao.add(sms);
                }

                item.setSmsId(sms.getSmsId());

                result.add(item);
            }
        }

        startSmsSend();

        return result;
    }

    public static void startSmsSend()
    {
        try
        {
            synchronized (SendTask.class)
            {
                //启动发短信的线程
                if (sendThread == null || !sendThread.isAlive())
                {
                    sendThread = new Thread(new SendTask());
                    sendThread.start();
                }

                //同时发短信线程启动
                SendTask.class.notify();
            }
        }
        catch (Throwable ex)
        {
            //数据已经插入到数据库中，不影响返回结果
            Tools.log(ex);
        }
    }

    /**
     * 发送一条短信
     *
     * @param sms 要发送的短信
     * @throws Exception 短信发送错误
     */
    public void sendSms(SmsMt sms) throws Exception
    {
        SmsUser user = sms.getUser();

        GatewayType gatewayType = config.getGatewayType(sms.getPhone());

        UserGateway gateway = user.getGateway(gatewayType);

        if (gateway == null)
            return;

        SmsClientItem clientItem = clients.getItem(gateway.getGatewayId());

        //启动网关
        if (clientItem.getState() == GatewayState.error ||
                (clientItem.getState() == GatewayState.stoped && clientItem.isAvailable()))
        {
            clientItem.start();
        }

        if (clientItem.getState() == GatewayState.running && clientItem.getClient() != null)
        {
            String serial = sms.getSerial();

            Mt mt = new Mt();
            mt.setContent(new String(sms.getContent()));
            mt.setTargetPhone(sms.getPhone());
            mt.setServiceCode(gateway.getServiceCode());
            mt.setUserName(gateway.getUserName());
            mt.setPassword(gateway.getPassword());

            String number = gateway.getSubNumber();
            if (!StringUtils.isEmpty(serial))
            {
                if (StringUtils.isEmpty(number))
                    number = serial;
                else
                    number += serial;
            }
            mt.setSerial(number);

            try
            {
                final Long smsId = sms.getSmsId();
                clientItem.getClient().sendSms(mt, new SmsSendCallback()
                {
                    public void call(String messageId) throws Exception
                    {
                        SmsDao dao = serviceProvider.get().getDao();

                        SmsMt sms = dao.getSmsMt(smsId);
                        sms.setMessageId(messageId);
                        sms.setState(0);
                        dao.update(sms);
                    }

                    public void error(SmsException ex) throws Exception
                    {
                        SmsDao dao = serviceProvider.get().getDao();

                        SmsMt sms = dao.getSmsMt(smsId);
                        sms.setError(ex.getMessage());
                        sms.setState(0);
                        dao.update(sms);
                    }
                });
            }
            catch (Throwable ex)
            {
                boolean restart = false;
                if (WebUtils.isSocketException(ex) || ex instanceof SmsTimeoutException)
                {
                    restart = true;
                }
                else
                {
                    Tools.log(ex);

                    try
                    {
                        clientItem.getClient().ping();
                    }
                    catch (Throwable ex1)
                    {
                        Tools.log(ex1);

                        //ping错误，重启网关
                        restart = true;
                    }
                }

                if (restart)
                {
                    Tools.log("重启网关:" + clientItem.getGateway().getGatewayId());
                    try
                    {
                        synchronized (clientItem)
                        {
                            clientItem.stop();
                            clientItem.start();
                        }
                    }
                    catch (Throwable ex1)
                    {
                        //重启失败，继续重启
                        Tools.log(ex1);
                    }

                    if (!(ex instanceof SmsTimeoutException))
                    {
//                        sendSms(sms);
                        return;
                    }
                }

                try
                {
                    sms.setError(ex.getMessage());
                    sms.setState(0);
                    dao.update(sms);
                }
                catch (Throwable ex1)
                {
                    Tools.log(ex1);
                }
            }
        }
    }

    public void sendSms(Long smsId) throws Exception
    {
        sendSms(dao.getSmsMt(smsId));
    }

    public void sendSmss(Date time_start, Date time_end) throws Exception
    {
        while (true)
        {
            List<Long> smsIds = dao.queryNoSendedSmsIds(time_start, time_end);
            if (smsIds.size() == 0)
                return;

            for (Long smsId : smsIds)
            {
                SmsMt sms = dao.get(SmsMt.class, smsId);

                try
                {
                    //将短信状态置为发送中，防止重复发送
                    sms.setState(3);
                    dao.update(sms);
                }
                catch (Throwable ex)
                {
                    //更新状态错误不影响其他短信发送
                    Tools.log(ex);
                }

                JobQueue<Long> sendJobQueue;
                synchronized (sendJobQueues)
                {
                    sendJobQueue = sendJobQueues.get(sms.getGatewayId());

                    if (sendJobQueue == null)
                    {
                        sendJobQueues.put(sms.getGatewayId(), sendJobQueue = new JobQueue<Long>()
                        {
                            @Override
                            protected void run(Long smsId) throws Exception
                            {
                                try
                                {
                                    serviceProvider.get().sendSms(smsId);

//                                    synchronized (this)
//                                    {
//                                        //控制流量
//                                        wait(1000);
//                                    }
                                }
                                catch (Throwable ex)
                                {
                                    Tools.log(ex);
                                }
                            }
                        });
                    }
                }

                //将短信发送任务放到发送队列中，不允许短信并行发送，避免短信网关出错
                sendJobQueue.push(smsId);
            }
        }
    }

    /**
     * 更新短信状态
     *
     * @param messageId 消息ID
     * @param phone     电话号码
     * @param gatewayId 网关ID
     * @param time      接收时间
     * @param error     错误信息
     * @throws Exception 数据库更新错误
     */
    public void updateSmsState(String messageId, String phone, Integer gatewayId, Date time, String error)
            throws Exception
    {
        SmsMt sms = dao.getSmsByMessageId(messageId, phone, gatewayId);

        if (sms != null)
        {
            if (StringUtils.isEmpty(error))
                sms.setReceiveTime(time);
            else
                sms.setError(error);

            sms.setState(1);
            dao.update(sms);

            Processor processor = sms.getUser().getProcessorInstance();
            if (processor != null)
                processor.processReport(sms);
        }
    }

    private void receiveSms(String phone, String content, String serial, Date time, UserGateway userGateway)
            throws Exception
    {
        SmsMo sms = new SmsMo();
        sms.setGatewayId(userGateway.getGatewayId());
        sms.setPhone(phone);
        sms.setContent(content.toCharArray());
        sms.setSendTime(time);
        sms.setReceiveTime(new Date());
        sms.setUserId(userGateway.getUserId());
        sms.setState(0);

        String subNumber = userGateway.getSubNumber();
        if (StringUtils.isEmpty(subNumber))
            sms.setSerial(serial);
        else
            sms.setSerial(serial.substring(subNumber.length()));

        if (!StringUtils.isEmpty(sms.getSerial()))
        {
            sms.setClientCode(dao.getClinetCodeBySerialAndPhone(serial, phone));
        }

        dao.add(sms);

        SmsUser user = userGateway.getUser();
        Processor processor = user.getProcessorInstance();
        if (processor != null)
        {
            sms.setUser(user);
            try
            {
                processor.processReceive(sms);
            }
            catch (Throwable ex)
            {
                Tools.log(ex);
            }
        }
    }

    /**
     * 接收一条上行短信
     *
     * @param phone     电话号码
     * @param content   短信内容
     * @param serial    短信序号
     * @param time      短信发送时间
     * @param gatewayId 网关ID
     * @throws Exception 数据库更新错误
     */
    public void receiveSms(String phone, String content, String serial, Date time, Integer gatewayId) throws Exception
    {
        List<UserGateway> userGateways = dao.getUserGatewaysByGatewayId(gatewayId);
        UserGateway suitableUserGateway = null;
        if ("null".equals(serial))
        {
            //网关不支持短信序号，当作是回复最后一条短信

            SmsMt smsMt = dao.getLastSmsMtByPhone(phone, gatewayId);

            if (smsMt == null)
            {
                for (UserGateway userGateway : userGateways)
                {
                    if (StringUtils.isEmpty(userGateway.getSubNumber()))
                    {
                        suitableUserGateway = userGateway;
                        break;
                    }
                }
            }
            else
            {
                serial = smsMt.getSerial();
                Integer userId = smsMt.getUserId();

                for (UserGateway userGateway : userGateways)
                {
                    if (userGateway.getUserId().equals(userId))
                    {
                        suitableUserGateway = userGateway;
                        break;
                    }
                }
            }
        }
        else
        {
            String subNumber0 = null;
            for (UserGateway userGateway : userGateways)
            {
                String subNumber = userGateway.getSubNumber();

                if (StringUtils.isEmpty(subNumber) || serial != null && serial.startsWith(subNumber))
                {
                    if (StringUtils.isEmpty(subNumber0) ||
                            subNumber != null && subNumber.length() > subNumber0.length())
                    {
                        subNumber0 = subNumber;
                        suitableUserGateway = userGateway;
                    }
                }
            }
        }

        if (suitableUserGateway != null)
            receiveSms(phone, content, serial, time, suitableUserGateway);
    }

    /**
     * 读取未阅读的短信，并置为已阅读
     *
     * @param userId     短信用户ID
     * @param clientCode 客户端编码，如果为null，则查询所有回复
     * @return 短信列表
     * @throws Exception 数据库查询或者更新错误
     */
    public List<SmsMo> readSmsMoList(Integer userId, String clientCode) throws Exception
    {
        List<SmsMo> list = dao.queryNoReadedSmsMoList(userId, clientCode);

        for (SmsMo smsMo : list)
        {
            smsMo.setState(1);
            dao.update(smsMo);
        }

        return list;
    }

    /**
     * 读取未阅读的短信状态报告，并置为已阅读
     *
     * @param userId     短信用户ID
     * @param clientCode 客户端编码
     * @return 短信状态报告列表
     * @throws Exception 数据库查询或者更新错误
     */
    public List<SmsMt> readSmsMtList(Integer userId, String clientCode) throws Exception
    {
        List<SmsMt> list = dao.queryNoReadedSmsMtList(userId, clientCode);

        for (SmsMt smsMt : list)
        {
            smsMt.setState(2);
            dao.update(smsMt);
        }

        return list;
    }

    public List<SmsMo> querySmsMoListBySerial(String phone, String serial) throws Exception
    {
        return dao.querySmsMoListBySerial(phone, serial);
    }
}
