package com.gzzm.platform.commons;

import net.cyan.nest.annotation.*;

import java.net.*;
import java.util.Map;

/**
 * 系统配置，配置一些和实施地相关的全局配置信息
 *
 * @author camel
 * @date 2009-7-22
 */
@Injectable(singleton = true)
public class GlobalConfig
{
    /**
     * 登录的时候是否需要验证码
     */
    private boolean loginCaptcha = true;

    /**
     * 登陆时是否踢掉其他登录信息
     */
    private boolean logoutOthers = false;

    /**
     * 默认密码为888;
     */
    private String defaultPassword = "888";

    /**
     * 服务器的名称
     */
    private String serverName;

    /**
     * 系统每页记录数
     */
    private int pageSize = 30;

    /**
     * 简单列表页面的每页记录数
     */
    private int simpleListPageSize = 7;

    /**
     * 是否对密码加盐，考虑到要把密码同步回旧系统，有些系统不加盐
     */
    private boolean saltPassword;

    /**
     * 各系统的信息，通过注入进来
     */
    @Inject
    private Map<String, SystemInfo> systems;

    public GlobalConfig()
    {
    }

    public synchronized String getServerName()
    {
        if (serverName == null)
        {
            try
            {
                serverName = InetAddress.getLocalHost().getHostName();
            }
            catch (UnknownHostException ex)
            {
                //没有网卡
                serverName = "";
            }
        }
        return serverName;
    }

    public void setServerName(String serverName)
    {
        this.serverName = serverName;
    }

    public boolean isLoginCaptcha()
    {
        return loginCaptcha;
    }

    public void setLoginCaptcha(boolean loginCaptcha)
    {
        this.loginCaptcha = loginCaptcha;
    }

    public boolean isLogoutOthers()
    {
        return logoutOthers;
    }

    public void setLogoutOthers(boolean logoutOthers)
    {
        this.logoutOthers = logoutOthers;
    }

    public String getDefaultPassword()
    {
        return defaultPassword;
    }

    public void setDefaultPassword(String defaultPassword)
    {
        this.defaultPassword = defaultPassword;
    }

    public int getPageSize()
    {
        return pageSize;
    }

    public void setPageSize(int pageSize)
    {
        this.pageSize = pageSize;
    }

    public int getSimpleListPageSize()
    {
        return simpleListPageSize;
    }

    public void setSimpleListPageSize(int simpleListPageSize)
    {
        this.simpleListPageSize = simpleListPageSize;
    }

    public boolean isSaltPassword()
    {
        return saltPassword;
    }

    public void setSaltPassword(boolean saltPassword)
    {
        this.saltPassword = saltPassword;
    }

    public Map<String, SystemInfo> getSystems()
    {
        return systems;
    }

    public int getPageSize(String systemId)
    {
        if (systems != null)
        {
            SystemInfo system = systems.get(systemId);
            if (system != null)
            {
                int pageSize = system.getPageSize();
                if (pageSize > 0)
                    return pageSize;
            }
        }

        return pageSize;
    }

    public String getSystemName(String systemId)
    {
        if (systems != null)
        {
            SystemInfo system = systems.get(systemId);
            if (system != null)
            {
                String systemName = system.getSystemName();
                if (systemName != null)
                    return systemName;
            }
        }

        return systemId;
    }
}
