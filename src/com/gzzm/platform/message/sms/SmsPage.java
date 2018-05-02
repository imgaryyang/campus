package com.gzzm.platform.message.sms;

import com.gzzm.platform.annotation.AuthDeptIds;
import com.gzzm.platform.commons.*;
import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.platform.receiver.Receiver;
import net.cyan.arachne.*;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.transaction.*;
import net.cyan.commons.util.StringUtils;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 发送手机短消息的界面入口
 *
 * @author camel
 * @date 2010-5-23
 */
@Service
public class SmsPage
{
    /**
     * 增强SmsSendInfo，使其具有向客户端展示短信发送进度的功能
     */
    public static class ProgressSmsSendInfo extends SmsSendInfo implements ProgressInfo
    {
        private static final long serialVersionUID = -1220149891365667200L;

        public ProgressSmsSendInfo()
        {
        }

        public String getProgressName()
        {
            return Tools.getMessage("message.sms.send.progressname");
        }

        public float getPercentage()
        {
            List<SmsReceiver> receivers = getReceivers();

            return receivers == null ? 0 : (float) getSendingIndex() / receivers.size();
        }

        public String getDescription()
        {
            int sendingIndex = getSendingIndex();

            List<SmsReceiver> receivers = getReceivers();

            if (receivers != null)
            {
                if (sendingIndex == receivers.size())
                    return Tools.getMessage("message.sms.send.progressfinish");
                else
                    return Tools.getMessage("message.sms.send.progressdescription", sendingIndex,
                            getReceivers().get(sendingIndex).getName());
            }

            return "";
        }
    }

    @Inject
    private SmsService service;

    /**
     * 将短信发送给谁
     */
    @Require
    private String sendTo;

    /**
     * 短信的内容
     */
    @Require
    private String content;

    /**
     * 定时发送时间
     */
    private Date fixedTime;

    /**
     * 是否以部门的名义发送短信
     */
    private Boolean dept;

    /**
     * 是否要求回复
     */
    private Boolean requireReply;

    /**
     * 发送短信的部门
     */
    private Integer deptId;


    @Inject
    private UserOnlineInfo userOnlineInfo;

    @AuthDeptIds
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private Collection<Integer> authDeptIds;

    /**
     * 短信id，此值不为null表示使用原来的短信继续发送
     */
    private Long smsId;

    private Long[] receiptIds;

    private Integer templateId;

    private boolean sign = true;

    public SmsPage()
    {
    }

    @Service(url = "/message/sms")
    @Forward(page = "/platform/message/sms.ptl")
    public String show() throws Exception
    {
        if (smsId != null)
        {
            Sms sms = service.getSms(smsId);

            if (receiptIds != null)
            {
                List<SmsReceipt> receipts = service.getDao().getReceipts(receiptIds);
                if (receipts.size() > 0)
                {
                    List<String> receivers = new ArrayList<String>(receipts.size());
                    for (SmsReceipt receipt : receipts)
                    {
                        String s;
                        if (receipt.getUserId() != null)
                            s = "\"" + receipt.getUserName() + "\"<" + receipt.getUserId() + "@local>";
                        else if (receipt.getUserName().equals(receipt.getPhone()))
                            s = receipt.getPhone();
                        else
                            s = "\"" + receipt.getUserName() + "\"<" + receipt.getPhone() + ">";
                        if (!receivers.contains(s))
                            receivers.add(s);
                    }
                    sendTo = StringUtils.concat(receivers, ",");
                }
            }

            content = sms.getContent();
            dept = sms.getDeptId() != null;

            if (!userOnlineInfo.isAdmin())
            {
                //不是管理员，权限校验
                if (!(sms.getUserId().equals(userOnlineInfo.getUserId()) ||
                        dept != null && dept && authDeptIds != null && authDeptIds.contains(sms.getDeptId())))
                {
                    throw new SystemMessageException(Messages.NO_AUTH_RECORD,
                            "no auth,smsId:" + smsId + ",userId:" + userOnlineInfo.getUserId());
                }
            }
        }
        else
        {
            if (templateId != null)
            {
                SmsTemplate smsTemplate = service.getDao().getSmsTemplate(templateId);
                if (smsTemplate != null)
                    content = smsTemplate.getContent();
            }
        }

        return null;
    }

