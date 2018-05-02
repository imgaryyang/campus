package com.gzzm.safecampus.wx.user;

import com.gzzm.platform.commons.Tools;
import com.gzzm.safecampus.wx.common.WxDao;
import net.cyan.arachne.RequestContext;
import net.cyan.commons.util.Provider;
import net.cyan.commons.util.StringUtils;
import net.cyan.commons.util.io.WebUtils;
import net.cyan.nest.annotation.Inject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author Neo
 * @date 2018/4/4 12:51
 */
public class WxLoginService
{
    @Inject
    private static Provider<WxDao> wxDaoProvider;

    public WxLoginService()
    {
    }

    public void login(WxUser wxUser, HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        WxUserOnlineInfo wxUserOnlineInfo = new WxUserOnlineInfo(wxUser);
        wxUserOnlineInfo.login(request.getSession());
        String loginId = saveLogin(wxUserOnlineInfo);
        WebUtils.setCookie(response, request, WxUserOnlineInfo.COOKIE_LOGINID, loginId, false);
    }

    public void autoLogin(HttpServletRequest request)
    {
        if (WxUserOnlineInfo.getWxUserOnlineInfo(request) == null)
        {
            String loginId = WebUtils.getCookie(request, WxUserOnlineInfo.COOKIE_LOGINID);

            if (!StringUtils.isEmpty(loginId))
            {
                try
                {
                    WxUserOnlineInfo wxUserOnlineInfo = autoLogin(loginId);

                    if (wxUserOnlineInfo != null)
                    {
                        wxUserOnlineInfo.login(request.getSession(true));
                    }
                } catch (Throwable ex)
                {
                    //自动登录错误，不影响逻辑
                    Tools.log(ex);
                }
            }
        }
    }


    public WxUserOnlineInfo autoLogin(String loginId) throws Exception
    {
        WxLogin wxLogin = getLogin(loginId);

        if (wxLogin == null)
        {
            return null;
        } else
        {
            if (wxLogin.getLoginTime() == null)
                return null;

            if (System.currentTimeMillis() - wxLogin.getLoginTime().getTime() > 1000 * 60 * 60 * 12)
                return null;

            //数据库中由此登录记录，自动登录
            WxUserOnlineInfo wxUserOnlineInfo = getWxUserOnlineInfoForUserId(wxLogin.getWxUserId());
            wxUserOnlineInfo.setLoginTime(wxLogin.getLoginTime());
            wxUserOnlineInfo.setId(wxLogin.getLoginId());
            return wxUserOnlineInfo;
        }
    }

    public String saveLogin(WxUserOnlineInfo wxUserOnlineInfo) throws Exception
    {
        WxLogin login = new WxLogin();
        login.setLoginId(wxUserOnlineInfo.getId());
        login.setWxUserId(wxUserOnlineInfo.getUserId());
        login.setLoginTime(wxUserOnlineInfo.getLoginTime());
        wxDaoProvider.get().add(login);

        return login.getLoginId();
    }

    public WxLogin getLogin(String loginId) throws Exception
    {
        return wxDaoProvider.get().load(WxLogin.class, loginId);
    }

    private WxUserOnlineInfo getWxUserOnlineInfoForUserId(Integer wxUserId) throws Exception
    {
        WxUser wxUser = wxDaoProvider.get().getWxUser(wxUserId);
        return new WxUserOnlineInfo(wxUser);
    }

    public void logout() throws Exception
    {
        RequestContext context = RequestContext.getContext();
        HttpServletRequest request = context.getRequest();
        HttpServletResponse response = context.getResponse();
        HttpSession session = request.getSession();
        if (session == null)
            return;
        WxUserOnlineInfo wxUserOnlineInfo = WxUserOnlineInfo.getWxUserOnlineInfo(request);
        if (wxUserOnlineInfo != null)
        {
            //从session删除
            session.removeAttribute(WxUserOnlineInfo.SESSIONNAME_ID);
            session.removeAttribute(WxUserOnlineInfo.SESSIONNAME);

            //在在线用户列表中删除
            WxUserOnlineList wxUserOnlineList = Tools.getBean(WxUserOnlineList.class);
            wxUserOnlineList.remove(wxUserOnlineInfo);
        }

        String loginId = WebUtils.getCookie(request, WxUserOnlineInfo.COOKIE_LOGINID);
        if (!StringUtils.isEmpty(loginId))
        {
            try
            {
                removeLogin(loginId);
            } catch (Throwable ex)
            {
                //不影响注销登录
                Tools.log(ex);
            }

            WebUtils.removeCookie(response, request, WxUserOnlineInfo.COOKIE_LOGINID);
        }
    }

    private void removeLogin(String loginId) throws Exception
    {
        wxDaoProvider.get().delete(WxLogin.class, loginId);
    }
}
