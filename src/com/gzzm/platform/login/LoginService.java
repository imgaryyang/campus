package com.gzzm.platform.login;

import com.gzzm.platform.commons.*;
import com.gzzm.platform.organ.*;
import net.cyan.commons.util.StringUtils;
import net.cyan.nest.annotation.Inject;

import java.security.cert.X509Certificate;
import java.util.*;

/**
 * 登录的service
 *
 * @author camel
 * @date 2009-7-23
 */
public class LoginService
{
    @Inject
    private OrganDao organDao;

    @Inject
    private LoginDao loginDao;

    /**
     * 注入ca服务列表
     */
    @Inject
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private List<CertLoginServiceItem> certServices;

    public LoginService()
    {
    }

    public OrganDao getOrganDao()
    {
        return organDao;
    }

    public LoginDao getLoginDao()
    {
        return loginDao;
    }

    /**
     * 用户名密码登录
     *
     * @param loginName   登录名
     * @param password    密码
     * @param requireCert 是否要求整数
     * @param userType    用户类型，外网用户或者内网用户
     * @return 用户信息
     * @throws Exception 异常
     */
    public User passwordLogin(String loginName, String password, boolean requireCert, UserType userType)
            throws Exception
    {
        User user = organDao.getUserByLoginName(loginName, userType);

        //用户不存在
        if (user == null)
            throw new PasswordErrorException("login.no_user");

        //用户状态无效
        if (user.getState().intValue() != 0)
            throw new NoErrorException("login.state_illegal");

        if (password != null)
        {
            //要求用证书登录
            if (requireCert && (user.getLoginType() == LoginType.cert ||
                    user.getLoginType() == LoginType.auto && !StringUtils.isEmpty(user.getCertId())))
                throw new NoErrorException("login.require_cert");

            //密码错误
            if (!PasswordUtils.checkPassword(password, user.getPassword(), user.getUserId()))
                throw new PasswordErrorException("login.password_error");
        }

        return user;
    }

    /**
     * 证书登录
     *
     * @param cert     证书信息
     * @param certType 证书类型
     * @param sign     用户证书对随机数的签名
     * @param random   随机数
     * @return 用户信息
     * @throws Exception 登录错误
     */
    public UserOnlineInfo certLogin(String cert, String certType, String sign, String random) throws Exception
    {
        if (StringUtils.isEmpty(random))
            throw new NoErrorException("login.no_random");

        CertLoginService certLoginService = null;

        for (CertLoginServiceItem item : certServices)
        {
            if (item.getType().equals(certType))
            {
                certLoginService = item.getService();
                break;
            }
        }

        if (certLoginService == null)
            throw new SystemException("service for cert " + certType + " is undefined");

        CertUserInfo certUserInfo = certLoginService.check(cert, sign, random);

        return getUserInfoForCert(certUserInfo, certType);
    }

    public UserOnlineInfo sslLogin(X509Certificate certificate) throws Exception
    {
        CertLoginService certLoginService = null;
        String certType = null;
        if (certServices != null)
        {
            for (CertLoginServiceItem item : certServices)
            {
                if (item.getService().accpet(certificate))
                {
                    certLoginService = item.getService();
                    break;
                }
            }
        }

        if (certLoginService == null)
            throw new SystemException("service for cert is undefined:" + certificate);

        CertUserInfo certUserInfo = certLoginService.getUserInfo(certificate);

        return getUserInfoForCert(certUserInfo, certType);
    }

    private UserOnlineInfo getUserInfoForCert(CertUserInfo certUserInfo, String certType) throws Exception
    {
        User user = null;
        String certId = null;

        for (String id : certUserInfo.getCertIds())
        {
            if (!StringUtils.isEmpty(id))
            {
                Tools.debug("certId:" + id);

                user = organDao.getUserByCert(id, certType);
                if (user != null)
                {
                    certId = id;
                    break;
                }
            }
        }

        //用户不存在
        if (user == null)
            throw new NoErrorException("login.no_user_for_cert");

        //用户状态无效
        if (user.getState().intValue() != 0)
            throw new NoErrorException("login.state_illegal");

        //不允许用证书登录
        if (user.getLoginType() == LoginType.password)
            throw new NoErrorException("login.cert_rejected");

        UserOnlineInfo userOnlineInfo = toUserOnlineInfo(user);
        userOnlineInfo.setCertType(certType);
        userOnlineInfo.setCertId(certId);
        userOnlineInfo.setCertName(certUserInfo.getCertName());

        return userOnlineInfo;
    }

