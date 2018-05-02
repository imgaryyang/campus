package com.gzzm.platform.message;

/**
 * 邮件消息配置
 * @author camel
 * @date 2010-6-16
 */
public class EMailConfig
{
    /**
     * smtp服务地址
     */
    private String smtphost;

    /**
     * 登录smtp服务器的用户名
     */
    private String userName;

    /**
     * 登录smtp服务器的密码
     */
    private String password;

    /**
     * 邮件发送人
     */
    private String from;
    
    public EMailConfig()
    {
    }

    public String getSmtphost()
    {
        return smtphost;
    }

    public void setSmtphost(String smtphost)
    {
        this.smtphost = smtphost;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getFrom()
    {
        return from;
    }

    public void setFrom(String from)
    {
        this.from = from;
    }
}
