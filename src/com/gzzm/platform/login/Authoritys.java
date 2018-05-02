package com.gzzm.platform.login;

import com.gzzm.platform.commons.*;
import com.gzzm.platform.menu.*;
import com.gzzm.platform.organ.AppInfo;
import net.cyan.commons.log.*;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 权限校验
 *
 * @author camel
 * @date 2009-7-22
 */
@Injectable(singleton = true)
public class Authoritys
{
    /**
     * 静态注入菜单容器
     */
    @Inject
    private static Provider<MenuContainer> menuContainerProvider;

    @Inject
    private static Provider<HttpServletRequest> requestProvider;

    /**
     * 不检查权限的路径
     */
    private Set<String> no_checked_paths;

    private volatile Log log;

    public Authoritys()
    {
        no_checked_paths = new HashSet<String>();

        //验证码不需要校验
        no_checked_paths.add("/captcha");

        //登录不需要权限
        no_checked_paths.add("/login");
        no_checked_paths.add("/login/");

        //菜单自己控制权限
        no_checked_paths.add("/menu/");
    }

    public Log getLog()
    {
        if (log == null)
        {
            synchronized (this)
            {
                if (log == null)
                {
                    log = LogManager.getLog(getClass());
                }
            }
        }

        return log;
    }

    public final void check(HttpServletRequest request) throws NoErrorException
    {
        try
        {
            check0(request);
        }
        catch (LoginExpireException ex)
        {
            //记录没有登录
            if (log != null)
            {
                String s = request.getRequestURI();
                if (request.getQueryString() != null)
                    s += "?" + request.getQueryString();
                log.info(request.getRemoteAddr() + ":" + request.getRemotePort() + " attempt to access " +
                        s + " without login");
            }

            throw ex;
        }
        catch (NoErrorException ex)
        {
            //记录非法访问
            if (log != null)
            {
                String s = request.getRequestURI();
                if (request.getQueryString() != null)
                    s += "?" + request.getQueryString();

                UserOnlineInfo userOnlineInfo = UserOnlineInfo.getUserOnlineInfo(request);

                log.info("user " + userOnlineInfo.getUserId() + " from " + request.getRemoteAddr() +
                        " attempt to access " + s + " without authority");
            }

            throw ex;
        }
    }

    /**
     * 校验当前用户是否有权限访问此请求
     *
     * @param request http请求
     * @throws NoErrorException 如果没有权限，抛出异常
     */
    protected final void check0(HttpServletRequest request) throws NoErrorException
    {
        UserOnlineInfo userOnlineInfo = UserOnlineInfo.getUserOnlineInfo(request);

        String menuId = MenuItem.getMenuId(request);
        MenuContainer menuContainer = null;

        if (menuId != null)
        {
            if (userOnlineInfo == null)
                throw new LoginExpireException();

            menuContainer = menuContainerProvider.get();
            MenuItem menu = menuContainer.getMenu(menuId);
            if (menu != null)
                userOnlineInfo.setState(menu.getTitle());
        }

        //管理员，不作权限校验
        if (userOnlineInfo != null && userOnlineInfo.isAdmin())
            return;

        if (menuId != null)
        {
            //没有访问权限
            if (!userOnlineInfo.hasApp(menuId))
                throw new NoErrorException(Messages.NO_AUTH);
        }

        //需要检查
        List<AppUrl> urls;
        try
        {
            if (menuContainer == null)
                menuContainer = menuContainerProvider.get();
            urls = menuContainer.getUrls();
        }
        catch (Exception ex)
        {
            //使用不受检查的异常
            Tools.wrapException(ex);
            return;
        }

        boolean checked = false;

        List<AppInfo> appInfos = null;

        for (AppUrl url : urls)
        {
            if (url.matches(request))
            {
                //能够匹配到一个系统定义的url，需要检查权限

                //没有登录或超时
                if (userOnlineInfo == null)
                    throw new LoginExpireException();

                if (menuId != null)
                {
                    Collection<String> auths = url.getAppMap().get(menuId);
                    if (auths != null)
                    {
                        //有权限访问
                        AppInfo appInfo = userOnlineInfo.getApp(menuId, auths);
                        if (appInfo != null)
                        {
                            request.setAttribute("appInfos$", Collections.singleton(appInfo));
                            return;
                        }
                    }
                }

                if (appInfos == null)
                {
                    appInfos = userOnlineInfo.getApps(url.getAppMap());
                    if (appInfos != null)
                    {
                        if (appInfos.size() > 0)
                        {
                            if (menuId == null)
                                break;
                        }
                        else
                        {
                            appInfos = null;
                        }
                    }
                }

                //标志此url是受限的
                checked = true;
            }
        }

        if (appInfos != null)
        {
            //有权限访问
            request.setAttribute("appInfos$", appInfos);
            MenuItem.setMenuId(request, appInfos.get(0).getAppId());
        }
        else if (checked)
        {
            //一个受限的url，但是没有权限访问
            throw new NoErrorException(Messages.NO_AUTH);
        }
        else if (menuId != null)
        {
            AppInfo appInfo = userOnlineInfo.getApp(menuId, Collections.singleton("default"));
            if (appInfo != null)
            {
                request.setAttribute("appInfos$", Collections.singleton(appInfo));
            }
        }
    }

    public Set<String> getNo_checked_paths()
    {
        return no_checked_paths;
    }

    public void setNo_checked_paths(Set<String> no_checked_paths)
    {
        this.no_checked_paths = no_checked_paths;
    }

    public boolean isNoCheckedUrl(String url)
    {
        if (no_checked_paths != null)
        {
            for (String path : no_checked_paths)
            {
                if (path.charAt(path.length() - 1) == '/')
                {
                    if (url.startsWith(path))
                        return true;
                }
                else if (url.equals(path))
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 判断当前用户能否使用此权限
     *
     * @param auth 权限编号
     * @return 能使用返回true，不能返回false
     */
    @SuppressWarnings("unchecked")
    public static boolean hasAuth(String auth)
    {
        HttpServletRequest request = requestProvider.get();

        Collection<AppInfo> appInfos = (Collection<AppInfo>) request.getAttribute("appInfos$");

        //功能不受限
        if (appInfos == null)
            return true;

        for (AppInfo appInfo : appInfos)
        {
            //拥有权限，返回true
            if (appInfo.hasAuth(auth))
                return true;
        }

        //没有权限，返回false
        return false;
    }
}
