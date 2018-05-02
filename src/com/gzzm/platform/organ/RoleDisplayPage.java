package com.gzzm.platform.organ;

import com.gzzm.platform.menu.MenuTreeModel;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 角色展示的页面，展示某些角色拥有哪些菜单
 *
 * @author camel
 * @date 2010-6-24
 */
@Service
public class RoleDisplayPage
{
    @Inject
    private static Provider<MenuTreeModel> menuTreeModelProvider;

    @Inject
    private OrganDao dao;

    private List<Integer> roleIds;

    @NotSerialized
    private Collection<String> appIds;

    private Integer deptId;

    private Integer userId;

    @NotSerialized
    private MenuTreeModel menuTree;

    public RoleDisplayPage()
    {
    }

    @Service(url = "/RoleDisplay")
    @Forward(page = "/platform/organ/role_display.ptl")
    public String show()
    {
        return null;
    }

    public List<Integer> getRoleIds() throws Exception
    {
        if (roleIds == null && userId != null)
        {
            roleIds = dao.getRoleIds(userId, deptId);
        }

        return roleIds;
    }

    public void setRoleIds(List<Integer> roleIds)
    {
        this.roleIds = roleIds;
    }

    @SuppressWarnings("unchecked")
    public Collection<String> getAppIds() throws Exception
    {
        if (appIds == null)
        {
            List<Integer> roleIds = getRoleIds();
            if (roleIds == null)
                return Collections.EMPTY_LIST;

            appIds = new ArrayList<String>();
            for (Integer roleId : getRoleIds())
            {
                Role role = dao.getRole(roleId);

                if (role != null)
                    addRole(role);
            }
        }

        return appIds;
    }

    private void addRole(Role role)
    {
        if (role.getType() == RoleType.group)
        {
            for (Role role1 : role.getGroupRoles())
                addRole(role1);
        }
        else
        {
            for (RoleApp roleApp : role.getRoleApps())
                appIds.add(roleApp.getAppId());
        }
    }

    public void setAppIds(Collection<String> appIds)
    {
        this.appIds = appIds;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

    public MenuTreeModel getMenuTree() throws Exception
    {
        if (menuTree == null)
        {
            menuTree = menuTreeModelProvider.get();
            menuTree.setCheckable(false);
            menuTree.setShowHidden(true);
            menuTree.setAppIds(getAppIds());
        }

        return menuTree;
    }

    @Service(method = HttpMethod.post)
    public List<Role> getRoles(String appId) throws Exception
    {
        List<Role> roles = new ArrayList<Role>();
        for (Integer roleId : getRoleIds())
        {
            Role role = dao.getRole(roleId);

            if (role != null)
                putRole(role, roles, appId);
        }

        return roles;
    }

    private boolean putRole(Role role, List<Role> roles, String appId) throws Exception
    {
        if (role.getType() == RoleType.group)
        {
            boolean b = false;
            for (Role role1 : role.getGroupRoles())
            {
                b |= putRole(role1, roles, appId);
            }

            if (b)
                roles.add(role);

            return b;
        }
        else
        {
            if (roles.contains(role))
                return true;

            for (RoleApp roleApp : role.getRoleApps())
            {
                if (roleApp.getAppId().equals(appId))
                {
                    roles.add(role);
                    return true;
                }
            }

            return false;
        }
    }

    @Service
    public List<RoleScope> getScopes(Integer[] roleIds, String appId) throws Exception
    {
        List<RoleScope> scopes = new ArrayList<RoleScope>();
        for (Integer roleId : roleIds)
        {
            Role role = dao.getRole(roleId);

            if (role != null)
            {
                for (RoleApp roleApp : role.getRoleApps())
                {
                    if (roleApp.getAppId().equals(appId))
                    {
                        if (roleApp.getScopeId() != null && roleApp.getScopeId() == -1)
                            return null;

                        RoleScope scope = roleApp.getScope();
                        if (scope != null && !scopes.contains(scope))
                            scopes.add(scope);
                    }
                }
            }
        }

        return scopes;
    }

    @Service
    public List<Dept> getDepts(Integer[] roleIds, String appId) throws Exception
    {
        AppInfo appInfo = new AppInfo(appId);
        for (Integer roleId : roleIds)
        {
            Role role = dao.getRole(roleId);

            if (role != null)
            {
                for (RoleApp roleApp : role.getRoleApps())
                {
                    if (roleApp.getAppId().equals(appId))
                    {
                        if (roleApp.getScopeId() != null && roleApp.getScopeId() == -1)
                            return null;

                        appInfo.add(roleApp, deptId);
                    }
                }
            }
        }

        Collection<Integer> deptIds = appInfo.getDeptIds();
        if (deptIds == null)
            return null;

        return dao.getDepts(deptIds);
    }
}