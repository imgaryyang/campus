package com.gzzm.platform.organ;

import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.*;

/**
 * 角色和应用的关系
 *
 * @author camel
 * @date 2009-7-18
 */
@Entity(table = "PFROLEAPP", keys = {"roleId", "appId"})
public class RoleApp
{
    /**
     * 角色id
     */
    @Index
    @ColumnDescription(type = "number(8)", nullable = false)
    private Integer roleId;

    /**
     * 应用id，可能是一个菜单的id，也可能是一个系统另外定义的应用（如收短信等）
     */
    @Index
    @ColumnDescription(type = "varchar(50)", nullable = false)
    private String appId;

    /**
     * 作用范围id
     */
    private Integer scopeId;

    @Lazy(false)
    @NotSerialized
    private RoleScope scope;

    @NotSerialized
    private Role role;

    /**
     * 权限列表，多个权限用逗号隔开
     */
    @ColumnDescription(type = "varchar(250)")
    private String auths;

    /**
     * 附加查询条件
     *
     * @see com.gzzm.platform.organ.AppInfo#condition
     * @see com.gzzm.platform.commons.crud.SystemCrudUtils#getCondition(String)
     */
    @ColumnDescription(type = "varchar(500)")
    private String condition;

    /**
     * 限制只允许看到自己的数据，false表示不限制，true表示限制，默认为false
     * 当限制时，通过userId或者creator过滤数据
     */
    @ColumnDescription(defaultValue = "0")
    private Boolean self;

    public RoleApp()
    {
    }

    public Integer getRoleId()
    {
        return roleId;
    }

    public void setRoleId(Integer roleId)
    {
        this.roleId = roleId;
    }

    public String getAppId()
    {
        return appId;
    }

    public void setAppId(String appId)
    {
        this.appId = appId;
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

    public Role getRole()
    {
        return role;
    }

    public void setRole(Role role)
    {
        this.role = role;
    }

    public String getAuths()
    {
        return auths;
    }

    public void setAuths(String auths)
    {
        this.auths = auths;
    }

    public String getCondition()
    {
        return condition;
    }

    public void setCondition(String condition)
    {
        this.condition = condition;
    }

    public Boolean isSelf()
    {
        return self;
    }

    public void setSelf(Boolean self)
    {
        this.self = self;
    }

    public String getScopeName()
    {
        if (scopeId == null)
            return "";

        if (scopeId == -1)
            return "所有部门";

        RoleScope scope = getScope();
        if (scope != null)
            return scope.getScopeName();

        return null;
    }

    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof RoleApp))
            return false;

        RoleApp roleApp = (RoleApp) o;

        return appId.equals(roleApp.appId) && roleId.equals(roleApp.roleId);
    }

    public int hashCode()
    {
        int result;
        result = roleId.hashCode();
        result = 31 * result + appId.hashCode();
        return result;
    }

    @Override
    @SuppressWarnings("CloneDoesntCallSuperClone")
    protected RoleApp clone() throws CloneNotSupportedException
    {
        RoleApp c = new RoleApp();
        c.appId = appId;
        c.scopeId = scopeId;
        c.auths = auths;
        c.condition = condition;

        return c;
    }
}
