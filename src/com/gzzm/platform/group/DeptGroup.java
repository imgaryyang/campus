package com.gzzm.platform.group;

import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

import java.util.List;

/**
 * 部门组
 *
 * @author camel
 * @date 11-9-21
 */
@Entity(table = "PFDEPTGROUP", keys = "groupId")
public class DeptGroup
{
    /**
     * 部门组ID
     */
    @Generatable(length = 7)
    private Integer groupId;

    /**
     * 部门组名称
     */
    @Require
    @Unique(with = "deptId")
    @ColumnDescription(nullable = false)
    private String groupName;

    /**
     * 部门组所属的部门
     */
    @Index
    private Integer deptId;

    /**
     * 关联部门对象
     */
    private Dept dept;

    /**
     * 组成员
     */
    @NotSerialized
    @ManyToMany(table = "PFDEPTGROUPMEMBER")
    @OrderBy(column = "ORDERID")
    private List<Dept> depts;

    /**
     * 创建人ID
     */
    private Integer creator;

    @NotSerialized
    @ToOne("CREATOR")
    private User createUser;

    @ColumnDescription(nullable = false, defaultValue = "0")
    private Boolean self;

    /**
     * 排序
     */
    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    public DeptGroup()
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

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public Dept getDept()
    {
        return dept;
    }

    public void setDept(Dept dept)
    {
        this.dept = dept;
    }

    public List<Dept> getDepts()
    {
        return depts;
    }

    public void setDepts(List<Dept> depts)
    {
        this.depts = depts;
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

    public Boolean getSelf()
    {
        return self;
    }

    public void setSelf(Boolean self)
    {
        this.self = self;
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

        if (!(o instanceof DeptGroup))
            return false;

        DeptGroup deptGroup = (DeptGroup) o;

        return groupId.equals(deptGroup.groupId);
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
