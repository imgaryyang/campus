package com.gzzm.oa.mail;

import com.gzzm.platform.attachment.Attachment;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.organ.User;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.ext.TextExtractors;
import net.cyan.commons.util.*;
import net.cyan.thunwind.annotation.*;

import java.util.*;

/**
 * 邮件内容表，当一份邮件发给多个人是，发件人和收件人的邮件内容是相同的，因此共用一份数据，
 * 即MailBody和MailItem是一对多关心
 *
 * @author camel
 * @date 2010-3-15
 * @see com.gzzm.oa.mail.Mail#body
 */
@Entity(table = "OAMAILBODY", keys = "bodyId")
public class MailBody
{
    @Generatable(length = 12)
    private Long bodyId;

    /**
     * 邮件标题d
     */
    @ColumnDescription(type = "varchar(250)", nullable = false)
    private String title;

    /**
     * 邮件正文内容
     */
    @NotSerialized
    private char[] content;

    /**
     * 消息体的contentType
     */
    @ColumnDescription(type = "varchar(50)")
    private String contentType;

    /**
     * 消息的唯一id
     */
    @Index
    @ColumnDescription(type = "varchar(250)")
    private String messageId;

    /**
     * 发件人
     */
    @ColumnDescription(type = "varchar(250)")
    private String mailFrom;

    /**
     * 收件人
     */
    @ColumnDescription(type = "varchar(4000)")
    private String mailTo;

    /**
     * 抄送
     */
    @ColumnDescription(type = "varchar(4000)")
    private String cc;

    /**
     * 密送
     */
    @ColumnDescription(type = "varchar(4000)")
    private String sc;

    /**
     * 邮件大小
     */
    @ColumnDescription(type = "number(18)")
    private Long mailSize;

    /**
     * 是否紧急
     */
    private Boolean urgent;

    /**
     * 附件列表ID，关联PFATTACHMENT表
     *
     * @see com.gzzm.platform.attachment.Attachment
     */
    private Long attachmentId;

    /**
     * 附件列表
     *
     * @see com.gzzm.platform.attachment.Attachment
     */
    @EntityList(column = "ATTACHMENTID")
    private SortedSet<Attachment> attachments;

    /**
     * 邮件创建人，仅对从本系统发出的邮件有效
     */
    private Integer creator;

    /**
     * 邮件创建人对应的User对象
     */
    @NotSerialized
    @ToOne("CREATOR")
    private User createUser;

    /**
     * 发送邮件的帐号，仅使用外部帐号发送邮件时有效，
     */
    private Integer accountId;

    /**
     * 关联MailAccount对象
     */
    @NotSerialized
    private MailAccount account;

    /**
     * 邮件发送时间
     */
    private Date sendTime;

    @NotSerialized
    @OneToMany
    private List<Mail> mails;

    /**
     * 关联的邮件的ID
     */
    @Index
    @ColumnDescription(type = "number(13)")
    private Long referenceId;

    /**
     * 关联的邮件体的ID
     */
    @Index
    @ColumnDescription(type = "number(12)")
    private Long refBodyId;

    /**
     * 邮件体中的references属性
     */
    @ColumnDescription(type = "varchar(4000)")
    private String mailReferences;

    /**
     * 邮件体中的inReplyTo属性
     */
    @ColumnDescription(type = "varchar(250)")
    private String inReplyTo;

    /**
     * 邮件通知的目标，仅对外部邮件有效
     */
    @ColumnDescription(type = "varchar(250)")
    private String notificationTo;

    /**
     * 此邮件是否为回执
     */
    private Boolean receipt;

    /**
     * 邮件通知方式
     */
    @ColumnDescription(nullable = false, defaultValue = "2")
    private MailNotifyType notifyType;

    /**
     * 邮件更新时间
     */
    @IndexTimestamp
    private Date updateTime;

    private Integer signId;

    @NotSerialized
    private MailSign sign;

