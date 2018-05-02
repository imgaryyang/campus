package com.gzzm.oa.mail;

import com.gzzm.platform.organ.User;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * 邮件基本信息，每一条记录代表一个邮件
 *
 * @author camel
 * @date 2010-3-15
 */
@Entity(table = "OAMAIL", keys = "mailId")
@Indexes({
        @Index(columns = {"SENDTIME", "NOTIFIED"})
})
public class Mail
{
    @Generatable(length = 13)
    private Long mailId;

    /**
     * 用户ID，邮件拥有者的ID
     */
    @Index
    private Integer userId;

    /**
     * 关联用户表
     */
    @ToOne("USERID")
    @NotSerialized
    private User user;

    /**
     * 邮件标题
     */
    @ColumnDescription(type = "varchar(250)", nullable = false)
    private String title;

    /**
     * 收件时间
     */
    private Date acceptTime;

    /**
     * 发件时间
     */
    private Date sendTime;

    /**
     * 阅读时间
     */
    private Date readTime;

    /**
     * 发件人
     */
    @ColumnDescription(type = "varchar(500)")
    private String mailFrom;

    /**
     * 发件人的id，仅对系统内部邮件有效
     */
    private Integer sender;

    /**
     * 关联发件人
     */
    @ToOne("SENDER")
    @NotSerialized
    private User senderUser;

    /**
     * 草稿，收件箱，或者发件箱
     */
    private MailType type;

    /**
     * 关联邮件内容，即MailBody
     */
    private Long bodyId;

    /**
     * 关联邮件内容，邮件内容被多份邮件共享
     */
    @NotSerialized
    private MailBody body;

    /**
     * 邮件大小,包括所有附件和内容的大小
     */
    private Long mailSize;

    /**
     * pop3地址
     */
    private String pop3;

    /**
     * 邮件唯一编号，当从pop3服务器接收邮件时记录pop3服务器上的uidl
     */
    private String uidl;

    /**
     * 是否已经被删除
     */
    @ColumnDescription(defaultValue = "0")
    private Boolean deleted;

    /**
     * 所属的目录的ID，如果不属于任何目录则为null
     */
    private Long catalogId;

    /**
     * 关联所属的目录对象，如果不属于任何目录为null
     */
    @NotSerialized
    private MailCatalog catalog;

    /**
     * 标记Id
     */
    private Long markId;

    /**
     * 关联所属的标记对象，如果没有进行标记，标记为null
     */
    @NotSerialized
    private MailMark mark;

    /**
     * 是否紧急
     */
    private Boolean urgent;

    /**
     * 是否已经回复
     */
    private Boolean replyed;

    /**
     * 是否包含附件
     */
    private Boolean attachment;

    /**
     * smtp发送状态
     */
    private SmtpSendState smtpSendState;

    /**
     * 最后一次smtp发送错误的时间
     */
    private Date smtpErrorTime;

    private Boolean notified;

    public Mail()
    {
    }

    public Boolean isUrgent()
    {
        return urgent;
    }

    public void setUrgent(Boolean urgent)
    {
        this.urgent = urgent;
    }

    public Long getMailId()
    {
        return mailId;
    }

    public void setMailId(Long mailId)
    {
        this.mailId = mailId;
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

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public Date getAcceptTime()
    {
        return acceptTime;
    }

    public void setAcceptTime(Date acceptTime)
    {
        this.acceptTime = acceptTime;
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

    public String getMailFrom()
    {
        return mailFrom;
    }

    public void setMailFrom(String mailFrom)
    {
        this.mailFrom = mailFrom;
    }

    public Integer getSender()
    {
        return sender;
    }

    public void setSender(Integer sender)
    {
        this.sender = sender;
    }

    public User getSenderUser()
    {
        return senderUser;
    }

    public void setSenderUser(User senderUser)
    {
        this.senderUser = senderUser;
    }

    public MailType getType()
    {
        return type;
    }

    public void setType(MailType type)
    {
        this.type = type;
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

    public Long getMailSize()
    {
        return mailSize;
    }

    public void setMailSize(Long mailSize)
    {
        this.mailSize = mailSize;
    }

    public String getPop3()
    {
        return pop3;
    }

    public void setPop3(String pop3)
    {
        this.pop3 = pop3;
    }

    public String getUidl()
    {
        return uidl;
    }

    public void setUidl(String uidl)
    {
        this.uidl = uidl;
    }

    public Boolean isDeleted()
    {
        return deleted;
    }

    public void setDeleted(Boolean deleted)
    {
        this.deleted = deleted;
    }

    public Long getCatalogId()
    {
        return catalogId;
    }

    public void setCatalogId(Long catalogId)
    {
        this.catalogId = catalogId;
    }

    public MailCatalog getCatalog()
    {
        return catalog;
    }

    public void setCatalog(MailCatalog catalog)
    {
        this.catalog = catalog;
    }

    public Long getMarkId()
    {
        return markId;
    }

    public void setMarkId(Long markId)
    {
        this.markId = markId;
    }

    public MailMark getMark()
    {
        return mark;
    }

    public void setMark(MailMark mark)
    {
        this.mark = mark;
    }

    public Boolean isAttachment()
    {
        return attachment;
    }

    public void setAttachment(Boolean attachment)
    {
        this.attachment = attachment;
    }

    public SmtpSendState getSmtpSendState()
    {
        return smtpSendState;
    }

    public void setSmtpSendState(SmtpSendState smtpSendState)
    {
        this.smtpSendState = smtpSendState;
    }

    public Date getSmtpErrorTime()
    {
        return smtpErrorTime;
    }

    public void setSmtpErrorTime(Date smtpErrorTime)
    {
        this.smtpErrorTime = smtpErrorTime;
    }

    public Boolean getNotified()
    {
        return notified;
    }

    public void setNotified(Boolean notified)
    {
        this.notified = notified;
    }

    /**
     * 发件人的名字
     *
     * @return 发送人的名称，如果是本地用户则显示用户及所属的部门名称，如果是异地用户，则直接显示异地用户的姓名
     */
    @NotSerialized
    public String getSenderName()
    {
        User user = getSenderUser();
        if (user != null)
        {
            //发件人为本地用户
            return user.getUserName();
        }
        else if (mailFrom != null)
        {
            String s = mailFrom;
            int index = s.indexOf("<");
            if (index >= 0 && s.endsWith(">"))
                s = s.substring(0, index);

            if (s.startsWith("\"") || s.startsWith("\'"))
                s = s.substring(1);

            if (s.endsWith("\"") || s.endsWith("\'"))
                s = s.substring(0, s.length() - 1);

            return s;
        }

        return "";
    }

    public String getFrom()
    {
        return MailUtils.getFrom(this);
    }

    public Boolean isReplyed()
    {
        return replyed;
    }

    public void setReplyed(Boolean replyed)
    {
        this.replyed = replyed;
    }

    public MailState getState()
    {
        if (replyed != null && replyed)
            return MailState.replyed;
        else if (readTime != null)
            return MailState.readed;
        else
            return MailState.sended;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof Mail))
            return false;

        Mail mail = (Mail) o;

        return mailId.equals(mail.mailId);
    }

    @Override
    public int hashCode()
    {
        return mailId.hashCode();
    }
}
