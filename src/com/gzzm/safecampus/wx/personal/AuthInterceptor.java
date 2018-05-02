package com.gzzm.safecampus.wx.personal;


import com.gzzm.platform.commons.Tools;
import com.gzzm.safecampus.campus.wx.menu.WxMenuAuth;
import com.gzzm.safecampus.wx.user.WxLoginService;
import com.gzzm.safecampus.wx.user.WxUserOnlineInfo;
import net.cyan.commons.util.Provider;
import net.cyan.commons.util.io.WebUtils;
import net.cyan.nest.annotation.Inject;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 认证拦截器
 *
 * @author yiuman
 * @date 2018/3/21
 */
public class AuthInterceptor implements Filter
{
    @Inject
    private static Provider<ExcludeFilter> excludeFilterProvider;

    @Inject
    private static Provider<WxLoginService> wxLoginServiceProvider;

    @Inject
    private static Provider<WxMenuAuth> wxMenuAuthProvider;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException
    {
        HttpServletRequest httpRequest = ((HttpServletRequest) servletRequest);
        HttpServletResponse httpResponse = ((HttpServletResponse) servletResponse);
        String uri = WebUtils.getRequestURI(httpRequest);
        try
        {
            //不拦截该类请求
            if (excludeFilterProvider.get().accept(uri))
            {
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }
        } catch (Exception e)
        {
            Tools.log(e);
        }
        wxLoginServiceProvider.get().autoLogin(httpRequest);
        //从session中获取微信用户
        HttpSession session = httpRequest.getSession();
        WxUserOnlineInfo wxUserOnlineInfo = (WxUserOnlineInfo) session.getAttribute(WxUserOnlineInfo.SESSIONNAME);
        if (wxUserOnlineInfo == null)
        {
            //定向到非法请求页面
            httpResponse.sendError(400);
            return;
        } else
        {
            try
            {
                if (!wxMenuAuthProvider.get().accept(httpRequest))
                {
                    httpResponse.sendRedirect(CampusContants.NOAUTHPAGE);
                    return;
                }
            } catch (Exception e)
            {
                Tools.log(e);
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy()
    {

    }
}