    public UserOnlineInfo autoLogin(String loginId) throws Exception
    {
        Login login = getLogin(loginId);

        if (login == null)
        {
            return null;
        }
        else
        {
            if (login.getLoginTime() == null)
                return null;

            if (System.currentTimeMillis() - login.getLoginTime().getTime() > 1000 * 60 * 60 * 12)
                return null;


            //数据库中由此登录记录，自动登录
            UserOnlineInfo userOnlineInfo = getUserOnlineInfoForUserId(login.getUserId());
            userOnlineInfo.setDeptId(login.getDeptId());
            userOnlineInfo.setLoginTime(login.getLoginTime());
            userOnlineInfo.setId(login.getLoginId());
            userOnlineInfo.setSystemId(login.getSystemId());

            return userOnlineInfo;
        }
    }

    private UserOnlineInfo getUserOnlineInfoForUserId(Integer userId) throws Exception
    {
        User user = organDao.getUser(userId);

        return toUserOnlineInfo(user);
    }

    public UserOnlineInfo toUserOnlineInfo(User user) throws Exception
    {
        UserOnlineInfo userOnlineInfo = new UserOnlineInfo();
        userOnlineInfo.setUserId(user.getUserId());
        userOnlineInfo.setUserName(user.getUserName());
        userOnlineInfo.setLoginTime(new Date());
        userOnlineInfo.setAdmin(user.getAdminUser());
        userOnlineInfo.setUserType(user.getType());
        userOnlineInfo.setDeptId(organDao.getDefaultDeptId(user.getUserId()));
        userOnlineInfo.setAllDeptData(user.getDeptDataType() != null && user.getDeptDataType() == 1);

        if (userOnlineInfo.getDeptId() == null)
        {
            List<Integer> deptIds = userOnlineInfo.getDeptIds();

            if (user.getType() == UserType.in && deptIds.size() == 0)
                throw new NoErrorException("login.no_dept");

            if (deptIds.size() == 1)
                userOnlineInfo.setDeptId(deptIds.get(0));
        }

        return userOnlineInfo;
    }

    public String saveLogin(UserOnlineInfo userOnlineInfo) throws Exception
    {
        Login login = new Login();
        login.setUserId(userOnlineInfo.getUserId());
        login.setDeptId(userOnlineInfo.getDeptId());
        login.setLoginTime(userOnlineInfo.getLoginTime());

        organDao.add(login);

        return login.getLoginId();
    }

    public void updateLogin(String loginId, Integer deptId) throws Exception
    {
        Login login = new Login();
        login.setLoginId(loginId);
        login.setDeptId(deptId);

        organDao.update(login);
    }

    public Login getLogin(String loginId) throws Exception
    {
        return organDao.load(Login.class, loginId);
    }

    public void removeLogin(String loginId) throws Exception
    {
        organDao.delete(Login.class, loginId);
    }

    public void changePassword(Integer userId, String oldPassword, String password) throws Exception
    {
        User user = organDao.getUser(userId);

        //密码错误
        if (!PasswordUtils.checkPassword(oldPassword, user.getPassword(), userId))
            throw new PasswordErrorException("login.oldPassword_error");

        //md5密码
        user.setPassword(PasswordUtils.hashPassword(password, userId));

        organDao.update(user);
    }

    public boolean checkPassword(Integer userId, String password) throws Exception
    {
        User user = organDao.getUser(userId);

        return PasswordUtils.checkPassword(password, user.getPassword(), userId);
    }
}
