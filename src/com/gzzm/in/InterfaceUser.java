package com.gzzm.in;

import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

/**
 * @author camel
 * @date 13-12-17
 */
@Entity(table = "INUSER", keys = "userId")
public class InterfaceUser
{
    @Generatable(length = 6)
    private Integer userId;

    @Require
    @Unique
    @ColumnDescription(type = "varchar(250)")
    private String loginName;

    @Require
    @ColumnDescription(type = "varchar(250)")
    private String userName;

    @Require
    @ColumnDescription(type = "varchar(250)")
    private String password;

    private Integer deptId;

    @NotSerialized
    private Dept dept;

    /**
     * 拥有权限的数据范围
     */
    private Integer scopeId;

    @NotSerialized
    private RoleScope scope;

    public InterfaceUser()
    {
    }

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

    public String getLoginName()
    {
        return loginName;
    }

    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
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

    public Integer getScopeId()
    {
        return scopeId;
    }

    public void setScopeId(Integer scopeId)
    {
        this.scopeId = scopeId;
    }

    public RoleScope getScope()
    {
        return scope;
    }

    public void setScope(RoleScope scope)
    {
        this.scope = scope;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof InterfaceUser))
            return false;

        InterfaceUser that = (InterfaceUser) o;

        return userId.equals(that.userId);
    }

    @Override
    public int hashCode()
    {
        return userId.hashCode();
    }
}
