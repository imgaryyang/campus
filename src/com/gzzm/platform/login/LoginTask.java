package com.gzzm.platform.login;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.log.*;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

import java.util.Date;

/**
 * 用户的登录和注销的任务，用于另起线程执行，避免登录成为瓶颈
 *
 * @author camel
 * @date 2009-7-24
 */
public class LoginTask implements Runnable
{
    /**
     * 日志操作
     */
    @Inject
    private static Provider<LogDao> logDaoProvider;

    /**
     * 在线用户列表
     */
    @Inject
    private static UserOnlineList userOnlineList;

    private UserOnlineInfo userOnlineInfo;

    private LoginAction loginAction;

    public LoginTask(UserOnlineInfo userOnlineInfo, LoginAction loginAction)
    {
        this.userOnlineInfo = userOnlineInfo;
        this.loginAction = loginAction;
    }

    public void run()
    {
        if (loginAction == LoginAction.login || loginAction == LoginAction.recover)
            login(userOnlineInfo);
        else
            logout(userOnlineInfo, loginAction);
    }

    public void login(UserOnlineInfo userOnlineInfo)
    {
        LogDao dao = logDaoProvider.get();

        LoginTime loginTime = new LoginTime();
        loginTime.setUserId(userOnlineInfo.getUserId());
        loginTime.setLoginTime(userOnlineInfo.getLoginTime());

        try
        {
            dao.save(loginTime);
        }
        catch (Throwable ex)
        {
            //记录离线时间错误跳过
            Tools.log(ex);
        }

        //记录日志
        LoginLog log = new LoginLog();
        log.fill(userOnlineInfo);
        log.setLoginAction(loginAction);

        try
        {
            dao.log(log);
        }
        catch (Throwable ex)
        {
            //记录日志失败不处理
            Tools.log(ex);
        }
    }

    /**
     * 注销
     *
     * @param userOnlineInfo 用户信息
     * @param loginAction    注销的动作，必须是logout expire kickout的一个
     */
    public static void logout(UserOnlineInfo userOnlineInfo, LoginAction loginAction)
    {
        //从在线列表中删除
        userOnlineList.remove(userOnlineInfo);

        LogDao dao = logDaoProvider.get();

        LoginTime loginTime = new LoginTime();
        loginTime.setUserId(userOnlineInfo.getUserId());
        loginTime.setLogoutTime(new Date());

        try
        {
            dao.save(loginTime);
        }
        catch (Throwable ex)
        {
            //记录离线时间错误跳过
            Tools.log(ex);
        }

        //记录日志
        LoginLog log = new LoginLog();
        log.fill(userOnlineInfo);
        log.setLoginAction(loginAction);

        try
        {
            dao.log(log);
        }
        catch (Throwable ex)
        {
            //记录日志失败不处理
            Tools.log(ex);
        }

        if (loginAction == LoginAction.kickout)
        {
            try
            {
                dao.delete(Login.class, userOnlineInfo.getId());
            }
            catch (Throwable ex)
            {
                //删除登录信息失败不处理
                Tools.log(ex);
            }
        }
    }

    public static void log(Log log)
    {
        try
        {
            logDaoProvider.get().log(log);
        }
        catch (Throwable ex)
        {
            //记录日志失败不处理
            Tools.log(ex);
        }
    }
}
