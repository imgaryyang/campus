package com.gzzm.platform.message.sms;

import com.gzzm.platform.organ.User;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * 短信回复
 *
 * @author camel
 * @date 2010-5-23
 */
@Entity(table = "PFSMSRECEIPT", keys = "receiptId")
public class SmsReceipt
{
    @Generatable(length = 18)
    private Long receiptId;

    /**
     * Sms对象的短信ID
     */
    @Index
    private Long smsId;

    /**
     * 对应的发送日志的id
     */
    @Index
    @ColumnDescription(type = "varchar(32)")
    private String logId;

    /**
     * 关联短信对象
     */
    @NotSerialized
    private Sms sms;

    @NotSerialized
    @ToOne("LOGID")
    private SmsLog smsLog;

    /**
     * 接收者电话
     */
    private String phone;

    /**
     * 接收者的用户ID，仅发送给系统用户时有效
     */
    private Integer userId;

    private User user;

    /**
     * 接收者姓名
     */
    private String userName;

    /**
     * 发送时间，如果是只发送一次，则此时间和Sms.sendTime相同，对于发送多次的短信，每次发送时间可能不同
     *
     * @see Sms#sendTime
     */
    private Date sendTime;

    /**
     * 接收时间
     */
    private Date receiveTime;

    /**
     * 第一次回复的时间
     */
    private Date firstReplyTime;

    /**
     * 最后一次回复的时间
     */
    private Date lastReplyTime;

    /**
     * 回复的内容
     */
    private String content;

    /**
     * 回复的数量
     */
    @ColumnDescription(defaultValue = "0")
    private Integer replyCount;

    /**
     * 发送错误
     */
    private String error;

    public SmsReceipt()
    {
    }

    public Long getReceiptId()
    {
        return receiptId;
    }

    public void setReceiptId(Long receiptId)
    {
        this.receiptId = receiptId;
    }

    public Long getSmsId()
    {
        return smsId;
    }

    public void setSmsId(Long smsId)
    {
        this.smsId = smsId;
    }

    public String getLogId()
    {
        return logId;
    }

    public void setLogId(String logId)
    {
        this.logId = logId;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getUserName()
    {
        return userName;
    }

    public Date getSendTime()
    {
        return sendTime;
    }

    public void setSendTime(Date sendTime)
    {
        this.sendTime = sendTime;
    }

    public Date getReceiveTime()
    {
        return receiveTime;
    }

    public void setReceiveTime(Date receiveTime)
    {
        this.receiveTime = receiveTime;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public Date getFirstReplyTime()
    {
        return firstReplyTime;
    }

    public void setFirstReplyTime(Date firstReplyTime)
    {
        this.firstReplyTime = firstReplyTime;
    }

    public Date getLastReplyTime()
    {
        return lastReplyTime;
    }

    public void setLastReplyTime(Date lastReplyTime)
    {
        this.lastReplyTime = lastReplyTime;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public Sms getSms()
    {
        return sms;
    }

    public void setSms(Sms sms)
    {
        this.sms = sms;
    }

    public SmsLog getSmsLog()
    {
        return smsLog;
    }

    public void setSmsLog(SmsLog smsLog)
    {
        this.smsLog = smsLog;
    }

    public Integer getReplyCount()
    {
        return replyCount;
    }

    public void setReplyCount(Integer replyCount)
    {
        this.replyCount = replyCount;
    }

    public String getError()
    {
        return error;
    }

    public void setError(String error)
    {
        this.error = error;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof SmsReceipt))
            return false;

        SmsReceipt that = (SmsReceipt) o;

        return receiptId.equals(that.receiptId);
    }

    @Override
    public int hashCode()
    {
        return receiptId.hashCode();
    }
}