    public MailBody()
    {
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public Long getBodyId()
    {
        return bodyId;
    }

    public void setBodyId(Long bodyId)
    {
        this.bodyId = bodyId;
    }

    public char[] getContent()
    {
        return content;
    }

    public void setContent(char[] content)
    {
        this.content = content;
    }

    public String getContentType()
    {
        return contentType;
    }

    public void setContentType(String contentType)
    {
        this.contentType = contentType;
    }

    public String getMessageId()
    {
        return messageId;
    }

    public void setMessageId(String messageId)
    {
        this.messageId = messageId;
    }

    public String getMailFrom()
    {
        return mailFrom;
    }

    public void setMailFrom(String mailFrom)
    {
        this.mailFrom = mailFrom;
    }

    public String getMailTo()
    {
        return mailTo;
    }

    public void setMailTo(String mailTo)
    {
        this.mailTo = mailTo;
    }

    public String getCc()
    {
        return cc;
    }

    public void setCc(String cc)
    {
        this.cc = cc;
    }

    public String getSc()
    {
        return sc;
    }

    public void setSc(String sc)
    {
        this.sc = sc;
    }

    public Long getMailSize()
    {
        return mailSize;
    }

    public void setMailSize(Long mailSize)
    {
        this.mailSize = mailSize;
    }

    public Long getAttachmentId()
    {
        return attachmentId;
    }

    public void setAttachmentId(Long attachmentId)
    {
        this.attachmentId = attachmentId;
    }

    public String getAttachmentEncodedId()
    {
        if (attachmentId != null)
            return Attachment.encodeId(attachmentId);

        return null;
    }

    public SortedSet<Attachment> getAttachments()
    {
        return attachments;
    }

    public void setAttachments(SortedSet<Attachment> attachments)
    {
        this.attachments = attachments;
    }

    public Boolean isUrgent()
    {
        return urgent;
    }

    public void setUrgent(Boolean urgent)
    {
        this.urgent = urgent;
    }

    public Integer getCreator()
    {
        return creator;
    }

    public void setCreator(Integer creator)
    {
        this.creator = creator;
    }

    public User getCreateUser()
    {
        return createUser;
    }

    public void setCreateUser(User createUser)
    {
        this.createUser = createUser;
    }

    public String getFrom()
    {
        return MailUtils.getFrom(this);
    }

    public Integer getAccountId()
    {
        return accountId;
    }

    public void setAccountId(Integer accountId)
    {
        this.accountId = accountId;
    }

    public MailAccount getAccount()
    {
        return account;
    }

    public void setAccount(MailAccount account)
    {
        this.account = account;
    }

    public Date getSendTime()
    {
        return sendTime;
    }

    public void setSendTime(Date sendTime)
    {
        this.sendTime = sendTime;
    }

    public Date getUpdateTime()
    {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime)
    {
        this.updateTime = updateTime;
    }

    public List<Mail> getMails()
    {
        return mails;
    }

    public void setMails(List<Mail> mails)
    {
        this.mails = mails;
    }

    public String getMailReferences()
    {
        return mailReferences;
    }

    public void setMailReferences(String mailReferences)
    {
        this.mailReferences = mailReferences;
    }

    public String getInReplyTo()
    {
        return inReplyTo;
    }

    public void setInReplyTo(String inReplyTo)
    {
        this.inReplyTo = inReplyTo;
    }

    public Long getReferenceId()
    {
        return referenceId;
    }

    public void setReferenceId(Long referenceId)
    {
        this.referenceId = referenceId;
    }

    public Long getRefBodyId()
    {
        return refBodyId;
    }

    public void setRefBodyId(Long refBodyId)
    {
        this.refBodyId = refBodyId;
    }

    public String getNotificationTo()
    {
        return notificationTo;
    }

    public void setNotificationTo(String notificationTo)
    {
        this.notificationTo = notificationTo;
    }

    public Boolean isReceipt()
    {
        return receipt;
    }

    public void setReceipt(Boolean receipt)
    {
        this.receipt = receipt;
    }

    public MailNotifyType getNotifyType()
    {
        return notifyType;
    }

    public void setNotifyType(MailNotifyType notifyType)
    {
        this.notifyType = notifyType;
    }

    public Integer getSignId()
    {
        return signId;
    }

    public void setSignId(Integer signId)
    {
        this.signId = signId;
    }

    public MailSign getSign()
    {
        return sign;
    }

    public void setSign(MailSign sign)
    {
        this.sign = sign;
    }

    @FullText
    @NotSerialized
    public String getText()
    {
        StringBuilder buffer = new StringBuilder();

        if (getTitle() != null)
        {
            buffer.append(getTitle());
        }

        if (getContent() != null)
            buffer.append(" ").append(HtmlUtils.getPlainText(new String(getContent())));

        if (getAttachmentId() != null)
        {
            for (Attachment attachment : attachments)
            {
                try
                {
                    Inputable content = attachment.getInputable();
                    if (content != null)
                    {
                        String s = TextExtractors.extract(content.getInputStream(),
                                IOUtils.getExtName(attachment.getFileName()));

                        if (!StringUtils.isEmpty(s))
                            buffer.append(" ").append(s);
                    }
                }
                catch (Throwable ex)
                {
                    Tools.log(ex);
                }
            }
        }

        return buffer.toString();
    }

    public String getTopReference()
    {
        if (mailReferences == null)
            return messageId;

        String[] ss = mailReferences.split(" ");
        if (ss.length > 0)
        {
            String s = ss[0];
            if (s.startsWith("<") && s.endsWith(">"))
                s = s.substring(1, s.length() - 1);

            return s;
        }

        return null;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof MailBody))
            return false;

        MailBody mailBody = (MailBody) o;

        return bodyId.equals(mailBody.bodyId);
    }

    @Override
    public int hashCode()
    {
        return bodyId.hashCode();
    }
}
