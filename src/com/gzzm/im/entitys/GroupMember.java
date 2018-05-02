package com.gzzm.im.entitys;


import com.gzzm.platform.organ.User;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.*;

/**
 * 群成员
 *
 * @author camel
 * @date 2010-9-6
 */
@Entity(table = "IMGROUPMEMBER", keys = {"groupId", "userId"})
public class GroupMember
{
    /**
     * 群ID
     */
    private Integer groupId;

    /**
     * 用户ID
     */
    @Index
    private Integer userId;

    /**
     * 是否为管理员
     */
    private Boolean groupAdmin;

    /**
     * 关联群对象
     */
    @Cascade
    @ToOne("GROUPID")
    private ImGroup imGroup;

    /**
     * 关联用户对象
     */
    @ToOne("USERID")
    @Lazy(false)
    @NotSerialized
    private User user;

    public GroupMember()
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

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

    public Boolean isGroupAdmin()
    {
        return groupAdmin;
    }

    public void setGroupAdmin(Boolean groupAdmin)
    {
        this.groupAdmin = groupAdmin;
    }

    public ImGroup getImGroup()
    {
        return imGroup;
    }

    public void setImGroup(ImGroup imGroup)
    {
        this.imGroup = imGroup;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof GroupMember))
            return false;

        GroupMember that = (GroupMember) o;

        return groupId.equals(that.groupId) && userId.equals(that.userId);
    }

    @Override
    public int hashCode()
    {
        int result = groupId.hashCode();
        result = 31 * result + userId.hashCode();
        return result;
    }
}
