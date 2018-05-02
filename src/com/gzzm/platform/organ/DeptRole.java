package com.gzzm.platform.organ;

import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

/**
 * 将角色赋权给部门
 *
 * @author camel
 * @date 2016/9/20
 */
@Entity(table = "PFDEPTROLE", keys = "deptRoleId")
public class DeptRole
{
    @Generatable(length = 5)
    private Integer deptRoleId;

    private Integer deptId;

    private Dept dept;

    @Unique(with = "deptId")
    private Integer roleId;

    private Role role;

    @Require
    private Boolean inheritable;

    public DeptRole()
    {
    }

    public Integer getDeptRoleId()
    {
        return deptRoleId;
    }

    public void setDeptRoleId(Integer deptRoleId)
    {
        this.deptRoleId = deptRoleId;
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

    public Integer getRoleId()
    {
        return roleId;
    }

    public void setRoleId(Integer roleId)
    {
        this.roleId = roleId;
    }

    public Role getRole()
    {
        return role;
    }

    public void setRole(Role role)
    {
        this.role = role;
    }

    public Boolean getInheritable()
    {
        return inheritable;
    }

    public void setInheritable(Boolean inheritable)
    {
        this.inheritable = inheritable;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof DeptRole))
            return false;

        DeptRole deptRole = (DeptRole) o;

        return deptRoleId.equals(deptRole.deptRoleId);
    }

    @Override
    public int hashCode()
    {
        return deptRoleId.hashCode();
    }
}
