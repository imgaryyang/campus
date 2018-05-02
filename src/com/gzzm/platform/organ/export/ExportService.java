package com.gzzm.platform.organ.export;

import com.gzzm.platform.menu.*;
import com.gzzm.platform.organ.*;
import net.cyan.commons.transaction.Transactional;
import net.cyan.crud.CrudUtils;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * @author camel
 * @date 13-12-26
 */
public class ExportService
{
    @Inject
    private OrganDao organDao;

    @Inject
    private MenuDao menuDao;

    public ExportService()
    {
    }

    public List<Menu> getTopMenus() throws Exception
    {
        return menuDao.getTopMenus();
    }

    /**
     * 导出权限数据
     *
     * @param groupIds 菜单功能组
     * @throws Exception
     */
    public SystemExport export(String[] groupIds) throws Exception
    {
        SystemExport export = new SystemExport();

        //导出菜单
        for (String groupId : groupIds)
        {
            Menu menu = menuDao.getMenu(groupId);

            for (Menu childMenu : menu.getChildMenus())
            {
                export.addMenu(new ExportMenu(childMenu, groupId));
            }
        }

        for (Role role : organDao.getRolesByAppIds(export.getMenuIds(), 1))
        {
            export.addRole(new ExportRole(role));

            while (role.getParentRoleId() != null && !export.containsRole(role.getParentRoleId()))
            {
                role = role.getParentRole();
                export.addRole(new ExportRole(role));
            }
        }

        for (Integer scopeId : export.getScopeIds())
        {
            RoleScope scope = organDao.getScope(scopeId);

            if (scope != null && scope.getDeptId() == 1)
            {
                export.addScope(new ExportRoleScope(scope));

                while (scope.getParentScopeId() != null && !export.containsScope(scope.getParentScopeId()))
                {
                    scope = scope.getParentScope();
                    export.addScope(new ExportRoleScope(scope));
                }
            }
        }

        return export;
    }

    @Transactional
    public void imp(SystemExport export) throws Exception
    {
        Map<String, String> menuMap = new HashMap<String, String>();

        for (ExportMenu exportMenu : export.getMenus())
        {
            saveMenu(exportMenu, exportMenu.getGroup(), CrudUtils.getOrderValue(6, true), menuMap);
        }

        Map<Integer, Integer> scopeMap = new HashMap<Integer, Integer>();
        if (export.getScopes() != null)
        {
            for (ExportRoleScope exportScope : export.getScopes())
            {
                saveScope(exportScope, scopeMap, export);
            }
        }

        if (export.getRoles() != null)
        {
            Map<Integer, Integer> roleMap = new HashMap<Integer, Integer>();

            for (ExportRole exportRole : export.getRoles())
            {
                if (exportRole.getType() == RoleType.role)
                    saveRole(exportRole, menuMap, scopeMap, roleMap, export);
            }

            for (ExportRole exportRole : export.getRoles())
            {
                if (exportRole.getType() == RoleType.group)
                    saveRole(exportRole, menuMap, scopeMap, roleMap, export);
            }
        }

        Menu.setUpdated();
    }

    private void saveMenu(ExportMenu exportMenu, String parentMenuId, int orderId, Map<String, String> menuMap)
            throws Exception
    {
        if (exportMenu.isHidden())
        {
            if (menuDao.getMenu(exportMenu.getMenuId()) != null)
            {
                menuMap.put(exportMenu.getMenuId(), exportMenu.getMenuId());

                return;
            }
        }

        Menu menu = new Menu();

        if (exportMenu.isHidden())
            menu.setMenuId(exportMenu.getMenuId());

        menu.setMenuTitle(exportMenu.getMenuTitle());
        menu.setUrl(exportMenu.getUrl());
        menu.setAppTitle(exportMenu.getAppTitle());
        menu.setAppRemark(exportMenu.getAppRemark());
        menu.setHint(exportMenu.getHint());
        menu.setCondition(exportMenu.getCondition());
        menu.setCountCondition(exportMenu.getCountCondition());
        menu.setIcon(exportMenu.getIcon());
        menu.setCreateTime(new Date());
        menu.setParentMenuId(parentMenuId);
        menu.setOrderId(orderId);
        menu.setHidden(exportMenu.isHidden());

        if (exportMenu.getAuths() != null)
        {
            SortedSet<MenuAuth> auths = new TreeSet<MenuAuth>();
            int index = 0;
            for (ExportMenuAuth exportMenuAuth : exportMenu.getAuths())
            {
                MenuAuth menuAuth = new MenuAuth();
                menuAuth.setMenuId(menu.getMenuId());
                menuAuth.setAuthCode(exportMenuAuth.getAuthCode());
                menuAuth.setAuthName(exportMenuAuth.getAuthName());
                menuAuth.setOrderId(index++);

                auths.add(menuAuth);

                if (exportMenuAuth.getUrls() != null)
                {
                    List<MenuAuthUrl> urls = new ArrayList<MenuAuthUrl>();
                    for (ExportMenuAuthUrl exportUrl : exportMenuAuth.getUrls())
                    {
                        MenuAuthUrl url = new MenuAuthUrl();
                        url.setAuthId(menuAuth.getAuthId());
                        url.setUrl(exportUrl.getUrl());
                        url.setMethod(exportUrl.getMethod());
                        url.setUrlName(exportUrl.getUrlName());

                        urls.add(url);
                    }

                    menuAuth.setAuthUrls(urls);
                }
            }

            menu.setAuths(auths);
        }

        menuDao.add(menu);

        menuMap.put(exportMenu.getMenuId(), menu.getMenuId());

        if (exportMenu.getChildren() != null)
        {
            int index = 0;
            for (ExportMenu childExportMenu : exportMenu.getChildren())
            {
                saveMenu(childExportMenu, menu.getMenuId(), index++, menuMap);
            }
        }
    }

