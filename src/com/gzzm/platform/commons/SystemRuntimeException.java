package com.gzzm.platform.commons;

/**
 * 应用系统的RuntimeException
 * @author camel
 * @date 2009-7-21
 */

public class SystemRuntimeException extends RuntimeException
{
    private static final long serialVersionUID = -7220953361299267509L;

    public SystemRuntimeException()
    {
    }

    public SystemRuntimeException(String message)
    {
        super(message);
    }

    public SystemRuntimeException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public SystemRuntimeException(Throwable cause)
    {
        super(cause);
    }
}