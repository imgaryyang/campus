package com.gzzm.platform.menu;

import com.gzzm.platform.login.UserOnlineInfo;

import java.util.*;

/**
 * 一棵菜单树
 *
 * @author camel
 * @date 2009-7-20
 */
public class MenuTree
{
    /**
     * 菜单根节点
     */
    private MenuItem root;

    MenuTree(MenuItem root)
    {
        this.root = root;
    }

    public List<MenuItem> getMenus()
    {
        List<MenuItem> menus = root.getChildren();
        if (menus == null)
            return Collections.emptyList();
        else
            return menus;
    }

    /**
     * 根据权限获得菜单树
     *
     * @param appIds 权限列表，即菜单id
     * @return 过滤过的菜单树
     */
    public List<MenuItem> getTree(Collection<String> appIds, boolean showHidden, boolean load) throws Exception
    {
        List<MenuItem> menus = new ArrayList<MenuItem>();

        if (root.getChildren() != null)
        {
            for (MenuItem menu : root.getChildren())
            {
                MenuItem menu2 = menu.getMenuItem(appIds, showHidden, load);
                if (menu2 != null)
                    menus.add(menu2);
            }
        }

        return menus;
    }

    public List<MenuItem> getTree(UserOnlineInfo userOnlineInfo, boolean showHidden, boolean load) throws Exception
    {
        return showHidden && !load && userOnlineInfo.isAdmin() ? new ArrayList<MenuItem>(getMenus()) :
                getTree(userOnlineInfo.isAdmin() ? null : userOnlineInfo.getAppIds(), showHidden, load);
    }

    public String getGroup()
    {
        return root.getGroup();
    }

    public MenuItem getRoot()
    {
        return root;
    }
}
