package com.gzzm.oa.mail;

import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * 邮件Smtp发送记录，记录每个需要通过smtp服务发送出去的邮件
 *
 * @author camel
 * @date 2010-5-27
 */
@Entity(table = "OAMAILSMTPRECORD", keys = "recordId")
public class MailSmtpRecord
{
    /**
     * 记录ID
     */
    @Generatable(length = 13)
    private Long recordId;

    /**
     * 接收者的邮箱，包括邮箱的地址和接收者的名称，完整的格式为"名称"<邮箱地址>
     */
    @ColumnDescription(nullable = false)
    private String mailTo;

    /**
     * 关联的邮件体ID
     */
    @ColumnDescription(nullable = false)
    private Long bodyId;

    /**
     * 关联的邮件体
     */
    @Cascade
    private MailBody body;

    /**
     * 最后一次发送的时间
     */
    private Date sendTime;

    /**
     * 阅读时间
     */
    private Date readTime;

    /**
     * 邮件的状态
     */
    @ColumnDescription(nullable = false, defaultValue = "0")
    private MailState state;

    /**
     * 邮件发送错误信息
     */
    @ColumnDescription(type = "varchar(2000)")
    private String error;

    public MailSmtpRecord()
    {
    }

    public Long getRecordId()
    {
        return recordId;
    }

    public void setRecordId(Long recordId)
    {
        this.recordId = recordId;
    }

    public String getMailTo()
    {
        return mailTo;
    }

    public void setMailTo(String mailTo)
    {
        this.mailTo = mailTo;
    }

    public Long getBodyId()
    {
        return bodyId;
    }

    public void setBodyId(Long bodyId)
    {
        this.bodyId = bodyId;
    }

    public MailBody getBody()
    {
        return body;
    }

    public void setBody(MailBody body)
    {
        this.body = body;
    }

    public Date getSendTime()
    {
        return sendTime;
    }

    public void setSendTime(Date sendTime)
    {
        this.sendTime = sendTime;
    }

    public Date getReadTime()
    {
        return readTime;
    }

    public void setReadTime(Date readTime)
    {
        this.readTime = readTime;
    }

    public MailState getState()
    {
        return state;
    }

    public void setState(MailState state)
    {
        this.state = state;
    }

    public String getError()
    {
        return error;
    }

    public void setError(String error)
    {
        this.error = error;
    }
}