    private void saveScope(ExportRoleScope exportScope, Map<Integer, Integer> scopeMap, SystemExport export)
            throws Exception
    {
        if (scopeMap.containsKey(exportScope.getScopeId()))
            return;

        Integer parentScopeId = null;
        if (exportScope.getParentScopeId() != null)
        {
            parentScopeId = scopeMap.get(exportScope.getParentScopeId());

            if (parentScopeId == null)
            {
                saveScope(export.getRoleScope(exportScope.getParentScopeId()), scopeMap, export);
                parentScopeId = scopeMap.get(exportScope.getParentScopeId());
            }
        }

        RoleScope scope = new RoleScope();

        scope.setScopeName(exportScope.getScopeName());
        scope.setDeptId(1);
        scope.setParentScopeId(parentScopeId);
        scope.setType(exportScope.getType());
        scope.setOrderId(exportScope.getOrderId());

        organDao.add(scope);

        if (exportScope.getRoleScopeDepts() != null)
        {
            for (ExportRoleScopeDept exportRoleScopeDept : exportScope.getRoleScopeDepts())
            {
                if (exportRoleScopeDept.getDeptId() <= 1)
                {
                    RoleScopeDept roleScopeDept = new RoleScopeDept();

                    roleScopeDept.setScopeId(scope.getScopeId());
                    roleScopeDept.setDeptId(exportRoleScopeDept.getDeptId());
                    roleScopeDept.setIncludeSelf(exportRoleScopeDept.isIncludeSelf());
                    roleScopeDept.setIncludeSub(exportRoleScopeDept.isIncludeSub());
                    roleScopeDept.setIncludeSup(exportRoleScopeDept.isIncludeSup());
                    roleScopeDept.setExcluded(exportRoleScopeDept.isExcluded());
                    roleScopeDept.setFilter(exportRoleScopeDept.getFilter());

                    organDao.add(roleScopeDept);
                }
            }
        }

        scopeMap.put(exportScope.getScopeId(), scope.getScopeId());
    }

    private void saveRole(ExportRole exportRole, Map<String, String> menuMap, Map<Integer, Integer> scopeMap,
                          Map<Integer, Integer> roleMap, SystemExport export) throws Exception
    {
        if (roleMap.containsKey(exportRole.getRoleId()))
            return;

        Integer parentRoleId = null;
        if (exportRole.getParentRoleId() != null)
        {
            parentRoleId = roleMap.get(exportRole.getParentRoleId());

            if (parentRoleId == null)
            {
                saveRole(export.getRole(exportRole.getParentRoleId()), menuMap, scopeMap, roleMap, export);
                parentRoleId = roleMap.get(exportRole.getParentRoleId());
            }
        }

        Role role = new Role();

        role.setDeptId(1);
        role.setRoleName(exportRole.getRoleName());
        role.setInheritable(exportRole.isInheritable());
        role.setRemark(exportRole.getRemark());
        role.setType(exportRole.getType());
        role.setParentRoleId(parentRoleId);
        role.setSelectable(exportRole.getSelectable());
        role.setOrderId(exportRole.getOrderId());

        if (role.getType() == RoleType.group)
        {
            if (exportRole.getGroupRoleIds() != null)
            {
                List<Role> groupRoles = new ArrayList<Role>(exportRole.getGroupRoleIds().size());

                for (Integer groupRoleId : exportRole.getGroupRoleIds())
                {
                    if (roleMap.get(groupRoleId) != null)
                    {
                        Role groupRole = new Role();
                        groupRole.setRoleId(roleMap.get(groupRoleId));

                        groupRoles.add(groupRole);
                    }
                }

                role.setGroupRoles(groupRoles);
            }
        }

        organDao.add(role);
        roleMap.put(exportRole.getRoleId(), role.getRoleId());

        if (role.getType() == RoleType.role)
        {
            if (exportRole.getRoleApps() != null)
            {
                for (ExportRoleApp exportRoleApp : exportRole.getRoleApps())
                {
                    if (menuMap.get(exportRoleApp.getAppId()) != null)
                    {
                        RoleApp roleApp = new RoleApp();
                        roleApp.setRoleId(role.getRoleId());
                        roleApp.setAppId(menuMap.get(exportRoleApp.getAppId()));

                        if (exportRoleApp.getScopeId() != null && exportRoleApp.getScopeId() > 0)
                            roleApp.setScopeId(scopeMap.get(exportRoleApp.getScopeId()));
                        else
                            roleApp.setScopeId(exportRoleApp.getScopeId());

                        roleApp.setAuths(exportRoleApp.getAuths());
                        roleApp.setCondition(exportRoleApp.getCondition());
                        roleApp.setSelf(exportRoleApp.isSelf());

                        organDao.add(roleApp);
                    }
                }
            }
        }
    }
}
