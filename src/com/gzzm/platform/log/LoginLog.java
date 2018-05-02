package com.gzzm.platform.log;

import com.gzzm.platform.login.LoginAction;
import net.cyan.thunwind.annotation.Entity;

/**
 * 登录日志
 *
 * @author camel
 * @date 2009-7-23
 */
@Entity(table = "PFLOGINLOG", keys = "logId")
public class LoginLog extends UserLog
{
    /**
     * 登录或者注销
     */
    private LoginAction loginAction;

    public LoginLog()
    {
    }

    public LoginAction getLoginAction()
    {
        return loginAction;
    }

    public void setLoginAction(LoginAction loginAction)
    {
        this.loginAction = loginAction;
    }

    public boolean equals(Object o)
    {
        return this == o || o instanceof LoginErrorLog && logId.equals(((Log) o).logId);
    }
}
