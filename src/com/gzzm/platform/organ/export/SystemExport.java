package com.gzzm.platform.organ.export;

import java.io.Serializable;
import java.util.*;

/**
 * @author camel
 * @date 13-12-26
 */
public class SystemExport implements Serializable
{
    private static final long serialVersionUID = 8845924989900107414L;

    private List<ExportMenu> menus;

    private List<ExportRoleScope> scopes;

    private List<ExportRole> roles;

    public SystemExport()
    {
    }

    List<String> getMenuIds()
    {
        List<String> menuIds = new ArrayList<String>();

        if (menus != null)
        {
            for (ExportMenu menu : menus)
            {
                menu.getMenuIds(menuIds);
            }
        }

        return menuIds;
    }

    List<Integer> getScopeIds()
    {
        List<Integer> scopeIds = new ArrayList<Integer>();

        if (roles != null)
        {
            for (ExportRole role : roles)
            {
                role.getScopeIds(scopeIds);
            }
        }

        return scopeIds;
    }

    public List<ExportMenu> getMenus()
    {
        return menus;
    }

    public void setMenus(List<ExportMenu> menus)
    {
        this.menus = menus;
    }

    public void addMenu(ExportMenu menu)
    {
        if (menus == null)
            menus = new ArrayList<ExportMenu>();

        menus.add(menu);
    }

    public List<ExportRoleScope> getScopes()
    {
        return scopes;
    }

    public void setScopes(List<ExportRoleScope> scopes)
    {
        this.scopes = scopes;
    }

    public void addScope(ExportRoleScope scope)
    {
        if (scopes == null)
            scopes = new ArrayList<ExportRoleScope>();

        scopes.add(scope);
    }

    public List<ExportRole> getRoles()
    {
        return roles;
    }

    public void setRoles(List<ExportRole> roles)
    {
        this.roles = roles;
    }

    public void addRole(ExportRole role)
    {
        if (roles == null)
            roles = new ArrayList<ExportRole>();

        roles.add(role);
    }

    public boolean containsRole(Integer roleId)
    {
        if (roles == null)
            return false;

        for (ExportRole role : roles)
        {
            if (role.getRoleId().equals(roleId))
                return true;
        }

        return false;
    }

    public boolean containsScope(Integer scopeId)
    {
        if (scopes == null)
            return false;

        for (ExportRoleScope scope : scopes)
        {
            if (scope.getScopeId().equals(scopeId))
                return true;
        }

        return false;
    }

    public ExportRoleScope getRoleScope(Integer scopeId)
    {
        if (scopes == null)
            return null;

        for (ExportRoleScope scope : scopes)
        {
            if (scope.getScopeId().equals(scopeId))
                return scope;
        }

        return null;
    }

    public ExportRole getRole(Integer roleId)
    {
        if (roles == null)
            return null;

        for (ExportRole role : roles)
        {
            if (role.getRoleId().equals(roleId))
                return role;
        }

        return null;
    }
}
