package com.gzzm.platform.menu;

import com.gzzm.platform.commons.*;
import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.StringUtils;
import net.cyan.nest.annotation.Inject;

/**
 * 接收显示菜单的请求
 *
 * @author camel
 * @date 2009-7-20
 */
@Service
public class MenuPage
{
    @Inject
    private UserOnlineInfo userOnlineInfo;

    @Inject
    private MenuContainer menuContainer;

    public MenuPage()
    {
    }

    @Redirect
    @Service(url = "/menu/{$0}/{$1}")
    public String forwardSub(String menuId, String subMenuId) throws Exception
    {
        //未登录，抛出异常
        if (userOnlineInfo == null)
            throw new LoginExpireException();

        MenuItem menu = menuContainer.getMenu(menuId);

        //没有权限，抛出异常
        if (!userOnlineInfo.isAccessable(menuId))
            throw new NoErrorException(Messages.NO_AUTH);

        MenuLoader loader = menu.getLoader();
        if (loader == null)
            return null;

        MenuItem subMenu = loader.load(menu, subMenuId);

        //设置
        userOnlineInfo.setState(subMenu.getTitle());

        String url = subMenu.getUrl();

        if (url == null)
            return null;

        //在url后面附加menuId$属性，标识功能是从此菜单转过去的
        return url + (url.indexOf("?") > 0 ? "&" : "?") + MenuItem.MENUID + "=" + menuId + "&" + MenuItem.SUB_MENUID +
                "=" + subMenuId;
    }

    /**
     * 转向一个菜单
     *
     * @param menuId 要转向的菜单id
     * @return 菜单
     * @throws Exception 从数据库读取url异常
     */
    @Redirect
    @Service(url = "/menu/{$0}")
    public String forward(String menuId) throws Exception
    {
        //未登录，抛出异常
        if (userOnlineInfo == null)
            throw new LoginExpireException();

        MenuItem menu = menuContainer.getMenu(menuId);

        //没有权限，抛出异常
        if (!userOnlineInfo.isAccessable(menuId))
            throw new NoErrorException(Messages.NO_AUTH);

        //设置
        userOnlineInfo.setState(menu.getTitle());
        String url = menu.getUrl();

        if (url == null)
            return null;

        //在url后面附加menuId$属性，标识功能是从此菜单转过去的
        return url + (url.indexOf("?") > 0 ? "&" : "?") + MenuItem.MENUID + "=" + menuId;
    }

    /**
     * 转向一个桌面功能
     *
     * @param menuId 要转向的菜单id
     * @return 菜单
     * @throws Exception 从数据库读取url异常
     */
    @Redirect
    @Service(url = "/desktop/module/{$0}")
    public String showDesktopModule(String menuId) throws Exception
    {
        MenuItem menu = menuContainer.getMenu(menuId);

        if (StringUtils.isEmpty(menu.getUrl()) && menu.getChildren() != null && menu.getChildren().size() > 0)
        {
            //如果只有一个子菜单有权限，则直接跳向这个子菜单
            MenuItem validMenuItem = null;
            for (MenuItem menuItem : menu.getChildren())
            {
                if (userOnlineInfo.isAdmin() || userOnlineInfo.isAccessable(menuItem.getMenuId()))
                {
                    if (validMenuItem == null)
                        validMenuItem = menuItem;
                    else
                        return "/desktop/module/" + menuId + "/group";
                }
            }

            if (validMenuItem != null)
                return forward(validMenuItem.getMenuId());
            else
                return null;
        }
        else
        {
            return forward(menuId);
        }
    }
}
