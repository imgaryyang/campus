package com.gzzm.platform.login;

import com.gzzm.platform.commons.NoErrorException;

/**
 * 密码错误
 *
 * @author camel
 * @date 2009-7-30
 */
public class PasswordErrorException extends NoErrorException
{
    private static final long serialVersionUID = -4682739672752053785L;
    
    public PasswordErrorException(String message)
    {
        super(message);
    }
}
