package com.gzzm.im.entitys;

import com.gzzm.platform.organ.User;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

import java.util.List;

/**
 * 好友分组
 *
 * @author camel
 * @date 2010-12-30
 */
@Entity(table = "IMFRIENDGROUP", keys = "groupId")
public class FriendGroup
{
    /**
     * 群ID
     */
    @Generatable(length = 10)
    private Long groupId;

    @Require
    @Unique(with = "userId")
    private String groupName;

    /**
     * 组所属的用户
     */
    @Index
    private Integer userId;

    /**
     * 关联user对象
     */
    private User user;

    /**
     * 排序ID
     */
    private Integer orderId;

    @OneToMany
    @NotSerialized
    private List<Friend> friends;

    @Lazy(false)
    @ComputeColumn("count(friends)")
    private Integer friendCount;

    public FriendGroup()
    {
    }

    public FriendGroup(Long groupId, String groupName)
    {
        this.groupId = groupId;
        this.groupName = groupName;
    }

    public Long getGroupId()
    {
        return groupId;
    }

    public void setGroupId(Long groupId)
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

    public List<Friend> getFriends()
    {
        return friends;
    }

    public void setFriends(List<Friend> friends)
    {
        this.friends = friends;
    }

    public Integer getFriendCount()
    {
        return friendCount;
    }

    public void setFriendCount(Integer friendCount)
    {
        this.friendCount = friendCount;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof FriendGroup))
            return false;

        FriendGroup that = (FriendGroup) o;

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
