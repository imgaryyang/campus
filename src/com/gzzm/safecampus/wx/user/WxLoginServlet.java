package com.gzzm.safecampus.wx.user;

import com.gzzm.safecampus.campus.wx.WxMpServiceProvider;
import com.gzzm.safecampus.wx.personal.CampusContants;
import com.gzzm.safecampus.wx.personal.WxAuthDao;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import net.cyan.commons.util.Provider;
import net.cyan.commons.util.SystemConfig;
import net.cyan.nest.annotation.Inject;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @author Neo
 * @date 2018/4/4 10:32
 */
public class WxLoginServlet extends HttpServlet
{
    private static final long serialVersionUID = 7495662748447576970L;

    @Inject
    private static Provider<WxLoginService> wxLoginServiceProvider;

    @Inject
    private static Provider<WxAuthDao> dao;

    public WxLoginServlet()
    {
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        process(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        process(request, response);
    }

    /**
     * 微信用户登录处理
     * 1、将需要用户登录的微信菜单的url定义为：
     * https://open.weixin.qq.com/connect/oauth2/authorize
     * ?appid=appId&redirect_uri=redirect_uri&response_type=code&scope=snsapi_userinfo&state=1#wechat_redirect
     * 其中redirect_uri统一定义为http://域名/wxlogin?redirectPath=redirectPath，redirectPath表示的是登录成功后跳转的地址
     * 2、登录处理流程
     * 从session中获取WxUserOnlineInfo，存在则直接跳转到redirectPath
     * 从request中获取到code和redirectPath
     * 使用code获取openId，根据openid查询该用户是否已认证，已认证则系统自动登录（将WxUserOnlineInfo存到session中），未认证则跳转到认证界面
     *
     * @throws ServletException
     * @throws IOException
     */
    protected void process(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        String code = request.getParameter("code");
        String redirectPath = request.getParameter("redirectPath");
        HttpSession session = request.getSession();
        //从session中获取微信用户
        WxUserOnlineInfo wxUserOnlineInfo = (WxUserOnlineInfo) session.getAttribute(WxUserOnlineInfo.SESSIONNAME);
        if (wxUserOnlineInfo == null || wxUserOnlineInfo.getOpenId()==null)
        {
            try
            {
                //未登录 先获取openId
                if (StringUtils.isNotEmpty(code))
                {
                    WxMpServiceImpl wxMpService = WxMpServiceProvider.getWxMpService();
                    WxMpOAuth2AccessToken token = wxMpService.oauth2getAccessToken(code);
                    WxMpUser wxMpUser = wxMpService.oauth2getUserInfo(token, "zh_CN");
                    //根据OpenId获取微信用户
                    System.out.println(wxMpUser.toString());
                    WxUser wxUser = dao.get().getWxUserByOpenId(wxMpUser.getOpenId());
                    if (wxUser != null)
                    {
                        //已认证，自动登录
                        wxLoginServiceProvider.get().login(wxUser, request, response);
                        response.sendRedirect(redirectPath);
                    } else
                    {
                        //未认证，跳转到认证页面
                        //将微信用户信息存起来，避免重复获取
                        session.setAttribute(CampusContants.WXMPUSER_SESSIONNAME, wxMpUser);
                        session.setAttribute(CampusContants.RECORD_URL, redirectPath);
                        response.sendRedirect(CampusContants.AUTH_URL);
                    }
                } else
                {
                    //没有拿到微信服务器返回的code，转向400，标识为非法请求
                    response.sendError(400);
                }
            } catch (Exception e)
            {
                SystemConfig.error(e);
            }
        } else
        {
            //用户已登录，直接重定向到目标页面  如果目标页面是认证页面则跳转到个人中心
            if(CampusContants.AUTH_URL.equals(redirectPath))  response.sendRedirect(CampusContants.PERCENTER_URL);
            response.sendRedirect(redirectPath);
        }
    }
}