    @ObjectResult
    @Service(url = "/message/sms", method = HttpMethod.post)
    @Transactional(mode = TransactionMode.supported)
    public void send() throws Exception
    {
        if (dept == null)
            dept = false;

        List<Receiver> receiverList = Receiver.parseReceiverList(sendTo);
        if (receiverList != null)
        {
            Integer userId = userOnlineInfo.getUserId();

            if (smsId != null)
            {
                Sms sms = service.getSms(smsId);

                dept = sms.getDeptId() != null;

                if (!userOnlineInfo.isAdmin())
                {
                    //不是管理员，权限校验
                    if (!(sms.getUserId().equals(userId) ||
                            dept != null && dept && authDeptIds != null && authDeptIds.contains(sms.getDeptId())))
                    {
                        throw new SystemMessageException(Messages.NO_AUTH_RECORD,
                                "no auth,smsId:" + smsId + ",userId:" + userOnlineInfo.getUserId());
                    }
                }
            }

            SmsSendInfo sendInfo = RequestContext.getContext().getProgressInfo(ProgressSmsSendInfo.class);

            if (sendInfo == null)
                sendInfo = new SmsSendInfo();

            sendInfo.setSign(sign);

            if (smsId == null)
            {
                sendInfo.setContent(content);
                sendInfo.setSender(userId);
                sendInfo.setDeptId(getDeptId());
                sendInfo.setDept(dept != null && dept);
                sendInfo.setRequireReply(requireReply != null && requireReply);
                sendInfo.setFixedTime(fixedTime);
            }
            else
            {
                sendInfo.setSmsId(smsId);
                if (dept == null || !dept)
                    sendInfo.setDeptId(getDeptId());
            }

            sendInfo.setReceivers(new ArrayList<SmsReceiver>(receiverList.size()));

            for (Receiver receiver : receiverList)
            {
                if (receiver.getId() != null)
                {
                    sendInfo.addReceiver(new SmsReceiver(Integer.valueOf(receiver.getId())));
                }
                else
                {
                    String phone = receiver.getValue();

                    if (phone.startsWith("#"))
                    {
                        for (PhoneListItem item : service.getDao()
                                .getPhoneListItems(Integer.valueOf(phone.substring(1))))
                        {
                            sendInfo.addReceiver(new SmsReceiver(item.getPhone(), item.getItemName()));
                        }
                    }
                    else
                    {
                        sendInfo.addReceiver(new SmsReceiver(phone, receiver.getName()));
                    }
                }
            }

            service.send(sendInfo);
        }
    }

    private Integer getDeptId()
    {
        if (deptId == null)
        {
            if (dept != null && dept && authDeptIds != null && authDeptIds.size() > 0)
                deptId = authDeptIds.iterator().next();
            else
                deptId = userOnlineInfo.getDeptId();
        }
        return deptId;
    }

    public String getSendTo()
    {
        return sendTo;
    }

    public void setSendTo(String sendTo)
    {
        this.sendTo = sendTo;
    }

    public Date getFixedTime()
    {
        return fixedTime;
    }

    public void setFixedTime(Date fixedTime)
    {
        this.fixedTime = fixedTime;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public Boolean isDept()
    {
        return dept;
    }

    public void setDept(Boolean dept)
    {
        this.dept = dept;
    }

    public Boolean getRequireReply()
    {
        return requireReply;
    }

    public void setRequireReply(Boolean requireReply)
    {
        this.requireReply = requireReply;
    }

    public Long getSmsId()
    {
        return smsId;
    }

    public void setSmsId(Long smsId)
    {
        this.smsId = smsId;
    }

    public Long[] getReceiptIds()
    {
        return receiptIds;
    }

    public void setReceiptIds(Long[] receiptIds)
    {
        this.receiptIds = receiptIds;
    }

    public Integer getTemplateId()
    {
        return templateId;
    }

    public void setTemplateId(Integer templateId)
    {
        this.templateId = templateId;
    }

    public boolean isSign()
    {
        return sign;
    }

    public void setSign(boolean sign)
    {
        this.sign = sign;
    }
}
