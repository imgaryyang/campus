package com.gzzm.oa.mail;

import java.util.Date;

/**
 * 邮件跟踪，跟踪某份邮件的阅读情况
 *
 * @author camel
 * @date 2010-6-19
 */
public class MailTrace
{
    /**
     * 关联的Mail对象，仅对发送给本地用户的邮件有效
     */
    private Mail mail;

    /**
     * 关联MailSmtpRecord对象，仅对发送给外部用户的邮件有效，mail属性record属性互斥
     */
    private MailSmtpRecord record;

    public MailTrace(Mail mail)
    {
        this.mail = mail;
    }

    public MailTrace(MailSmtpRecord record)
    {
        this.record = record;
    }

    public String getKey()
    {
        if (mail != null)
            return "local_" + mail.getMailId();
        else
            return "remote_" + record.getRecordId();
    }

    public boolean isLocal()
    {
        return mail != null;
    }

    public String getReceiver()
    {
        if (mail != null)
        {
            return mail.getUser().getUserName();
        }
        else
        {
            return record.getMailTo();
        }
    }

    public MailState getState()
    {
        if (mail != null)
        {
            return mail.getState();
        }
        else
        {
            return record.getState();
        }
    }

    public Date getReadTime()
    {
        MailState state = getState();
        if (state == MailState.notSended || state == MailState.sended)
            return null;

        if (mail != null)
        {
            return mail.getReadTime();
        }
        else
        {
            return record.getReadTime();
        }
    }

    public String getError()
    {
        if (record != null && getState() == MailState.notSended)
            return record.getError();

        return null;
    }
}
