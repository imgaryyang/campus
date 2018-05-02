package com.gzzm.platform.organ.export;

import com.gzzm.platform.organ.*;

import java.io.Serializable;
import java.util.*;

/**
 * @author camel
 * @date 13-12-26
 */
public class ExportRoleScope implements Serializable
{
    private static final long serialVersionUID = -3952168040293651694L;

    private Integer scopeId;

    private String scopeName;

    private Integer parentScopeId;

    private RoleScopeType type;

    private Integer orderId;

    private List<ExportRoleScopeDept> roleScopeDepts;

    public ExportRoleScope()
    {
    }

    public ExportRoleScope(RoleScope scope)
    {
        scopeId = scope.getScopeId();
        scopeName = scope.getScopeName();
        parentScopeId = scope.getParentScopeId();
        type = scope.getType();
        orderId = scope.getOrderId();

        roleScopeDepts = new ArrayList<ExportRoleScopeDept>();

        for (RoleScopeDept roleScopeDept : scope.getRoleScopeDepts())
        {
            if (roleScopeDept.getDeptId() <= 1)
                roleScopeDepts.add(new ExportRoleScopeDept(roleScopeDept));
        }
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

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
    }

    public List<ExportRoleScopeDept> getRoleScopeDepts()
    {
        return roleScopeDepts;
    }

    public void setRoleScopeDepts(List<ExportRoleScopeDept> roleScopeDepts)
    {
        this.roleScopeDepts = roleScopeDepts;
    }
}
