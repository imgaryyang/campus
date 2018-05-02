package com.gzzm.platform.group;

import com.gzzm.platform.organ.User;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

import java.util.List;

/**
 * 用户组，将多个用户组成一组
 *
 * @author camel
 * @date 2010-8-26
 */
@Entity(table = "PFUSERGROUP", keys = "groupId")
public class UserGroup
{
    /**
     * 用户组ID
     */
    @Generatable(length = 9)
    private Integer groupId;

    /**
     * 用户组名称
     */
    @Require
    @Unique(with = {"type", "owner"})
    @ColumnDescription(nullable = false)
    private String groupName;

    /**
     * 用户组类型，标识一个用户组是用户所有还是部门所有
     */
    @ColumnDescription(nullable = false)
    private UserGroupType type;

    /**
     * 拥有者，当type==user时为userId，当type==dept时为deptId
     */
    @Index
    @ColumnDescription(type = "number(9)")
    private Integer owner;

    /**
     * 组成员
     */
    @NotSerialized
    @ManyToMany(table = "PFUSERGROUPMEMBER")
    @OrderBy(column = "ORDERID")
    private List<User> users;

    /**
     * 排序ID
     */
    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    public UserGroup()
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

    public UserGroupType getType()
    {
        return type;
    }

    public void setType(UserGroupType type)
    {
        this.type = type;
    }

    public Integer getOwner()
    {
        return owner;
    }

    public void setOwner(Integer owner)
    {
        this.owner = owner;
    }

    public List<User> getUsers()
    {
        return users;
    }

    public void setUsers(List<User> users)
    {
        this.users = users;
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

        if (!(o instanceof UserGroup))
            return false;

        UserGroup userGroup = (UserGroup) o;

        return groupId.equals(userGroup.groupId);
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