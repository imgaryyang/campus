package com.gzzm.platform.message;

import com.gzzm.platform.organ.User;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.*;

import java.io.Serializable;
import java.util.Date;

/**
 * @author camel
 * @date 2011-4-25
 */
@Entity(table = "PFIMMESSAGE", keys = "messageId")
@Indexes({@Index(columns = {"USERID", "READED"})})
public class ImMessage implements Serializable
{
    private static final long serialVersionUID = -5124883396843659549L;

    /**
     * 消息ID
     */
    @Generatable(length = 12)
    private Long messageId;

    /**
     * 消息所属的用户
     */
    private Integer userId;

    /**
     * 关联用户对象
     */
    @NotSerialized
    private transient User user;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 发送的时间
     */
    private Date sendTime;

    /**
     * 对应的url，可以为null
     */
    @ColumnDescription(type = "varchar(4000)")
    private String url;

    /**
     * 标识消息是否已读
     */
    private Boolean readed;

    public ImMessage()
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

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
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

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public Boolean getReaded()
    {
        return readed;
    }

    public void setReaded(Boolean readed)
    {
        this.readed = readed;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof ImMessage))
            return false;

        ImMessage imMessage = (ImMessage) o;

        return messageId.equals(imMessage.messageId);
    }

    @Override
    public int hashCode()
    {
        return messageId.hashCode();
    }
}
