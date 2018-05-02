package com.gzzm.platform.desktop;

import com.gzzm.platform.organ.User;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

/**
 * @author camel
 * @date 2016/9/23
 */
@Entity(table = "PFUSERLINKGROUP", keys = "groupId")
public class UserLinkGroup
{
    @Generatable(length = 8)
    private Integer groupId;

    @Unique(with = "userId")
    @Require
    @ColumnDescription(type = "varchar(250)", nullable = false)
    private String groupName;

    private Integer userId;

    @NotSerialized
    private User user;

    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    public UserLinkGroup()
    {
    }

    public Integer getGroupId()
    {
        return groupId;
    }

    public void setGroupId(Integer groupId)
    {
        this.groupId = groupId;
    }

    public String getGroupName()
    {
        return groupName;
    }

    public void setGroupName(String groupName)
    {
        this.groupName = groupName;
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

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof UserLinkGroup))
            return false;

        UserLinkGroup that = (UserLinkGroup) o;

        return groupId.equals(that.groupId);
    }

    @Override
    public int hashCode()
    {
        return groupId.hashCode();
    }

    @Override
    public String toString()
    {
        return groupName;
    }
}
