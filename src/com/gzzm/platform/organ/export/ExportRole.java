package com.gzzm.platform.organ.export;

import com.gzzm.platform.organ.*;

import java.io.Serializable;
import java.util.*;

/**
 * @author camel
 * @date 13-12-26
 */
public class ExportRole implements Serializable
{
    private static final long serialVersionUID = -5104054615604500854L;

    private Integer roleId;

    private String roleName;

    private String remark;

    private boolean inheritable;

    private RoleType type;

    private List<Integer> groupRoleIds;

    private List<ExportRoleApp> roleApps;

    private Integer parentRoleId;

    private Boolean selectable;

    private Integer orderId;

    public ExportRole()
    {
    }

    public ExportRole(Role role)
    {
        roleId = role.getRoleId();
        roleName = role.getRoleName();
        remark = role.getRemark();
        inheritable = role.isInheritable() != null && role.isInheritable();
        type = role.getType();
        parentRoleId = role.getParentRoleId();
        selectable = role.isSelectable();
        orderId = role.getOrderId();

        if (type == RoleType.group)
        {
            List<Role> groupRoles = role.getGroupRoles();

            groupRoleIds = new ArrayList<Integer>(groupRoles.size());
            for (Role groupRole : groupRoles)
            {
                groupRoleIds.add(groupRole.getRoleId());
            }
        }
        else
        {
            roleApps = new ArrayList<ExportRoleApp>();

            for (RoleApp app : role.getRoleApps())
            {
                roleApps.add(new ExportRoleApp(app));
            }
        }
    }

    void getScopeIds(List<Integer> scopeIds)
    {
        if (roleApps != null)
        {
            for (ExportRoleApp app : roleApps)
            {
                Integer scopeId = app.getScopeId();

                if (scopeId != null && !scopeIds.contains(scopeId))
                    scopeIds.add(scopeId);
            }
        }
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

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public boolean isInheritable()
    {
        return inheritable;
    }

    public void setInheritable(boolean inheritable)
    {
        this.inheritable = inheritable;
    }

    public RoleType getType()
    {
        return type;
    }

    public void setType(RoleType type)
    {
        this.type = type;
    }

    public List<Integer> getGroupRoleIds()
    {
        return groupRoleIds;
    }

    public void setGroupRoleIds(List<Integer> groupRoleIds)
    {
        this.groupRoleIds = groupRoleIds;
    }

    public List<ExportRoleApp> getRoleApps()
    {
        return roleApps;
    }

    public void setRoleApps(List<ExportRoleApp> roleApps)
    {
        this.roleApps = roleApps;
    }

    public Integer getParentRoleId()
    {
        return parentRoleId;
    }

    public void setParentRoleId(Integer parentRoleId)
    {
        this.parentRoleId = parentRoleId;
    }

    public Boolean getSelectable()
    {
        return selectable;
    }

    public void setSelectable(Boolean selectable)
    {
        this.selectable = selectable;
    }

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
    }
}
