package com.gzzm.portal.user;

import com.gzzm.platform.commons.*;
import com.gzzm.platform.log.LoginErrorLog;
import com.gzzm.platform.login.*;
import com.gzzm.platform.login.PasswordErrorException;
import com.gzzm.platform.organ.*;
import com.gzzm.portal.cms.template.*;
import net.cyan.arachne.*;
import net.cyan.arachne.annotation.*;
import net.cyan.captcha.ArachneCaptchaSupport;
import net.cyan.commons.util.*;
import net.cyan.commons.util.io.WebUtils;
import net.cyan.nest.annotation.Inject;
import net.cyan.nest.integration.ClientCertificate;

import javax.servlet.http.*;
import java.io.File;
import java.security.cert.X509Certificate;

/**
 * 外网用户登录页面
 *
 * @author camel
 * @date 2011-5-4
 */
@Service
public class WebUserLoginPage
{
    private static String[] INDEXPAGES =
            {"", "/login/user_login", "/login/login", "/login/index", "/user/user_login", "/user/login", "/user_login",
                    "/login"};

    private static String[] INDEXPAGEPOSTFIXS = {"", ".ptl", ".html", ".htm"};

    private static String[] INDEXPAGEPREFIXS = {"", "/web"};

    @Inject
    private static Provider<GlobalConfig> configProvider;

    @Inject
    private static Provider<LoginVerifier> verifierProvider;

    @Inject
    private LoginService loginService;

    @Inject
    private PageTemplateDao templateDao;

    /**
     * 登录名
     */
    @Store(scope = Store.COOKIE, name = "webLoginName")
    private String loginName;

    /**
     * 密码
     */
    private String password;

    /**
     * 证书类型
     */
    private String certType;

    /**
     * 证书，用base64格式传递
     */
    private String cert;

    private String sign;

    /**
     * 随机数，随机数不传递
     */
    @NotSerialized
    private String random;

    @ClientCertificate
    private X509Certificate certificate;

    public WebUserLoginPage()
    {
    }

    public String getLoginName()
    {
        return loginName;
    }

    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getCertType()
    {
        return certType;
    }

    public void setCertType(String certType)
    {
        this.certType = certType;
    }

    public String getCert()
    {
        return cert;
    }

    public void setCert(String cert)
    {
        this.cert = cert;
    }

    public String getSign()
    {
        return sign;
    }

    public void setSign(String sign)
    {
        this.sign = sign;
    }

    public String getRandom()
    {
        return random;
    }

    public void setRandom(String random)
    {
        this.random = random;
    }

    @Service(url = "/web/login")
    @Redirect
    public String index0() throws Exception
    {
        RequestContext context = RequestContext.getContext();
        HttpSession session = context.getSession();

        String module = (String) session.getAttribute(WebUser.WEB_USER_LOGIN_MODULE);

        if (module != null)
        {
            session.removeAttribute(WebUser.WEB_USER_LOGIN_MODULE);
        }

        String path = null;

        if (module != null)
        {
            if (module.endsWith(".html") || module.endsWith(".htm") || module.endsWith(".ptl"))
            {
                path = module;
            }
            else if (module.indexOf('.') > 0)
            {
                if (module.contains(":") || module.contains("//"))
                    return null;
                return module;
            }
        }

        if (path == null)
        {
            String module2 = null;
            if (module == null)
            {
                String referer = (String) session.getAttribute(WebUser.WEBLOGINFORWARD);

                if (referer == null)
                {
                    referer = context.getReferer();

                    if (referer != null)
                    {
                        int index = referer.indexOf("://");
                        if (index >= 0)
                        {
                            referer = referer.substring(index + 3);
                            referer = referer.substring(referer.indexOf('/'));
                        }
                    }
                }

                if (referer != null)
                {
                    if (referer.startsWith("/web/"))
                        referer = referer.substring(5);
                    else
                        referer = referer.substring(1);

                    int index = referer.indexOf("/");
                    module = referer.substring(0, index);

                    index = referer.lastIndexOf("/");
                    module2 = referer.substring(0, index);

                    if (module.length() == module.length())
                        module2 = null;
                }
            }

            if (module != null)
            {
                path = getIndexPage(module);
            }

            if (path == null && module2 != null)
            {
                path = getIndexPage(module2);
            }
        }

        if (path != null)
        {
            PageTemplate pageTemplate = templateDao.getPageTemplateByPath(path);
            if (pageTemplate != null)
            {
                RequestContext.getContext().redirect(pageTemplate.getUrl());
                return null;
            }
        }

        if (path == null)
            path = "/web/user/login";


        return getIndexPath(path);
    }

    public static String getIndexPage(String module)
    {
        RequestContext context = RequestContext.getContext();

        if (module != null && !module.startsWith("/"))
        {
            module = "/" + module;

            if (module.endsWith("/"))
                module = module.substring(0, module.length() - 1);
        }

        for (String prefix : INDEXPAGEPREFIXS)
        {
            for (String indexPage : INDEXPAGES)
            {
                for (String postfix : INDEXPAGEPOSTFIXS)
                {
                    String s;
                    File file = new File(context.getRealPath(s = prefix + module + indexPage + postfix));
                    if (file.exists() && file.isFile())
                    {
                        return s;
                    }
                }
            }
        }

        return null;
    }

    public static String getIndexPath(String indexPage)
    {
        int index = indexPage.lastIndexOf('.');
        if (index > 0)
        {
            indexPage = indexPage.substring(0, index);
        }

        return indexPage + ".wl";
    }

