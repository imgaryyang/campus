package com.gzzm.oa.mail;

import com.gzzm.platform.organ.User;
import net.cyan.commons.util.StringUtils;
import net.cyan.commons.validate.annotation.*;
import net.cyan.thunwind.annotation.*;

/**
 * 邮件帐号配置
 *
 * @author camel
 * @date 2010-4-2
 */
@Entity(table = "OAMAILACCOUNT", keys = "accountId")
public class MailAccount
{
    @Generatable(length = 9)
    private Integer accountId;

    /**
     * smtp服务为哪个用户所配置
     */
    private Integer userId;

    private User user;

    /**
     * 邮箱地址
     */
    @Pattern(Pattern.EMAIL)
    @Require
    @ColumnDescription(type = "varchar(100)", nullable = false)
    private String address;

    /**
     * 昵称
     */
    @ColumnDescription(type = "varchar(100)")
    private String nickName;

    /**
     * smtp服务器地址
     */
    @ColumnDescription(type = "varchar(100)")
    private String smtpServer;

    /**
     * pop3服务器地址
     */
    @ColumnDescription(type = "varchar(100)")
    private String pop3Server;

    /**
     * 登录服务器的用户名
     */
    @ColumnDescription(type = "varchar(100)")
    private String userName;

    /**
     * 登录smtp服务器的密码
     */
    @ColumnDescription(type = "varchar(100)")
    private String password;

    /**
     * 排序ID，代表smtp服务器的优先顺序
     */
    private Integer orderId;

    public MailAccount()
    {
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getNickName()
    {
        return nickName;
    }

    public void setNickName(String nickName)
    {
        this.nickName = nickName;
    }

    public Integer getAccountId()
    {
        return accountId;
    }

    public void setAccountId(Integer accountId)
    {
        this.accountId = accountId;
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

    public String getSmtpServer()
    {
        return smtpServer;
    }

    public void setSmtpServer(String smtpServer)
    {
        this.smtpServer = smtpServer;
    }

    public String getPop3Server()
    {
        return pop3Server;
    }

    public void setPop3Server(String pop3Server)
    {
        this.pop3Server = pop3Server;
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

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
    }

    @Override
    public int hashCode()
    {
        return accountId.hashCode();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof MailAccount))
            return false;

        MailAccount mailAccount = (MailAccount) o;
        return accountId.equals(mailAccount.accountId);
    }

    @Override
    public String toString()
    {
        if (StringUtils.isEmpty(nickName))
            return address;
        return nickName + "(" + address + ")";
    }
}
