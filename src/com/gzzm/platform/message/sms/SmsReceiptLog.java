package com.gzzm.platform.message.sms;

import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * 短消息回复信息日志
 *
 * @author camel
 * @date 2010-6-3
 */
@Entity(table = "PFSMSRECEIPTLOG", keys = "receiptId")
public class SmsReceiptLog
{
    /**
     * 短信回复id，主键，由uuid生成
     */
    @Generatable(name = "uuid", length = 32)
    private String receiptId;

    /**
     * 短信id，关联SmsLog表
     */
    private String smsId;

    private SmsLog sms;

    /**
     * 回复的手机的号码
     */
    private String phone;

    /**
     * 回复的内容
     */
    private String content;

    /**
     * 短信回复时间
     */
    private Date replyTime;

    public SmsReceiptLog()
    {
    }

    public String getReceiptId()
    {
        return receiptId;
    }

    public void setReceiptId(String receiptId)
    {
        this.receiptId = receiptId;
    }

    public String getSmsId()
    {
        return smsId;
    }

    public void setSmsId(String smsId)
    {
        this.smsId = smsId;
    }

    public SmsLog getSms()
    {
        return sms;
    }

    public void setSms(SmsLog sms)
    {
        this.sms = sms;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public Date getReplyTime()
    {
        return replyTime;
    }

    public void setReplyTime(Date replyTime)
    {
        this.replyTime = replyTime;
    }
}
