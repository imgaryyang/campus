package com.gzzm.ods.in;

import com.gzzm.in.AttachmentInfo;

import java.util.*;

/**
 * @author camel
 * @date 2016/12/3
 */
public class DocumentInfo
{
    private String title;

    private String sourceDept;

    private String sendNumber;

    private String subject;

    private String secret;

    private String priority;

    private Date sendTime;

    private String textType;

    private List<AttachmentInfo> attachments;

    public DocumentInfo()
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

    public String getSourceDept()
    {
        return sourceDept;
    }

    public void setSourceDept(String sourceDept)
    {
        this.sourceDept = sourceDept;
    }

    public String getSendNumber()
    {
        return sendNumber;
    }

    public void setSendNumber(String sendNumber)
    {
        this.sendNumber = sendNumber;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public String getSecret()
    {
        return secret;
    }

    public void setSecret(String secret)
    {
        this.secret = secret;
    }

    public String getPriority()
    {
        return priority;
    }

    public void setPriority(String priority)
    {
        this.priority = priority;
    }

    public Date getSendTime()
    {
        return sendTime;
    }

    public void setSendTime(Date sendTime)
    {
        this.sendTime = sendTime;
    }

    public String getTextType()
    {
        return textType;
    }

    public void setTextType(String textType)
    {
        this.textType = textType;
    }

    public List<AttachmentInfo> getAttachments()
    {
        return attachments;
    }

    public void setAttachments(List<AttachmentInfo> attachments)
    {
        this.attachments = attachments;
    }
}
