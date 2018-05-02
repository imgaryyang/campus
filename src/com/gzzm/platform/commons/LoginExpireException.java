package com.gzzm.platform.commons;

/**
 * 没有登录或者超时的异常
 * @author camel
 * @date 2009-7-23
 */
public class LoginExpireException extends NoErrorException
{
    private static final long serialVersionUID = -5261969637576992791L;

    public LoginExpireException()
    {
        super(Messages.LOGIN_EXPIRE);
    }
}
