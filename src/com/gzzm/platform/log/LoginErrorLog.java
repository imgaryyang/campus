package com.gzzm.platform.log;

import net.cyan.thunwind.annotation.*;

/**
 * 登录错误，记录非法登录信息
 *
 * @author camel
 * @date 2009-7-30
 */
@Entity(table = "PFLOGINERRORLOG", keys = "logId")
public class LoginErrorLog extends Log
{
    /**
     * 登录用户名
     */
    @ColumnDescription(type = "varchar(250)", nullable = false)
    private String loginName;

    /**
     * 登录使用的密码
     */
    @ColumnDescription(type = "varchar(250)", nullable = false)
    private String password;

    /**
     * 登录ip
     */
    @ColumnDescription(type = "varchar(60)")
    private String ip;

    public LoginErrorLog()
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

    public String getIp()
    {
        return ip;
    }

    public void setIp(String ip)
    {
        this.ip = ip;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public boolean equals(Object o)
    {
        return this == o || o instanceof LoginErrorLog && logId.equals(((Log) o).logId);
    }
}
