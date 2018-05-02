package com.gzzm.platform.login;

import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * 记录最近登录错误的信息，当登录错误达到一定次数则暂时锁住，当登录成功一次之后清除所有登录错误
 *
 * @author camel
 * @date 2018/1/30
 */
@Entity(table = "PFLOGINERROR", keys = "errorId")
@Indexes({
        @Index(columns = {"loginName", "loginTime"})
})
public class LoginError
{
    /**
     * 错误id，主键，由uuid生成
     */
    @Generatable(name = "uuid", length = 32)
    private String errorId;

    /**
     * 登录用户名
     */
    @ColumnDescription(type = "varchar(250)", nullable = false)
    private String loginName;

    private Date loginTime;

    public LoginError()
    {
    }

    public String getErrorId()
    {
        return errorId;
    }

    public void setErrorId(String errorId)
    {
        this.errorId = errorId;
    }

    public String getLoginName()
    {
        return loginName;
    }

    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }

    public Date getLoginTime()
    {
        return loginTime;
    }

    public void setLoginTime(Date loginTime)
    {
        this.loginTime = loginTime;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof LoginError))
            return false;

        LoginError that = (LoginError) o;

        return errorId.equals(that.errorId);
    }

    @Override
    public int hashCode()
    {
        return errorId.hashCode();
    }
}
