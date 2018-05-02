package com.gzzm.platform.login;

/**
 * 单点登录的返回结果
 *
 * @author camel
 * @date 11-11-25
 */
public class SSOResult
{
    private String loginId;

    /**
     * 00表示传入的用户id是空的
     * 01表示用户不存在
     * 02表示用户不属于任何部门
     */
    private String errorCode;

    private String errorMessage;

    public SSOResult()
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

    public String getErrorCode()
    {
        return errorCode;
    }

    public void setErrorCode(String errorCode)
    {
        this.errorCode = errorCode;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }
}
