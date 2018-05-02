package com.gzzm.platform.organ;

import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

import java.util.List;

/**
 * 角色
 *
 * @author camel
 * @date 2009-7-18
 */
@Entity(table = "PFROLE", keys = "roleId")
public class Role
{
    /**
     * 角色id，长度为8,前2位为系统id，后6位为序列号
     */
    @Generatable(length = 8)
    private Integer roleId;

    /**
     * 角色名称
     */
    @Require
    @Unique(with = "deptId")
    @ColumnDescription(type = "varchar(250)", nullable = false)
    private String roleName;

    /**
     * 角色所属的部门的id
     */
    @Index
    private Integer deptId;

    /**
     * 角色所属的部门
     */
    @NotSerialized
    private Dept dept;

    /**
     * 角色说明
     */
    @ColumnDescription(type = "varchar(250)")
    private String remark;

    /**
     * 排序号，用于展示时将岗位排序，只对属于同一个部门的岗位排序
     */
    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    /**
     * 角色权限关系
     */
    @Lazy(false)
    @OneToMany
    private List<RoleApp> roleApps;

    /**
     * 能否被子部门使用
     */
    private Boolean inheritable;

    @ColumnDescription(nullable = false, defaultValue = "1")
    private Boolean selectable;

    /**
     * 角色组或角色，默认为角色，角色组是表示此角色由多个角色组成
     */
    @ColumnDescription(defaultValue = "0")
    private RoleType type;

    /**
     * 角色组包含的角色，type为group时有效
     */
    @Lazy(false)
    @ManyToMany(table = "PFROLEGROUP", joinColumn = "GROUPID", reverseJoinColumn = "ROLEID")
    @NotSerialized
    private List<Role> groupRoles;

    /**
     * 上一级角色的id，即所属目录
     */
    private Integer parentRoleId;

    @ToOne("PARENTROLEID")
    @NotSerialized
    private Role parentRole;

    @NotSerialized
    @OneToMany("PARENTROLEID")
    @OrderBy(column = "ORDERID")
    private List<Role> children;

    public Role()
    {
    }

    public Role(Integer roleId)
    {
        this.roleId = roleId;
    }

    public Integer getRoleId()
    {
        return roleId;
    }

    public void setRoleId(Integer roleId)
    {
        this.roleId = roleId;
    }

    public String getRoleName()
    {
        return roleName;
    }

    public void setRoleName(String roleName)
    {
        this.roleName = roleName;
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

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
    }

    public List<RoleApp> getRoleApps()
    {
        return roleApps;
    }

    public void setRoleApps(List<RoleApp> roleApps)
    {
        this.roleApps = roleApps;
    }

    public Boolean isInheritable()
    {
        return inheritable;
    }

    public void setInheritable(Boolean inheritable)
    {
        this.inheritable = inheritable;
    }

    public Boolean isSelectable()
    {
        return selectable;
    }

    public void setSelectable(Boolean selectable)
    {
        this.selectable = selectable;
    }

    public RoleType getType()
    {
        return type;
    }

    public void setType(RoleType type)
    {
        this.type = type;
    }

    public List<Role> getGroupRoles()
    {
        return groupRoles;
    }

    public void setGroupRoles(List<Role> groupRoles)
    {
        this.groupRoles = groupRoles;
    }

    public Integer getParentRoleId()
    {
        return parentRoleId;
    }

    public void setParentRoleId(Integer parentRoleId)
    {
        this.parentRoleId = parentRoleId;
    }

    public Role getParentRole()
    {
        return parentRole;
    }

    public void setParentRole(Role parentRole)
    {
        this.parentRole = parentRole;
    }

    public List<Role> getChildren()
    {
        return children;
    }

    public void setChildren(List<Role> children)
    {
        this.children = children;
    }

    public boolean equals(Object o)
    {
        return this == o || o instanceof Role && roleId.equals(((Role) o).roleId);
    }

    public int hashCode()
    {
        return roleId.hashCode();
    }

    public String toString()
    {
        return roleName;
    }
}
