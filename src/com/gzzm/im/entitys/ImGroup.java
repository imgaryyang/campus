package com.gzzm.im.entitys;

import com.gzzm.platform.organ.User;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

import java.util.*;

/**
 * 即时消息群
 *
 * @author camel
 * @date 2010-9-6
 */
@Entity(table = "IMGROUP", keys = "groupId")
public class ImGroup
{
    /**
     * 群ID
     */
    @Generatable(length = 6)
    private Integer groupId;

    /**
     * 群名
     */
    @Require
    @Unique(with = "creator")
    @ColumnDescription(type = "varchar(250)")
    private String groupName;

    /**
     * 创建人用户ID
     */
    @Index
    private Integer creator;

    /**
     * 关联创建人的用户对象
     */
    @ToOne("CREATOR")
    private User createUser;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 群成员
     */
    @NotSerialized
    @OneToMany
    private List<GroupMember> members;

    public ImGroup()
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

    public Integer getCreator()
    {
        return creator;
    }

    public void setCreator(Integer creator)
    {
        this.creator = creator;
    }

    public User getCreateUser()
    {
        return createUser;
    }

    public void setCreateUser(User createUser)
    {
        this.createUser = createUser;
    }

    public Date getCreatedTime()
    {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime)
    {
        this.createdTime = createdTime;
    }

    @OneToMany
    public List<GroupMember> getMembers()
    {
        return members;
    }

    public void setMembers(List<GroupMember> members)
    {
        this.members = members;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof ImGroup))
            return false;

        ImGroup imGroup = (ImGroup) o;

        return groupId.equals(imGroup.groupId);
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
