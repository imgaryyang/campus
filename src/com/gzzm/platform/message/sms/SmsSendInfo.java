package com.gzzm.platform.message.sms;

import java.io.Serializable;
import java.util.*;

/**
 * 短信发送信息，包装发送短消息所需要的全部信息
 *
 * @author camel
 * @date 2010-5-23
 */
public class SmsSendInfo implements Serializable
{
    private static final long serialVersionUID = 6643125947494419611L;

    /**
     * 发送短信的用户ID
     */
    private Integer sender;

    /**
     * 发送短信的部门ID
     */
    private Integer deptId;

    /**
     * 短信内容
     */
    private String content;

    /**
     * 是否以部门的名义发送短信
     */
    private boolean dept;

    /**
     * 短消息接收者
     */
    private List<SmsReceiver> receivers;

    /**
     * 正在发送短信的接收者序号，从0开始
     */
    private int sendingIndex;

    private Long smsId;

    private Date fixedTime;

    private boolean sign;

    private boolean requireReply;

    public SmsSendInfo()
    {
    }

    public Integer getSender()
    {
        return sender;
    }

    public void setSender(Integer sender)
    {
        this.sender = sender;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public boolean isDept()
    {
        return dept;
    }

    public void setDept(boolean dept)
    {
        this.dept = dept;
    }

    public List<SmsReceiver> getReceivers()
    {
        return receivers;
    }

    public void setReceivers(List<SmsReceiver> receivers)
    {
        this.receivers = receivers;
    }

    public void addReceiver(SmsReceiver receiver)
    {
        if (receivers == null)
            receivers = new ArrayList<SmsReceiver>();
        receivers.add(receiver);
    }

    public Long getSmsId()
    {
        return smsId;
    }

    public void setSmsId(Long smsId)
    {
        this.smsId = smsId;
    }

    public Date getFixedTime()
    {
        return fixedTime;
    }

    public void setFixedTime(Date fixedTime)
    {
        this.fixedTime = fixedTime;
    }

    public int getSendingIndex()
    {
        return sendingIndex;
    }

    protected void sendNext()
    {
        sendingIndex++;
    }

    public boolean isSign()
    {
        return sign;
    }

    public void setSign(boolean sign)
    {
        this.sign = sign;
    }

    public boolean isRequireReply()
    {
        return requireReply;
    }

    public void setRequireReply(boolean requireReply)
    {
        this.requireReply = requireReply;
    }
}
