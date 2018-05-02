package com.gzzm.platform.desktop;

import com.gzzm.platform.commons.*;
import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.platform.menu.MenuItem;
import net.cyan.arachne.RequestContext;
import net.cyan.commons.util.*;
import net.cyan.commons.util.io.WebUtils;
import net.cyan.nest.annotation.Inject;

import javax.servlet.http.*;

/**
 * 和style相关的一些操作
 *
 * @author camel
 * @date 2009-10-6
 */
public class StyleUtils
{
    @Inject
    private static Provider<DesktopDao> daoProvider;

    private StyleUtils()
    {
    }

    public static String getDefaultStylePath() throws Exception
    {
        return "/oa/styles/default";
    }

    public static String getStylePath(HttpServletRequest request)
    {
        return (String) request.getAttribute("style.path");
    }

    public static String getStylePath()
    {
        return getStylePath(RequestContext.getContext().getRequest());
    }

    public static String getStylePath(Integer userId) throws Exception
    {
        return getStylePath(userId, getDefaultStylePath());
    }

    public static String getStylePath(Integer userId, String defaultStylePath) throws Exception
    {
        String path = daoProvider.get().getStylePath(userId);
        return path == null ? defaultStylePath : path;
    }

    public static boolean isAotoReload(Integer userId) throws Exception
    {
        Boolean autoReload = daoProvider.get().getAutoReload(userId);
        return autoReload == null || autoReload;
    }

    public static void initStylePath(String type, HttpServletRequest request, HttpServletResponse response)
    {
        String s = "style.path." + type;
        String stylePath = (String) request.getSession().getAttribute(s);
        if (stylePath == null)
            stylePath = WebUtils.getCookie(request, s);
        else
            WebUtils.setCookie(response, request, s, stylePath);

        if (!StringUtils.isEmpty(stylePath))
        {
            request.getSession().setAttribute("style.path", stylePath);
            WebUtils.setCookie(response, request, "style.path", stylePath);
        }
    }

    public static void initStylePath(String type, HttpServletRequest request, HttpServletResponse response,
                                     Appendable writer) throws Exception
    {
        String s = StringUtils.isEmpty(type) ? "style.path" : "style.path." + type;

        String stylePath = (String) request.getSession().getAttribute(s);
        if (stylePath == null)
            stylePath = WebUtils.getCookie(request, s);

        initStylePath0(stylePath, request, writer);
    }

    public static void initStylePath(String stylePath) throws Exception
    {
        RequestContext context = RequestContext.getContext();
        initStylePath0(stylePath, context.getRequest(), context.getForwardContext());
    }

    private static void initStylePath0(String stylePath, HttpServletRequest request, Appendable writer) throws Exception
    {
        if ("true".equals(request.getAttribute("stylePathInited")))
            return;

        boolean b = !StringUtils.isEmpty(stylePath);
        String stylePath0 = stylePath;

        if (b)
        {
            request.setAttribute("stylePathInited", "true");

            stylePath = Tools.getContextPath(stylePath);

            writer.append("<script src=\"").append(stylePath).append("/init.js\"></script>\n");
        }

        if (!"true".equals(request.getAttribute("baseJsImported")))
        {
            request.setAttribute("baseJsImported", "true");

            //引入系统基本功能的js
            writer.append("<script src=\"").append(Tools.getContextPath("/platform/commons/base.js"))
                    .append("\"></script>\n");


            String autoReload = WebUtils.getCookie(request, "style.autoReload");
            if (!StringUtils.isEmpty(autoReload))
            {
                writer.append("<script>\r\n");
                writer.append("System.autoReload=").append(autoReload).append(";\r\n");
                writer.append("</script>\r\n");
            }
        }

        if (b)
        {
            writer.append("<script>\r\n");
            request.setAttribute("style.path", stylePath);
            writer.append("System.stylePath=\"").append(HtmlUtils.escapeString(stylePath0)).append("\";\r\n");
            writer.append("</script>\r\n");

            writer.append("<script src=\"").append(stylePath).append("/style.js\"></script>\n");

            writer.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"").append(stylePath)
                    .append("/style.css\">\n");
        }
    }

    public static void setStylePath(String type, HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        UserOnlineInfo userOnlineInfo = UserOnlineInfo.getUserOnlineInfo(request);
        if (userOnlineInfo == null)
            throw new LoginExpireException();

        setStylePath(type, getStylePath(userOnlineInfo.getUserId()), request, response);
    }

    public static void setStylePath(String type, String path, HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        request.setAttribute("style.path", path);
        WebUtils.setCookie(response, request, "style.path", path);
        WebUtils.setCookie(response, request, "style.path." + type, path);
        request.getSession().setAttribute("style.path", path);
        request.getSession().setAttribute("style.path." + type, path);

        UserOnlineInfo userOnlineInfo = UserOnlineInfo.getUserOnlineInfo(request);
        if (userOnlineInfo == null)
            throw new LoginExpireException();

        WebUtils.setCookie(response, request, "style.autoReload",
                Boolean.toString(isAotoReload(userOnlineInfo.getUserId())));
    }

    public static void clearStyle(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        MenuItem menu = MenuItem.getMenu(request);
        if (menu != null)
        {
            String s = "style.path." + menu.getGroup();
            request.getSession().removeAttribute(s);
        }

        request.getSession().removeAttribute("style.path");
        WebUtils.setCookie(response, request, "style.path", "");
    }
}
