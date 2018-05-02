package com.gzzm.platform.login;

/**
 * 登录接口返回的信息
 *
 * @author camel
 * @date 2018/1/30
 */
public class LoginResult
{
    private boolean selectDept;

    private boolean modifyPassword;

    private boolean logoutOthers;

    private String message;

    public LoginResult()
    {
    }

    public boolean isSelectDept()
    {
        return selectDept;
    }

    public void setSelectDept(boolean selectDept)
    {
        this.selectDept = selectDept;
    }

    public boolean isModifyPassword()
    {
        return modifyPassword;
    }

    public void setModifyPassword(boolean modifyPassword)
    {
        this.modifyPassword = modifyPassword;
    }

    public boolean isLogoutOthers()
    {
        return logoutOthers;
    }

    public void setLogoutOthers(boolean logoutOthers)
    {
        this.logoutOthers = logoutOthers;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }
}
