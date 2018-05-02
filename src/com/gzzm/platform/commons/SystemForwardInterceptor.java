package com.gzzm.platform.commons;

import com.gzzm.platform.desktop.StyleUtils;
import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.platform.menu.MenuItem;
import net.cyan.arachne.*;
import net.cyan.commons.util.*;

/**
 * 整个系统通用的页面拦截
 *
 * @author camel
 * @date 2009-7-25
 */
public class SystemForwardInterceptor implements ForwardInterceptor
{
    public SystemForwardInterceptor()
    {
    }

    public void start(ForwardContext context, boolean action) throws Exception
    {
        if (action)
        {
            String path = context.getPath();

            String uri = context.getRequestContext().getOriginalRequestURI();

            boolean b = (uri != null && uri.endsWith(".lp")) || path.endsWith(".html") || path.startsWith("/web/") ||
                    path.startsWith("web/") || "/platform/login/deptselect.ptl".equals(path);

            //引入api.js
            context.importJs(context.getJsPath() + "/api.js");

            RequestContext requestContext = context.getRequestContext();
            UserOnlineInfo userOnlineInfo = UserOnlineInfo.getUserOnlineInfo(requestContext.getRequest());
            if (userOnlineInfo != null)
            {
                MenuItem menu = MenuItem.getMenu(requestContext.getRequest());

                if (menu != null || !b)
                {
                    //设置stylePath
                    StyleUtils.initStylePath(menu == null ? null : menu.getGroup(), requestContext.getRequest(),
                            requestContext.getResponse(), context);
                }

                if (menu != null)
                {
                    Object form = requestContext.getForm();

                    context.writeln("<script>");
                    String title = menu.getAppTitle();
                    if (StringUtils.isEmpty(title))
                        title = menu.getTitle();
                    context.writeln("System.title=\"" + HtmlUtils.escapeString(Tools.getMessage(title, form)) + "\";");

                    if (!StringUtils.isEmpty(menu.getAppRemark()))
                    {
                        context.writeln("System.remark=\"" +
                                HtmlUtils.escapeString(Tools.getMessage(menu.getAppRemark(), form)) +
                                "\";");
                    }
                    context.writeln("</script>");
                }

                if (menu != null || !b)
                {
                    context.writeln("<script>");
                    context.writeln("System.userName=\"" + userOnlineInfo.getUserName() + "\";");
                    if (menu != null)
                        context.writeln("System.menuId=\"" + menu.getMenuId() + "\";");
                    context.writeln("</script>");
                }
            }
        }
    }

    public void end(ForwardContext context, boolean action) throws Exception
    {
    }
}
