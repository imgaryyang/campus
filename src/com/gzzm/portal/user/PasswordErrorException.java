package com.gzzm.portal.user;

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

    /**
     * @param type 0为用户不存在，1为密码错误，为密码错误提醒
     */
    public PasswordErrorException(int type)
    {
        super(type == 0 ? "web.login.no_user" :
                (type == 1 ? "web.login.password_error" : "web.login.oldPassword_error"));
    }
}
