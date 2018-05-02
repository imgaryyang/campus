package com.gzzm.platform.commons.crud;

import com.gzzm.platform.commons.*;
import com.gzzm.platform.desktop.*;
import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.platform.menu.MenuItem;
import net.cyan.arachne.RequestContext;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.*;

/**
 * 获得当前请求的页面大小
 *
 * @author camel
 * @date 2009-11-3
 */
@Injectable(singleton = true)
public class PageSizeProvider implements Provider
{
    @Inject
    private static Provider<UserOnlineInfo> userInfoProvider;

    @Inject
    private static Provider<GlobalConfig> configProvider;

    @Inject
    private static Provider<DesktopDao> daoProvider;

    @Inject
    private static Provider<MenuItem> menuItemProvider;

    private static final PageSizeProvider INSTANCE = new PageSizeProvider();

    public static PageSizeProvider getInstance()
    {
        return INSTANCE;
    }

    private PageSizeProvider()
    {
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public Object get()
    {
        if (RequestContext.getContext().get("pageSize") != null)
            return null;

        String page = RequestContext.getContext().get("page");
        GlobalConfig config = configProvider.get();

        if ("list".equals(page) || "table".equals(page))
        {
            int pageSize = config.getSimpleListPageSize();

            if ("table".equals(page))
                pageSize--;

            MenuItem menuItem = menuItemProvider.get();

            try
            {
                Integer.parseInt(menuItem.getParentMenuId());
                //菜单是数字，说明是二级菜单，记录数减少一条，腾出位置给一级菜单标签页
                pageSize--;
            }
            catch (NumberFormatException ex)
            {
                //父菜单不是数字，说明是一级菜单
            }

            return pageSize;
        }
        else
        {
            UserOnlineInfo userOnlineInfo = userInfoProvider.get();


            //不是在线用户，返回默认的页面配置的记录数
            if (userOnlineInfo == null)
                return config.getPageSize();

            Integer pageSize = null;
            if (userOnlineInfo.getAttribute("pageSize") == null)
            {
                //还未加载用户配置的记录数，加载
                try
                {
                    //从数据库中加载用户设置的页面记录数
                    UserDesktopConfig desktopConfig =
                            daoProvider.get().getDesktopStyle(userOnlineInfo.getUserId(), "desktop");
                    if (desktopConfig != null)
                    {
                        pageSize = desktopConfig.getPageSize();
                        if (pageSize != null && pageSize <= 0)
                            pageSize = null;
                    }
                }
                catch (Exception ex)
                {
                    Tools.wrapException(ex);
                }

                //用户没有设置页面记录数，加载系统配置的页面记录数
                if (pageSize == null)
                    pageSize = config.getPageSize(userOnlineInfo.getSystemId());
                userOnlineInfo.setAttribute("pageSize", pageSize);
            }
            else
            {
                pageSize = userOnlineInfo.getAttribute("pageSize");
            }

            return pageSize;
        }
    }
}
