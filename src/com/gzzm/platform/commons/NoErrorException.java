package com.gzzm.platform.commons;

import java.util.Map;

/**
 * 不是错误的异常
 * @author camel
 * @date 2009-7-22
 */
public class NoErrorException extends SystemMessageException
{
    private static final long serialVersionUID = -4681210439408060743L;

    public NoErrorException(String message)
    {
        super(message, (String) null);
    }

    public NoErrorException(String message, Map<?, ?> args)
    {
        super(message, (String) null, args);
    }

    public NoErrorException(String message, Object... args)
    {
        super(message, (String) null, args);
    }
}
