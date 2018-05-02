package com.gzzm.platform.login;

import com.gzzm.platform.organ.User;
import net.cyan.thunwind.annotation.Entity;

import java.util.Date;

/**
 * 记录用户最后一次登录的时间
 *
 * @author camel
 * @date 2018/1/30
 */
@Entity(table = "PFLOGINTIME", keys = "userId")
public class LoginTime
{
    /**
     * 登录的用户ID
     */
    private Integer userId;

    /**
     * 关联user对象
     */
    private User user;

    private Date loginTime;

    private Date logoutTime;

    public LoginTime()
    {
    }

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public Date getLoginTime()
    {
        return loginTime;
    }

    public void setLoginTime(Date loginTime)
    {
        this.loginTime = loginTime;
    }

    public Date getLogoutTime()
    {
        return logoutTime;
    }

    public void setLogoutTime(Date logoutTime)
    {
        this.logoutTime = logoutTime;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof LoginTime))
            return false;

        LoginTime loginTime = (LoginTime) o;

        return userId.equals(loginTime.userId);
    }

    @Override
    public int hashCode()
    {
        return userId.hashCode();
    }
}