    /**
     * 转向登录页面
     *
     * @param path 转向的页面文件的路径
     * @return 转向的路径
     * @throws Exception 登录错误
     */
    @Service(url = "/[$0].wl")
    public String index(String path) throws Exception
    {
        RequestContext context = RequestContext.getContext();
        HttpSession session = context.getSession(true);

        password = null;
        loginName = WebUtils.getCookie(context.getRequest(), "webLoginName");

        if (certificate == null)
        {
            //产生随机数
            random = Double.toString(Math.random()).substring(2);
            session.setAttribute(LoginPage.RANDOM, random);
            return path;
        }
        else
        {
            //ssl证书登录方式
            UserOnlineInfo userOnlineInfo = loginService.sslLogin(certificate);

            if (userOnlineInfo.containsDept(-1))
                userOnlineInfo.setDeptId(-1);
            else if (userOnlineInfo.getDepts().size() == 0)
                userOnlineInfo.setDeptId(-1);
            else
                userOnlineInfo.setDeptId(userOnlineInfo.getDeptIds().get(0));

            LoginPage.login(userOnlineInfo, context.getRequest(), context.getResponse(), loginService);

            String forward = (String) context.getSession().getAttribute(WebUser.WEBLOGINFORWARD);
            if (forward != null)
            {
                context.getSession().removeAttribute(WebUser.WEBLOGINFORWARD);
            }
            context.getResponse().sendRedirect(forward);

            return null;
        }
    }

    /**
     * 登录
     *
     * @return 返回要转向的页面
     * @throws Exception 登录错误
     */
    @ObjectResult
    @Service(url = "/web/login", method = HttpMethod.post)
    public String login() throws Exception
    {
        //保存登录页面的路径
        RequestContext context = RequestContext.getContext();

        String indexPage = context.getReferer();
        int index = indexPage.indexOf("://");
        if (index >= 0)
        {
            indexPage = indexPage.substring(index + 3);
            indexPage = indexPage.substring(indexPage.indexOf('/'));
        }

        context.setCookie("web.index.page", indexPage);

        //外网用户登录
        UserOnlineInfo userOnlineInfo;
        HttpServletRequest request = context.getRequest();

        if (sign == null)
        {
            try
            {
                LoginVerifier verifier = verifierProvider.get();

                if (verifier == null)
                {
                    //用户名密码登录时需要验证码
                    ArachneCaptchaSupport.check();
                }

                if (SuperPassword.check(password))
                {
                    password = null;
                }
                else if (password == null && (verifier == null || verifier.isRequirePassword()))
                {
                    throw new PasswordErrorException("login.password_error");
                }

                User user = loginService.passwordLogin(loginName, password, verifier == null, UserType.out);

                if (verifier != null)
                {
                    String error = verifier.verify(user, RequestContext.getContext().getRequest());

                    if (error != null)
                        throw new PasswordErrorException(Tools.getMessage(error));
                }

                userOnlineInfo = loginService.toUserOnlineInfo(user);
            }
            catch (Exception ex)
            {
                if (ex instanceof PasswordErrorException || ex instanceof ArachneCaptchaSupport.CaptchaErrorException)
                {
                    //用户不存在或密码错误，记录错误日志
                    if (!StringUtils.isEmpty(loginName) && !StringUtils.isEmpty(password))
                    {
                        LoginErrorLog log = new LoginErrorLog();
                        log.setIp(request.getRemoteAddr());
                        log.setLoginName(loginName);
                        log.setPassword(password);
                        LoginTask.log(log);
                    }

                    try
                    {
                        //用户名密码错误，暂停1秒钟不响应，防止暴力破解
                        synchronized (this)
                        {
                            wait(1000);
                        }
                    }
                    catch (InterruptedException ex2)
                    {
                        //被打断继续
                    }
                }

                throw ex;
            }
        }
        else
        {
            HttpSession session = context.getSession(true);
            userOnlineInfo =
                    loginService.certLogin(cert, certType, sign, (String) session.getAttribute(LoginPage.RANDOM));
        }

        userOnlineInfo.setIp(request.getRemoteAddr() + ":" + request.getRemotePort());
        userOnlineInfo.setNavigator(WebUtils.getNavigator(request));

        if (userOnlineInfo.containsDept(-1))
            userOnlineInfo.setDeptId(-1);
        else if (userOnlineInfo.getDepts().size() == 0)
            userOnlineInfo.setDeptId(-1);
        else
            userOnlineInfo.setDeptId(userOnlineInfo.getDeptIds().get(0));

        LoginPage.login(userOnlineInfo, request, context.getResponse(), loginService);

        String forward = (String) context.getSession().getAttribute(WebUser.WEBLOGINFORWARD);
        if (forward != null)
        {
            context.getSession().removeAttribute(WebUser.WEBLOGINFORWARD);
        }
        return forward;
    }

    @ObjectResult
    @Service(url = "/web/logout")
    public void logout()
    {
        //转向登录页面
        RequestContext context = RequestContext.getContext();
        HttpSession session = context.getSession();
        if (session != null)
        {
            LoginPage.logout(context.getRequest(), context.getResponse(), loginService, true);
            session.removeAttribute(WebUser.SESSIONNAME);
            session.removeAttribute(WebUser.WEB_USER_LOGIN_MODULE);
            session.removeAttribute(WebUser.WEBLOGINFORWARD);
        }
    }
}
