package com.gzzm.im.entitys;

import com.gzzm.im.MessageType;
import com.gzzm.platform.organ.User;
import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * 个人即时消息记录，每条消息复制两份，发送人一份，接收人一份
 *
 * @author camel
 * @date 2010-9-6
 */
@Entity(table = "IMUSERMESSAGE", keys = "messageId")
@Indexes({
        @Index(columns = {"USERID", "READED"}),
        @Index(columns = {"USERID", "RECEIVER"})
})
public class UserMessage
{
    /**
     * 消息ID
     */
    @Generatable(length = 12)
    private Long messageId;

    /**
     * 消息拥有者，每条消息复制两份，发送人一份，接收人一份
     */
    private Integer userId;

    /**
     * 发送人ID
     */
    private Integer sender;

    /**
     * 接收人ID
     */
    private Integer receiver;

    /**
     * 消息内容，如果是文件和图片，则存放文件名
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
     * 关联消息拥有者的user对象
     */
    private User user;

    /**
     * 关联发送人的User对象
     */
    @Lazy(false)
    @ToOne("SENDER")
    private User senderUser;

    @ToOne("RECEIVER")
    private User receiverUser;

    /**
     * 已阅读，false为未阅读，true为已阅读
     */
    @ColumnDescription(defaultValue = "0")
    private Boolean readed;

    /**
     * 信息类型，普通信息，或者手机短信，或者图片，或者文件
     */
    private MessageType type;

    public UserMessage()
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

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

    public Integer getSender()
    {
        return sender;
    }

    public void setSender(Integer sender)
    {
        this.sender = sender;
    }

    public Integer getReceiver()
    {
        return receiver;
    }

    public void setReceiver(Integer receiver)
    {
        this.receiver = receiver;
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

    public Date getSendTime()
    {
        return sendTime;
    }

    public void setSendTime(Date sendTime)
    {
        this.sendTime = sendTime;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public User getSenderUser()
    {
        return senderUser;
    }

    public void setSenderUser(User senderUser)
    {
        this.senderUser = senderUser;
    }

    public User getReceiverUser()
    {
        return receiverUser;
    }

    public void setReceiverUser(User receiverUser)
    {
        this.receiverUser = receiverUser;
    }

    public Boolean isReaded()
    {
        return readed;
    }

    public void setReaded(Boolean readed)
    {
        this.readed = readed;
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

        if (!(o instanceof UserMessage))
            return false;

        UserMessage userMessage = (UserMessage) o;

        return messageId.equals(userMessage.messageId);
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

    public UserMessage cloneMessage()
    {
        UserMessage message = new UserMessage();
        message.content = content;
        message.type = type;
        message.sender = sender;
        message.receiver = receiver;
        message.attachmentId = attachmentId;

        return message;
    }
}
