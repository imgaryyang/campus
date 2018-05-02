package com.gzzm.platform.organ;

import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

import java.util.List;

/**
 * 角色权限作用范围，定义某个角色对某个权限的作用范围，
 *
 * @author camel
 * @date 2010-1-31
 */
@Entity(table = "PFROLESCOPE", keys = "scopeId")
public class RoleScope
{
    /**
     * 作用范围ID，主键
     */
    @Generatable(length = 8)
    private Integer scopeId;

    /**
     * 角色名称
     */
    @Require
    @Unique(with = "deptId")
    @ColumnDescription(type = "varchar(250)", nullable = false)
    private String scopeName;

    /**
     * 所属部门的id
     */
    @Index
    private Integer deptId;

    /**
     * 排序ID
     */
    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    /**
     * 所属部门
     */
    private Dept dept;

    @ColumnDescription(nullable = false, defaultValue = "0")
    private RoleScopeType type;

    @OneToMany
    private List<RoleScopeDept> roleScopeDepts;

    /**
     * 上一级角色的id，即所属目录
     */
    private Integer parentScopeId;

    @ToOne("PARENTSCOPEID")
    @NotSerialized
    private RoleScope parentScope;

    @NotSerialized
    @OneToMany("PARENTSCOPEID")
    @OrderBy(column = "ORDERID")
    private List<RoleScope> children;

    public RoleScope()
    {
    }

    public Integer getScopeId()
    {
        return scopeId;
    }

    public void setScopeId(Integer scopeId)
    {
        this.scopeId = scopeId;
    }

    public String getScopeName()
    {
        return scopeName;
    }

    public void setScopeName(String scopeName)
    {
        this.scopeName = scopeName;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
    }

    public Dept getDept()
    {
        return dept;
    }

    public void setDept(Dept dept)
    {
        this.dept = dept;
    }

    public List<RoleScopeDept> getRoleScopeDepts()
    {
        return roleScopeDepts;
    }

    public void setRoleScopeDepts(List<RoleScopeDept> roleScopeDepts)
    {
        this.roleScopeDepts = roleScopeDepts;
    }

    public RoleScopeType getType()
    {
        return type;
    }

    public void setType(RoleScopeType type)
    {
        this.type = type;
    }

    public Integer getParentScopeId()
    {
        return parentScopeId;
    }

    public void setParentScopeId(Integer parentScopeId)
    {
        this.parentScopeId = parentScopeId;
    }

    public RoleScope getParentScope()
    {
        return parentScope;
    }

    public void setParentScope(RoleScope parentScope)
    {
        this.parentScope = parentScope;
    }

    public List<RoleScope> getChildren()
    {
        return children;
    }

    public void setChildren(List<RoleScope> children)
    {
        this.children = children;
    }

    @Override
    public boolean equals(Object o)
    {
        return this == o || o instanceof RoleScope && scopeId.equals(((RoleScope) o).scopeId);
    }

    @Override
    public int hashCode()
    {
        return scopeId.hashCode();
    }

    @Override
    public String toString()
    {
        return scopeName;
    }
}
