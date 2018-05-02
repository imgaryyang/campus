package com.gzzm.platform.login;

import com.gzzm.platform.organ.*;
import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * 登录状态，当服务器重启之后不需要重新登录，可直接使用系统
 *
 * @author camel
 * @date 2010-11-30
 */
@Entity(table = "PFLOGIN", keys = "loginId")
public class Login
{
    /**
     * 登录ID，由uuid生成
     */
    @Generatable(name = "uuid", length = 32)
    private String loginId;

    /**
     * 登录的用户ID
     */
    private Integer userId;

    /**
     * 关联user对象
     */
    private User user;

    /**
     * 登录的部门ID
     */
    private Integer deptId;

    /**
     * 关联dept对象
     */
    private Dept dept;

    /**
     * 登录的系统ID
     */
    @ColumnDescription(type = "varchar(50)")
    private String systemId;

    /**
     * 登录的时间
     */
    private Date loginTime;

    public Login()
    {
    }

    public String getLoginId()
    {
        return loginId;
    }

    public void setLoginId(String loginId)
    {
        this.loginId = loginId;
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

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public Dept getDept()
    {
        return dept;
    }

    public void setDept(Dept dept)
    {
        this.dept = dept;
    }

    public String getSystemId()
    {
        return systemId;
    }

    public void setSystemId(String systemId)
    {
        this.systemId = systemId;
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

        if (!(o instanceof Login))
            return false;

        Login login = (Login) o;

        return loginId.equals(login.loginId);
    }

    @Override
    public int hashCode()
    {
        return loginId.hashCode();
    }
}
