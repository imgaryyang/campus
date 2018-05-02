package com.gzzm.oa.mail;

import net.cyan.thunwind.annotation.*;


/**
 * 邮件关联，记录此邮件相关发件人和收件人
 *
 * @author camel
 * @date 2010-6-16
 */
@Entity(table = "OAMAILRELATIONSHIP", keys = {"mailId", "refAddress"})
public class MailRelationShip
{
    private Long mailId;

    @ColumnDescription(type = "varchar(250)")
    private String refAddress;

    @Cascade
    private Mail mail;

    @Index
    @ColumnDescription(type = "number(9)", nullable = false)
    private Integer userId;

    public MailRelationShip()
    {
    }

    public Long getMailId()
    {
        return mailId;
    }

    public void setMailId(Long mailId)
    {
        this.mailId = mailId;
    }

    public String getRefAddress()
    {
        return refAddress;
    }

    public void setRefAddress(String refAddress)
    {
        this.refAddress = refAddress;
    }

    public Mail getMail()
    {
        return mail;
    }

    public void setMail(Mail mail)
    {
        this.mail = mail;
    }

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }
}
