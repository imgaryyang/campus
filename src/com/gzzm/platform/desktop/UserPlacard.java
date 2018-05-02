package com.gzzm.platform.desktop;

import com.gzzm.platform.organ.User;
import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * 发送给某个用户的公告
 *
 * @author camel
 * @date 2010-6-3
 */
@Entity(table = "PFUSERPLACARD", keys = "placardId")
public class UserPlacard
{
    @Generatable(name = "uuid", length = 32)
    private String placardId;

    private Integer userId;

    private User user;

    @ColumnDescription(nullable = false, type = "varchar(4000)")
    private String content;

    private Date sendTime;

    private Date timeout;

    public UserPlacard()
    {
    }

    public String getPlacardId()
    {
        return placardId;
    }

    public void setPlacardId(String placardId)
    {
        this.placardId = placardId;
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

    public Date getTimeout()
    {
        return timeout;
    }

    public void setTimeout(Date timeout)
    {
        this.timeout = timeout;
    }
}
