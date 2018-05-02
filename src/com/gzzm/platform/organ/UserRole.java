package com.gzzm.platform.organ;

import net.cyan.thunwind.annotation.*;

/**
 * UserRole实体，对应PFUSERRole表，记录某个用户在某个部门中拥有哪些角色
 *
 * @author camel
 * @date 2009-7-18
 */
@Entity(table = "PFUSERROLE", keys = {"userId", "deptId", "roleId"})
public class UserRole
{
    private Integer userId;

    private Integer deptId;

    private Integer roleId;

    private User user;

    private Dept dept;

    @Lazy(false)
    @Cascade
    private Role role;

    public UserRole()
    {
    }

    public UserRole(Integer userId, Integer deptId, Integer roleId)
    {
        this.userId = userId;
        this.deptId = deptId;
        this.roleId = roleId;
    }

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public Integer getRoleId()
    {
        return roleId;
    }

    public void setRoleId(Integer roleId)
    {
        this.roleId = roleId;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public Dept getDept()
    {
        return dept;
    }

    public void setDept(Dept dept)
    {
        this.dept = dept;
    }

    public Role getRole()
    {
        return role;
    }

    public void setRole(Role role)
    {
        this.role = role;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof UserRole))
            return false;

        UserRole userRole = (UserRole) o;

        return deptId.equals(userRole.deptId) && roleId.equals(userRole.roleId) && userId.equals(userRole.userId);
    }

    public int hashCode()
    {
        int result;
        result = userId.hashCode();
        result = 31 * result + deptId.hashCode();
        result = 31 * result + roleId.hashCode();
        return result;
    }

    @Override
    public String toString()
    {
        return getRole().toString();
    }
}
