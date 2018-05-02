package com.gzzm.im.entitys;

import com.gzzm.platform.organ.User;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.*;

/**
 * 即时消息好友
 *
 * @author camel
 * @date 2010-9-7
 */
@Entity(table = "IMFRIEND", keys = "friendId")
public class Friend
{
    /**
     * 好友ID
     */
    @Generatable(length = 11)
    private Long friendId;

    /**
     * 用户ID
     */
    @Index
    private Integer userId;

    /**
     * 好友的用户ID
     */
    @Index
    private Integer friendUserId;

    @Lazy(false)
    @ToOne("USERID")
    @NotSerialized
    private User user;

    @ToOne("FRIENDUSERID")
    @Lazy(false)
    @NotSerialized
    private User friendUser;

    @ToOne("FRIENDUSERID")
    @NotSerialized
    private ImUserConfig config;

    /**
     * 所属分组ID
     */
    private Long groupId;

    /**
     * 关联所属分组
     */
    @NotSerialized
    private FriendGroup friendGroup;

    @ColumnDescription(nullable = false, defaultValue = "0")
    private Boolean confirmed;

    public Friend()
    {
    }

    public Long getFriendId()
    {
        return friendId;
    }

    public void setFriendId(Long friendId)
    {
        this.friendId = friendId;
    }

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

    public Integer getFriendUserId()
    {
        return friendUserId;
    }

    public void setFriendUserId(Integer friendUserId)
    {
        this.friendUserId = friendUserId;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public User getFriendUser()
    {
        return friendUser;
    }

    public void setFriendUser(User friendUser)
    {
        this.friendUser = friendUser;
    }

    public ImUserConfig getConfig()
    {
        return config;
    }

    public void setConfig(ImUserConfig config)
    {
        this.config = config;
    }

    public Long getGroupId()
    {
        return groupId;
    }

    public void setGroupId(Long groupId)
    {
        this.groupId = groupId;
    }

    public FriendGroup getFriendGroup()
    {
        return friendGroup;
    }

    public void setFriendGroup(FriendGroup friendGroup)
    {
        this.friendGroup = friendGroup;
    }

    public Boolean getConfirmed()
    {
        return confirmed;
    }

    public void setConfirmed(Boolean confirmed)
    {
        this.confirmed = confirmed;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof Friend))
            return false;

        Friend friend = (Friend) o;

        return friendUserId.equals(friend.friendUserId) && userId.equals(friend.userId);
    }

    @Override
    public int hashCode()
    {
        int result = userId.hashCode();
        result = 31 * result + friendUserId.hashCode();
        return result;
    }
}
