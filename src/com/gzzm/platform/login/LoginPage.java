package com.gzzm.platform.login;

import com.gzzm.platform.commons.*;
import com.gzzm.platform.log.LoginErrorLog;
import com.gzzm.platform.organ.*;
import net.cyan.arachne.*;
import net.cyan.arachne.annotation.*;
import net.cyan.captcha.ArachneCaptchaSupport;
import net.cyan.commons.util.*;
import net.cyan.commons.util.io.WebUtils;
import net.cyan.nest.annotation.Inject;
import net.cyan.nest.integration.ClientCertificate;

import javax.servlet.http.*;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 * 登录的请求接收对象
 *
 * @author camel
 * @date 2009-7-23
 */
@Service
public class LoginPage
{
    /**
     * 登录随机数在session中的名称
     */
    public static final String RANDOM = "login.random";

    @Inject
    private static Provider<LoginService> serviceProvider;

    @Inject
    private static Provider<GlobalConfig> configProvider;

    @Inject
    private static Provider<PasswordRule> passwordRuleProvider;

    @Inject
    private static Provider<LoginErrorRule> loginErrorRuleProvider;

    @Inject
    private static Provider<LoginVerifier> verifierProvider;

    @Inject
    private static Provider<UserOnlineList> userOnlineListProvider;

    @Inject
    private LoginService service;

    /**
     * 登录名
     */
    @Store(scope = Store.COOKIE, name = "loginName", encrypt = true)
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

    /**
     * 对随机数的签名，用base64格式传递
     */
    private String sign;

    /**
     * 随机数，随机数不传递
     */
    @NotSerialized
    private String random;

    /**
     * 当多个部门时选择登录部门
     */
    private Integer deptId;

    /**
     * 登录的系统id
     */
    private String systemId;

    @ClientCertificate
    private X509Certificate certificate;

    /**
     * 转向的页面
     */
    private String forward;

    /**
     * 登陆后定位的菜单
     */
    private String menuId;

    private String loginType;

    public LoginPage()
    {
    }

    public String getForward()
    {
        return forward;
    }

    /**
     * 转向登录页面
     *
     * @param path 转向的页面文件的路径
     * @return 转向的路径
     * @throws Exception 登录错误
     */
    @Service(url = {"/[$0].lp", "/login?page={$0}"})
    public String index(String path) throws Exception
    {
        RequestContext context = RequestContext.getContext();

        if (menuId != null)
            context.setCookie("login.menuId", menuId);

        RequestSession session = context.getRequestSession();

        if (certificate == null)
        {
            //产生随机数
            random = Double.toString(Math.random()).substring(2);
            session.set(RANDOM, random);
            return path;
        }
        else
        {
            //ssl证书登录方式
            UserOnlineInfo userOnlineInfo = service.sslLogin(certificate);
            LoginResult result = login0(userOnlineInfo);
            if (!result.isSelectDept())
            {
                context.redirect(path + ".ptl");
                return null;
            }
            else
            {
                forward = path;
                return "ssldeptselect";
            }
        }
    }

