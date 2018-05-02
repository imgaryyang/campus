package com.gzzm.platform.menu;

import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 菜单配置
 *
 * @author camel
 * @date 11-11-23
 */
@Service
public class MenuConfigPage
{
    @Inject
    private static Provider<MenuContainer> containerProvider;

    @Inject
    private MenuDao dao;

    @Inject
    private UserOnlineInfo userOnlineInfo;

    private String group;

    public MenuConfigPage()
    {
    }

    public String getGroup()
    {
        return group;
    }

    public void setGroup(String group)
    {
        this.group = group;
    }

    @Service(url = "/menuconfig/{group}/size?size={$0}", method = HttpMethod.post)
    public void setShowSize(int size) throws Exception
    {
        UserMenuConfig userMenuConfig = new UserMenuConfig();
        userMenuConfig.setUserId(userOnlineInfo.getUserId());
        userMenuConfig.setMenuGroup(group);
        userMenuConfig.setShowSize(size);

        dao.save(userMenuConfig);
    }

    @NotSerialized
    public List getMenus() throws Exception
    {
        List<MenuItem> menus = containerProvider.get().getMenuTree(group).getTree(userOnlineInfo, false, false);

        UserMenuConfig userMenuConfig = dao.getUserMenuConfig(userOnlineInfo.getUserId(), group);
        if (userMenuConfig != null)
        {
            MenuConfig menuConfig = userMenuConfig.getMenuConfig();

            if (menuConfig != null && menuConfig.getMenuIds() != null)
            {
                Collections.sort(menus, menuConfig);
            }
        }

        return menus;
    }

    @Service(url = "/menuconfig/{group}", method = HttpMethod.get)
    public String showConfig()
    {
        return "config";
    }

    @Service(url = "/menuconfig/{group}", method = HttpMethod.post)
    public void saveConfig(List<String> menuIds) throws Exception
    {
        UserMenuConfig userMenuConfig = new UserMenuConfig();
        userMenuConfig.setUserId(userOnlineInfo.getUserId());
        userMenuConfig.setMenuGroup(group);

        MenuConfig menuConfig = new MenuConfig();
        menuConfig.setMenuIds(menuIds);

        userMenuConfig.setMenuConfig(menuConfig);

        dao.save(userMenuConfig);
    }
}
