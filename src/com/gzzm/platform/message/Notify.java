package com.gzzm.platform.message;

import com.gzzm.platform.organ.User;
import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * @author camel
 * @date 12-12-27
 */
@Entity(table = "PFNOTIFY", keys = "notifyId")
public class Notify
{
    @Generatable(length = 7)
    private Integer notifyId;

    private String content;

    private Integer userId;

    private User user;

    private Date sendTime;

    public Notify()
    {
    }

    public Integer getNotifyId()
    {
        return notifyId;
    }

    public void setNotifyId(Integer notifyId)
    {
        this.notifyId = notifyId;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
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

    public Date getSendTime()
    {
        return sendTime;
    }

    public void setSendTime(Date sendTime)
    {
        this.sendTime = sendTime;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof Notify))
            return false;

        Notify notify = (Notify) o;

        return notifyId.equals(notify.notifyId);
    }

    @Override
    public int hashCode()
    {
        return notifyId.hashCode();
    }
}