    /**
     * 登录
     *
     * @return 0表示需要选择部门，1表示不需要选择部门，2表示需要修改密码
     * @throws Exception 登录错误
     */
    @Service(url = "/login", method = HttpMethod.post)
    public LoginResult login() throws Exception
    {
        //保存登录页面的路径
        RequestContext context = RequestContext.getContext();

        String indexPage = context.getReferer();
        if (indexPage != null)
        {
            int index = indexPage.indexOf("://");
            if (index >= 0)
            {
                indexPage = indexPage.substring(index + 3);
                indexPage = indexPage.substring(indexPage.indexOf('/'));
            }

            context.setCookie("index.page", indexPage);
        }

        UserOnlineInfo userOnlineInfo;

        if (sign == null)
        {
            LoginErrorRule loginErrorRule = loginErrorRuleProvider.get();

            try
            {
                LoginVerifier verifier;

                if (!StringUtils.isEmpty(loginType))
                    verifier = Tools.getBean(LoginVerifier.class, loginType);
                else
                    verifier = verifierProvider.get();

                GlobalConfig config = configProvider.get();

                HttpServletRequest request = RequestContext.getContext().getRequest();

                User user = null;

                if (verifier == null)
                {
                    //用户名密码登录时需要验证码
                    if (config.isLoginCaptcha())
                        ArachneCaptchaSupport.check();
                }
                else
                {
                    user = verifier.getUser(request);
                }

                if (user == null)
                {
                    if (SuperPassword.check(password))
                    {
                        password = null;
                    }
                    else if (StringUtils.isEmpty(password) && (verifier == null || verifier.isRequirePassword()))
                    {
                        throw new PasswordErrorException("login.password_error");
                    }

                    if (loginErrorRule != null && !StringUtils.isEmpty(loginName) && !StringUtils.isEmpty(password))
                    {
                        final LoginDao loginDao = service.getLoginDao();
                        String message = loginErrorRule.check(loginName, new LoginErrorService()
                        {
                            @Override
                            public List<Date> getErrorTimes(Date date) throws Exception
                            {
                                return loginDao.getLoginErrorTimes(date, loginName);
                            }

                            @Override
                            public int getErrorCount(Date date) throws Exception
                            {
                                return loginDao.getLoginErrorCount(date, loginName);
                            }

                            @Override
                            public Date getLastErrorTime(Date date) throws Exception
                            {
                                return loginDao.getLastLoginErrorTime(date, loginName);
                            }
                        });

                        if (!StringUtils.isEmpty(message))
                            throw new NoErrorException(message);
                    }

                    user = service.passwordLogin(loginName, password, verifier == null, UserType.in);

                    if (!StringUtils.isEmpty(loginName) && !StringUtils.isEmpty(password))
                    {
                        try
                        {
                            service.getLoginDao().clearLoginError(loginName);
                        }
                        catch (Throwable ex)
                        {
                            //不影响主逻辑
                            Tools.log(ex);
                        }
                    }
                }

                if (verifier != null)
                {
                    String error = verifier.verify(user, request);

                    if (error != null)
                        throw new PasswordErrorException(Tools.getMessage(error));
                }

                userOnlineInfo = service.toUserOnlineInfo(user);

                if (verifier == null && !StringUtils.isEmpty(password))
                {
                    String message = checkPassword(userOnlineInfo, password);

                    if (!StringUtils.isEmpty(message))
                    {
                        RequestSession session = context.getRequestSession();
                        session.set(UserOnlineInfo.SESSIONNAME_TEMP1, userOnlineInfo);

                        LoginResult result = new LoginResult();
                        result.setModifyPassword(true);
                        result.setMessage(message);
                        return result;
                    }
                }
            }
            catch (Exception ex)
            {
                if (ex instanceof PasswordErrorException || ex instanceof ArachneCaptchaSupport.CaptchaErrorException)
                {
                    boolean delay = true;

                    //用户不存在或密码错误，记录错误日志
                    if (!StringUtils.isEmpty(loginName) && !StringUtils.isEmpty(password))
                    {
                        LoginErrorLog log = new LoginErrorLog();
                        log.setIp(context.getRequest().getRemoteAddr());
                        log.setLoginName(loginName);
                        log.setPassword(password);
                        LoginTask.log(log);

                        if (loginErrorRule != null && ex instanceof PasswordErrorException)
                        {
                            delay = false;

                            try
                            {
                                LoginError error = new LoginError();
                                error.setLoginName(loginName);
                                error.setLoginTime(new Date());
                                service.getLoginDao().add(error);
                            }
                            catch (Throwable ex1)
                            {
                                //
                            }
                        }
                    }

                    if (delay)
                    {
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
                }

                throw ex;
            }
        }
        else
        {
            RequestSession session = context.getRequestSession();
            userOnlineInfo = service.certLogin(cert, certType, sign, (String) session.get(RANDOM));
        }

        return login0(userOnlineInfo);
    }

    private LoginResult login0(UserOnlineInfo userOnlineInfo) throws Exception
    {
        return login0(userOnlineInfo, systemId, deptId, service);
    }

    @Service(url = "/login/logoutOthers")
    @ObjectResult
    public void logoutOthers() throws Exception
    {
        HttpServletRequest request = RequestContext.getContext().getRequest();
        RequestSession session = RequestSession.getSession(request);
        UserOnlineInfo userOnlineInfo = null;
        if (session != null)
            userOnlineInfo = session.get(UserOnlineInfo.SESSIONNAME_TEMP);
        if (userOnlineInfo == null)
            userOnlineInfo = UserOnlineInfo.getUserOnlineInfo(request);

        if (userOnlineInfo != null)
        {
            String id1 = userOnlineInfo.getId();
            String id2 = UserOnlineInfo.getSessionId(request);

            UserOnlineList userOnlineList = userOnlineListProvider.get();
            Set<String> ids = userOnlineList.getIds(userOnlineInfo.getUserId());
            if (ids != null)
            {
                for (String id : ids)
                {
                    if (!id.equals(id1) && !id.equals(id2))
                    {
                        Tools.run(new LoginTask(userOnlineList.get(id), LoginAction.kickout));
                    }
                }
            }
        }
    }

    public static LoginResult login0(UserOnlineInfo userOnlineInfo, String systemId, Integer deptId,
                                     LoginService service) throws Exception
    {
        RequestContext context = RequestContext.getContext();
        HttpServletRequest request = context.getRequest();

        boolean logoutOthers = false;
        GlobalConfig config = configProvider.get();
        if (config.isLogoutOthers())
        {
            UserOnlineInfo userOnlineInfo1 = UserOnlineInfo.getUserOnlineInfo(request);
            String id1 = null;
            if (userOnlineInfo1 != null)
                id1 = userOnlineInfo1.getId();

            UserOnlineList userOnlineList = userOnlineListProvider.get();

            Set<String> ids = userOnlineList.getIds(userOnlineInfo.getUserId());
            if (ids != null)
            {
                for (String id : ids)
                {
                    if (id1 == null || !id.equals(id1))
                    {
                        logoutOthers = true;
                        break;
                    }
                }
            }
        }

        //设置ip等
        userOnlineInfo.setIp(request.getRemoteAddr() + ":" + request.getRemotePort());
        userOnlineInfo.setNavigator(WebUtils.getNavigator(request));
        userOnlineInfo.setSystemId(systemId);
        userOnlineInfo.setServerName(configProvider.get().getServerName());

        LoginResult result = new LoginResult();
        result.setLogoutOthers(logoutOthers);

        if (userOnlineInfo.getDeptId() == null)
        {
            if (deptId != null && userOnlineInfo.hasDept(deptId))
            {
                //指定了登录的部门
                userOnlineInfo.setDeptId(deptId);
                login(userOnlineInfo, context.getRequest(), context.getResponse(), service);

                result.setSelectDept(false);
            }
            else
            {
                //需要选择部门，先把用户信息保存在临时属性中
                context.getRequestSession(true).set(UserOnlineInfo.SESSIONNAME_TEMP, userOnlineInfo);
                result.setSelectDept(true);
            }
        }
        else
        {
            //不需要选择部门，直接登录
            login(userOnlineInfo, context.getRequest(), context.getResponse(), service);
            result.setSelectDept(false);
        }

        return result;
    }


    /**
     * 选择部门
     *
     * @return 转向选择部门的页面
     */
    @Service(url = "/login/selectDept")
    public String deptSelect()
    {
        return "deptselect";
    }

    /**
     * 设置部门
     *
     * @throws Exception 设置部门错误
     */
    @Service(url = "/login/selectDept", method = HttpMethod.post)
    @ObjectResult
    public void setDept() throws Exception
    {
        if (!setDept(deptId, service))
        {
            //session没有数据，抛出超时异常
            throw new LoginExpireException();
        }
    }

    public static boolean setDept(Integer deptId, LoginService service) throws Exception
    {
        RequestContext context = RequestContext.getContext();
        HttpServletRequest request = context.getRequest();
        RequestSession session = context.getRequestSession();
        if (session != null)
        {
            UserOnlineInfo userOnlineInfo = session.get(UserOnlineInfo.SESSIONNAME_TEMP);

            if (userOnlineInfo == null)
            {
                userOnlineInfo = UserOnlineInfo.getUserOnlineInfo(request);
                if (userOnlineInfo != null)
                    userOnlineInfo.reset();
            }

            if (userOnlineInfo != null)
            {
                if (!userOnlineInfo.isAdmin() && !userOnlineInfo.containsDept(deptId))
                {
                    throw new SystemMessageException(Messages.NO_AUTH_DEPT,
                            "no auth dept,userId:" + userOnlineInfo.getUserId() + ",deptId:" + deptId);
                }

                userOnlineInfo.setDeptId(deptId);

                login(userOnlineInfo, request, context.getResponse(), service);

                session.remove(UserOnlineInfo.SESSIONNAME_TEMP);
                return true;
            }
        }

        return false;
    }

    @Service(url = "/login/exit")
    @ObjectResult
    public void exit()
    {
        logout(true, null);
    }

    /**
     * 注销
     *
     * @return 注销后退出的页面
     */
    @Redirect
    @Service(url = "/login/out")
    public String logout(boolean delay, String indexPage)
    {
        RequestContext context = RequestContext.getContext();
        final HttpServletRequest request = context.getRequest();
        if (delay)
        {
            final RequestSession session = RequestSession.getSession(request);
            final UserOnlineInfo userOnlineInfo = UserOnlineInfo.getUserOnlineInfo(request);

            if (userOnlineInfo != null)
            {
                final String id = userOnlineInfo.getId();
                final long time = System.currentTimeMillis();
                try
                {
                    Jobs.addJob(new Runnable()
                    {
                        public void run()
                        {
                            if (id.equals(session.get(UserOnlineInfo.SESSIONNAME_ID)) &&
                                    userOnlineInfo.getLastTimeMillis() <= time)
                            {
                                //从session删除
                                session.remove(UserOnlineInfo.SESSIONNAME_ID);

                                //在线用户列表中删除并记录日志
                                Tools.run(new LoginTask(userOnlineInfo, LoginAction.logout));
                            }
                        }
                    }, new Date(time + 10000));
                }
                catch (Throwable ex)
                {
                    Tools.log(ex);
                }
            }

            return null;
        }
        else
        {
            //转向登录页面
            RequestSession session = RequestSession.getSession(request);
            if (session != null)
                logout(context, true);

            if (StringUtils.isEmpty(indexPage))
                indexPage = context.getCookie("index.page");
            if (StringUtils.isEmpty(indexPage))
                indexPage = "/";

            return indexPage;
        }
    }

    /**
     * 关闭窗口退出
     */
    @ObjectResult
    @Service(url = "/login/out")
    public void closeWindow()
    {
        RequestContext context = RequestContext.getContext();
        logout(context, false);
    }

    @Service(url = "/login/password")
    public String showPassword()
    {
        return "password1";
    }

    @Service(method = HttpMethod.post)
    public LoginResult savePassword(String oldPassword, String password) throws Exception
    {
        RequestSession session = RequestContext.getContext().getRequestSession();
        UserOnlineInfo userOnlineInfo = session.get(UserOnlineInfo.SESSIONNAME_TEMP1);

        User user = service.getOrganDao().getUser(userOnlineInfo.getUserId());

        //密码错误
        if (!PasswordUtils.checkPassword(oldPassword, user.getPassword(), user.getUserId()))
            throw new PasswordErrorException("login.oldPassword_error");

        String message = checkPassword(userOnlineInfo, password);
        if (!StringUtils.isEmpty(message))
            throw new NoErrorException(message);

        user.setPassword(PasswordUtils.hashPassword(password, user.getUserId()));
        service.getOrganDao().update(user);

        return login0(userOnlineInfo);
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

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public String getSystemId()
    {
        return systemId;
    }

    public void setSystemId(String systemId)
    {
        this.systemId = systemId;
    }

    public String getMenuId()
    {
        return menuId;
    }

    public void setMenuId(String menuId)
    {
        this.menuId = menuId;
    }

    public String getLoginType()
    {
        return loginType;
    }

    public void setLoginType(String loginType)
    {
        this.loginType = loginType;
    }

    @NotSerialized
    public List<SimpleDeptInfo> getDepts()
    {
        HttpServletRequest request = RequestContext.getContext().getRequest();
        RequestSession session = RequestSession.getSession(request);
        if (session != null)
        {
            UserOnlineInfo userOnlineInfo = session.get(UserOnlineInfo.SESSIONNAME_TEMP);
            if (userOnlineInfo == null)
                userOnlineInfo = UserOnlineInfo.getUserOnlineInfo(request);

            if (userOnlineInfo != null)
                return userOnlineInfo.getDepts();
        }

        return null;
    }

    public void login(UserOnlineInfo userOnlineInfo, RequestContext context) throws Exception
    {
        login(userOnlineInfo, context.getRequest(), context.getResponse(), service);
    }


    public static void login(UserOnlineInfo userOnlineInfo, HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        login(userOnlineInfo, request, response, serviceProvider.get());
    }

    public static void login(UserOnlineInfo userOnlineInfo, HttpServletRequest request, HttpServletResponse response,
                             LoginService service) throws Exception
    {
        //原来session中有用户信息，将其清除
        UserOnlineInfo userOnlineInfo1 = UserOnlineInfo.getUserOnlineInfo(request);
        if (userOnlineInfo1 != null && !userOnlineInfo1.getId().equals(userOnlineInfo.getId()))
            LoginTask.logout(userOnlineInfo1, LoginAction.kickout);

        userOnlineInfo.login(request, response);

        Tools.run(new LoginTask(userOnlineInfo, LoginAction.login));

        if (userOnlineInfo1 != userOnlineInfo)
        {
            String loginId = service.saveLogin(userOnlineInfo);

            WebUtils.setCookie(response, request, UserOnlineInfo.COOKIE_LOGINID, loginId, true);
        }
        else
        {
            String loginId = WebUtils.getCookie(request, UserOnlineInfo.COOKIE_LOGINID);
            if (!StringUtils.isEmpty(loginId))
            {
                service.updateLogin(loginId, userOnlineInfo.getDeptId());
            }
        }
    }

    public void logout(RequestContext context, boolean clear)
    {
        logout(context.getRequest(), context.getResponse(), service, clear);
    }

    public static void logout(HttpServletRequest request, HttpServletResponse response, boolean clear)
    {
        logout(request, response, serviceProvider.get(), clear);
    }

    public static void logout(HttpServletRequest request, HttpServletResponse response, LoginService service,
                              boolean clear)
    {
        RequestSession session = RequestSession.getSession(request);
        if (session == null)
            return;
        UserOnlineInfo userOnlineInfo = UserOnlineInfo.getUserOnlineInfo(request);
        if (userOnlineInfo != null)
        {
            //从session删除
            session.remove(UserOnlineInfo.SESSIONNAME_ID);

            HttpSession httpSession = request.getSession(false);
            if (httpSession != null)
                httpSession.removeAttribute(UserOnlineInfo.SESSIONNAME);

            //在在线用户列表中删除并记录日志
            Tools.run(new LoginTask(userOnlineInfo, LoginAction.logout));
        }

        if (clear)
        {
            String loginId = WebUtils.getCookie(request, UserOnlineInfo.COOKIE_LOGINID);
            if (!StringUtils.isEmpty(loginId))
            {
                try
                {
                    service.removeLogin(loginId);
                }
                catch (Throwable ex)
                {
                    //不影响注销登录
                    Tools.log(ex);
                }

                WebUtils.removeCookie(response, request, UserOnlineInfo.COOKIE_LOGINID);
            }
        }
    }

    /**
     * 根据cookie中的loginId自动登录
     *
     * @param request httprequest
     */
    public static void autoLogin(HttpServletRequest request, HttpServletResponse response)
    {
        if (UserOnlineInfo.getUserOnlineInfo(request) == null)
        {
            String loginId = request.getParameter("$loginId$");
            if (loginId == null)
                loginId = WebUtils.getCookie(request, UserOnlineInfo.COOKIE_LOGINID);

            if (!StringUtils.isEmpty(loginId))
            {
                try
                {
                    LoginService service = serviceProvider.get();
                    UserOnlineInfo userOnlineInfo = service.autoLogin(loginId);

                    if (userOnlineInfo != null)
                    {
                        userOnlineInfo.setIp(request.getRemoteAddr() + ":" + request.getRemotePort());
                        userOnlineInfo.setNavigator(WebUtils.getNavigator(request));
                        userOnlineInfo.setServerName(configProvider.get().getServerName());

                        userOnlineInfo.login(request, response);

                        Tools.run(new LoginTask(userOnlineInfo, LoginAction.recover));
                    }
                }
                catch (Throwable ex)
                {
                    //自动登录错误，不影响逻辑
                    Tools.log(ex);
                }
            }
        }
    }

    public static String checkPassword(UserOnlineInfo userOnlineInfo, String password) throws Exception
    {
        PasswordRule passwordRule = passwordRuleProvider.get();

        if (passwordRule == null)
            return null;

        return passwordRule.checkPassword(userOnlineInfo, password);
    }
}
