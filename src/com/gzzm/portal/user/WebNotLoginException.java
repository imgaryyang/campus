package com.gzzm.portal.user;

import com.gzzm.platform.commons.NoErrorException;

/**
 * web用户没有登录的异常
 *
 * @author camel
 * @date 2011-5-4
 */
public class WebNotLoginException extends NoErrorException
{
    private static final long serialVersionUID = 7634365089514871769L;

    public WebNotLoginException()
    {
        super(null);
    }
}
