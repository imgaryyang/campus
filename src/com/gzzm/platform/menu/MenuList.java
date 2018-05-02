package com.gzzm.platform.menu;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * @author camel
 * @date 2016/1/15
 */
public class MenuList implements Iterable<MenuItem>
{
    @Inject
    private MenuContainer container;

    @Inject
    private MenuDao dao;

    @Inject
    private UserOnlineInfo userOnlineInfo;

    private String parentMenuId;

    public MenuList()
    {
    }

    public String getParentMenuId()
    {
        return parentMenuId;
    }

    public void setParentMenuId(String parentMenuId)
    {
        this.parentMenuId = parentMenuId;
    }

    public List<MenuItem> getMenus() throws Exception
    {
        MenuItem parentMenu = container.getMenu(parentMenuId);

        List<MenuItem> children = parentMenu.getChildren();

        List<MenuItem> menus = new ArrayList<MenuItem>(children.size());

        for (MenuItem menu : children)
        {
            if (!menu.isHidden() && userOnlineInfo.isAccessable(menu.getMenuId()))
            {
                menus.add(menu);
            }
        }

        if (parentMenu.getParentMenuId() == null)
        {
            UserMenuConfig userMenuConfig = dao.getUserMenuConfig(userOnlineInfo.getUserId(), parentMenuId);
            if (userMenuConfig != null)
            {
                MenuConfig menuConfig = userMenuConfig.getMenuConfig();

                if (menuConfig != null && menuConfig.getMenuIds() != null)
                {
                    Collections.sort(menus, menuConfig);
                }
            }
        }

        return menus;
    }

    @Override
    public Iterator<MenuItem> iterator()
    {
        try
        {
            return getMenus().iterator();
        }
        catch (Exception ex)
        {
            Tools.wrapException(ex);
            return null;
        }
    }
}
