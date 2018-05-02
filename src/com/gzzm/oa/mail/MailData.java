package com.gzzm.oa.mail;

import net.cyan.thunwind.annotation.Entity;

import java.io.InputStream;

/**
 * 外部邮件保留邮件的原始内容，以备查
 *
 * @author camel
 * @date 2015/1/23
 */
@Entity(table = "OAMAILDATA", keys = "bodyId")
public class MailData
{
    private Long bodyId;

    private MailBody body;

    private InputStream data;

    public MailData()
    {
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

    public InputStream getData()
    {
        return data;
    }

    public void setData(InputStream data)
    {
        this.data = data;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof MailData))
            return false;

        MailData mailData = (MailData) o;

        return bodyId.equals(mailData.bodyId);
    }

    @Override
    public int hashCode()
    {
        return bodyId.hashCode();
    }
}
