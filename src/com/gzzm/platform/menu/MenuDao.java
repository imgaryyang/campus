package com.gzzm.platform.menu;

import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.*;

/**
 * 菜单功能相关的数据访问对象
 *
 * @author camel
 * @date 2009-7-20
 */
public abstract class MenuDao extends GeneralDao
{
    public MenuDao()
    {
    }

    public Menu getMenu(String menuId) throws Exception
    {
        return load(Menu.class, menuId);
    }

    @OQL("select m.menuTitle from Menu m where m.menuId=:1")
    public abstract String getMenuTitle(String menuId) throws Exception;


    /**
     * 获得所有菜单的菜单id和url
     *
     * @return 所有菜单的菜单id和url, key属性为菜单id，value属性为url
     * @throws Exception 数据库异常
     */
    @OQL("select m,[m.icon] from Menu m where valid=1 order by m.orderId")
    public abstract List<Menu> getAllMenus() throws Exception;

    @MapResult
    @OQL("select authId,a from MenuAuth a where menu.valid=1")
    public abstract Map<Integer, MenuAuth> getAllMenuAuths() throws Exception;

    @OQL("select u from MenuAuthUrl u where auth.menu.valid=1")
    public abstract List<MenuAuthUrl> getAllMenuAuthUrls() throws Exception;

    @OQL("select m from Menu m where parentMenuId is null order by m.orderId")
    public abstract List<Menu> getTopMenus() throws Exception;

    /**
     * 获得某个菜单的url
     *
     * @param menuId 菜单id
     * @return url
     * @throws Exception 数据库异常
     */
    @OQL("select m.url from Menu m where m.menuId=:1")
    public abstract String getUrl(String menuId) throws Exception;


    public UserMenuConfig getUserMenuConfig(Integer userId, String menuGroup) throws Exception
    {
        return load(UserMenuConfig.class, userId, menuGroup);
    }
}
