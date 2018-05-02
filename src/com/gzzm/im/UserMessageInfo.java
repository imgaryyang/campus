package com.gzzm.im;

import java.io.Serializable;
import java.util.Date;

/**
 * 消息信息
 *
 * @author camel
 * @date 2011-2-6
 */
public class UserMessageInfo implements Serializable
{
    private static final long serialVersionUID = 3049020559393172276L;

    private Long messageId;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 附件ID
     */
    private Long attachmentId;

    private String attachmentUUID;

    /**
     * 附件大小
     */
    private Integer fileSize;

    /**
     * 发送者的用户ID
     */
    private Integer sender;

    /**
     * 发送者的用户名
     */
    private String senderName;

    /**
     * 发送的时间
     */
    private Date time;

    /**
     * 信息类型
     */
    private MessageType type;

    /**
     * 是否已读
     */
    private Boolean readed;

    public UserMessageInfo()
    {
    }

    public Long getMessageId()
    {
        return messageId;
    }

    public void setMessageId(Long messageId)
    {
        this.messageId = messageId;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public Long getAttachmentId()
    {
        return attachmentId;
    }

    public void setAttachmentId(Long attachmentId)
    {
        this.attachmentId = attachmentId;
    }

    public String getAttachmentUUID()
    {
        return attachmentUUID;
    }

    public void setAttachmentUUID(String attachmentUUID)
    {
        this.attachmentUUID = attachmentUUID;
    }

    public Integer getFileSize()
    {
        return fileSize;
    }

    public void setFileSize(Integer fileSize)
    {
        this.fileSize = fileSize;
    }

    public Integer getSender()
    {
        return sender;
    }

    public void setSender(Integer sender)
    {
        this.sender = sender;
    }

    public String getSenderName()
    {
        return senderName;
    }

    public void setSenderName(String senderName)
    {
        this.senderName = senderName;
    }

    public Date getTime()
    {
        return time;
    }

    public void setTime(Date time)
    {
        this.time = time;
    }

    public MessageType getType()
    {
        return type;
    }

    public void setType(MessageType type)
    {
        if (type == null)
            type = MessageType.text;

        this.type = type;
    }

    public Boolean getReaded() {
        return readed;
    }

    public void setReaded(Boolean readed) {
        this.readed = readed;
    }
}
