package com.gzzm.im.entitys;

import com.gzzm.platform.organ.User;
import net.cyan.thunwind.annotation.*;

/**
 * 记录每个用户对群消息的阅读情况，仅当用户加入群后，群消息才发给此用户，否则在此表没有记录
 *
 * @author camel
 * @date 2010-12-14
 */
@Entity(table = "IMGROUPMESSAGERECORD", keys = {"messageId", "userId"})
@Indexes({@Index(columns = {"USERID", "READED"})})
public class GroupMessageRecord
{
    /**
     * 消息ID
     */
    private Long messageId;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 关联用户对象
     */
    private User user;

    /**
     * 关联消息对象
     */
    private GroupMessage message;

    /**
     * 是否已经阅读，false为未阅读，true为已阅读
     */
    private Boolean readed;

    public GroupMessageRecord()
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

    public GroupMessage getMessage()
    {
        return message;
    }

    public void setMessage(GroupMessage message)
    {
        this.message = message;
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

        if (!(o instanceof GroupMessageRecord))
            return false;

        GroupMessageRecord that = (GroupMessageRecord) o;

        return messageId.equals(that.messageId) && userId.equals(that.userId);
    }

    @Override
    public int hashCode()
    {
        int result = messageId.hashCode();
        result = 31 * result + userId.hashCode();
        return result;
    }
}
