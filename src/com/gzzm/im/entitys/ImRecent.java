package com.gzzm.im.entitys;

import com.gzzm.platform.organ.User;
import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * @author camel
 * @date 2017/2/22
 */
@Entity(table = "IMRECENT", keys = {"userId", "type", "targetId"})
public class ImRecent
{
    /**
     * 用户ID，表示这是属于哪个用户的最新联系人
     */
    private Integer userId;

    private User user;

    /**
     * 关联的最新联系人的类型，目前包括用户和群
     */
    private RecentType type;

    /**
     * 关联的最新联系人的ID，关联用户的userId或者关联群的groupId
     */
    @ColumnDescription(type = "number(10)")
    private Integer targetId;

    /**
     * 最后联系的时间
     */
    private Date lastTime;

    public ImRecent()
    {
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

    public RecentType getType()
    {
        return type;
    }

    public void setType(RecentType type)
    {
        this.type = type;
    }

    public Integer getTargetId()
    {
        return targetId;
    }

    public void setTargetId(Integer targetId)
    {
        this.targetId = targetId;
    }

    public Date getLastTime()
    {
        return lastTime;
    }

    public void setLastTime(Date lastTime)
    {
        this.lastTime = lastTime;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof ImRecent))
            return false;

        ImRecent imRecent = (ImRecent) o;

        return targetId.equals(imRecent.targetId) && type == imRecent.type && userId.equals(imRecent.userId);
    }

    @Override
    public int hashCode()
    {
        int result = userId.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + targetId.hashCode();
        return result;
    }
}