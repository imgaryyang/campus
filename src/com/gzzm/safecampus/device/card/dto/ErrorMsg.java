package com.gzzm.safecampus.device.card.dto;

/**
 * @author liyabin
 * @date 2018/3/21
 */
public class ErrorMsg
{
    private String Result;
    private String Error;
    private String Token;


    public ErrorMsg()
    {
    }

    public ErrorMsg(String result, String error)
    {
        Result = result;
        Error = error;
    }

    public String getResult()
    {
        return Result;
    }

    public void setResult(String result)
    {
        Result = result;
    }

    public String getError()
    {
        return Error;
    }

    public void setError(String error)
    {
        Error = error;
    }

    public String getToken()
    {
        return Token;
    }

    public void setToken(String token)
    {
        Token = token;
    }
}
