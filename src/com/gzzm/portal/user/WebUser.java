package com.gzzm.portal.user;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.commons.util.StringUtils;
import net.cyan.commons.util.io.WebUtils;
import net.cyan.nest.annotation.BeanName;

import javax.servlet.http.*;
import java.io.*;
import java.util.Date;

/**
 * 外网用户信息
 *
 * @author camel
 * @date 2011-5-4
 */
public class WebUser
{
    /**
     * WebUser存放在session中的属性名
     */
    static final String SESSIONNAME = "webUserInfo";

    /**
     * web登录后转向的页面存放在session中的路径
     */
    static final String WEBLOGINFORWARD = "webLoginForward";


    static final String WEB_USER_LOGIN_MODULE = "web_user_login_module";

    private UserOnlineInfo userOnlineInfo;

    WebUser(UserOnlineInfo userOnlineInfo)
    {
        this.userOnlineInfo = userOnlineInfo;
    }

    public static WebUser getUserOnlineInfo(HttpServletRequest request)
    {
        if (request == null)
            return null;

        HttpSession session = request.getSession(false);
        if (session == null)
            return null;

        WebUser webUser = (WebUser) session.getAttribute(SESSIONNAME);

        if (webUser == null)
        {
            UserOnlineInfo userOnlineInfo = UserOnlineInfo.getUserOnlineInfo(request);
            if (userOnlineInfo != null)
            {
                webUser = new WebUser(userOnlineInfo);
                session.setAttribute(SESSIONNAME, webUser);
            }
        }
        else
        {
            UserOnlineInfo userOnlineInfo = UserOnlineInfo.getUserOnlineInfo(request);
            if (userOnlineInfo == null || !userOnlineInfo.getUserId().equals(webUser.getUserId()))
            {
                webUser = null;
                session.removeAttribute(SESSIONNAME);
            }
        }

        return webUser;
    }

    /**
     * 用Inject声明此方法为对象创建方法
     *
     * @param request  httprequest 用于从session中获取在线信息
     * @param response httpresponse 用于判断用户没有登录后转向登录页面
     * @param module   所属模块
     * @return 从httprequest中获取WebUser
     */
    public static WebUser getUserOnlineInfo(HttpServletRequest request, HttpServletResponse response,
                                            @BeanName String module)
    {
        if (request == null)
            return null;

        HttpSession session = request.getSession(true);

        WebUser webUser = (WebUser) session.getAttribute(SESSIONNAME);

        if (webUser == null)
        {
            UserOnlineInfo userOnlineInfo = UserOnlineInfo.getUserOnlineInfo(request);
            if (userOnlineInfo != null)
            {
                webUser = new WebUser(userOnlineInfo);
                session.setAttribute(SESSIONNAME, webUser);
            }
        }
        else
        {
            UserOnlineInfo userOnlineInfo = UserOnlineInfo.getUserOnlineInfo(request);
            if (userOnlineInfo == null)
            {
                webUser = null;
                session.removeAttribute(SESSIONNAME);
            }
        }

        if (webUser != null)
            return webUser;

        //记录当前页面，登录后能自动转向此页面
        String s = WebUtils.getOriginalRequestURL(request);

        if (!StringUtils.isEmpty(module))
        {
            request.getSession().setAttribute(WEB_USER_LOGIN_MODULE, module);
        }

        session.setAttribute(WebUser.WEBLOGINFORWARD, s);

        try
        {
            //转向登录页面
            response.sendRedirect("/web/login");
        }
        catch (Exception ex)
        {
            //页面无法转向，退出
            try
            {
                PrintWriter writer = response.getWriter();

                writer.println("<script>");
                writer.println("location.href=\"/web/login\";");
                writer.println("</script>");
            }
            catch (IOException ex1)
            {
                Tools.log(ex1);
            }
        }

        //用户没有登录，终止逻辑
        throw new WebNotLoginException();
    }

    public Integer getUserId()
    {
        return userOnlineInfo.getUserId();
    }

    public String getUserName()
    {
        return userOnlineInfo.getUserName();
    }

    public String getIp()
    {
        return userOnlineInfo.getIp();
    }

    public Date getLoginTime()
    {
        return userOnlineInfo.getLoginTime();
    }

    public String getLoginName()
    {
        return userOnlineInfo.getLoginName();
    }
}
