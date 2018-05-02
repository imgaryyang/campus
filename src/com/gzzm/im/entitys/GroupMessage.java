package com.gzzm.im.entitys;

import com.gzzm.im.*;
import com.gzzm.platform.organ.User;
import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * 群即时消息记录
 *
 * @author camel
 * @date 2010-9-6
 */
@Entity(table = "IMGROUPMESSAGE", keys = "messageId")
public class GroupMessage
{
    /**
     * 消息ID
     */
    @Generatable(length = 12)
    private Long messageId;

    /**
     * 群ID
     */
    @Index
    private Integer groupId;

    /**
     * 发送人ID
     */
    private Integer sender;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 附件ID，仅当文件和图片时有效
     */
    @ColumnDescription(type = "number(12)")
    private Long attachmentId;

    /**
     * 消息发送时间
     */
    private Date sendTime;

    /**
     * 关联消息所属群的imGroup对象
     */
    @Cascade
    private ImGroup imGroup;

    /**
     * 关联发送人的User对象
     */
    @ToOne("SENDER")
    private User senderUser;

    /**
     * 信息类型，普通信息，或者手机短信，或者图片，或者文件
     */
    private MessageType type;

    /**
     * 操作类型，用于在接收群信息的时候作为要不要刷新群的依据
     */
    private OperationType operationType;

    public GroupMessage()
    {
    }

    public OperationType getOperationType()
    {
        return operationType;
    }

    public void setOperationType(OperationType operationType)
    {
        this.operationType = operationType;
    }

    public Long getAttachmentId()
    {
        return attachmentId;
    }

    public void setAttachmentId(Long attachmentId)
    {
        this.attachmentId = attachmentId;
    }

    public Long getMessageId()
    {
        return messageId;
    }

    public void setMessageId(Long messageId)
    {
        this.messageId = messageId;
    }

    public Integer getGroupId()
    {
        return groupId;
    }

    public void setGroupId(Integer groupId)
    {
        this.groupId = groupId;
    }

    public Integer getSender()
    {
        return sender;
    }

    public void setSender(Integer sender)
    {
        this.sender = sender;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public Date getSendTime()
    {
        return sendTime;
    }

    public void setSendTime(Date sendTime)
    {
        this.sendTime = sendTime;
    }

    public ImGroup getImGroup()
    {
        return imGroup;
    }

    public void setImGroup(ImGroup imGroup)
    {
        this.imGroup = imGroup;
    }

    public User getSenderUser()
    {
        return senderUser;
    }

    public void setSenderUser(User senderUser)
    {
        this.senderUser = senderUser;
    }

    public MessageType getType()
    {
        return type;
    }

    public void setType(MessageType type)
    {
        this.type = type;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof GroupMessage))
            return false;

        GroupMessage groupMessage = (GroupMessage) o;

        return messageId.equals(groupMessage.messageId);
    }

    @Override
    public int hashCode()
    {
        return messageId.hashCode();
    }

    @Override
    public String toString()
    {
        return content;
    }
}