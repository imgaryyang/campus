package com.gzzm.platform.commons;

/**
 * 应用系统异常
 * @author camel
 * @date 2009-7-21
 */

public class SystemException extends Exception
{
    private static final long serialVersionUID = 4184522478683070574L;

    public SystemException()
    {
    }

    public SystemException(String message)
    {
        super(message);
    }

    public SystemException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public SystemException(Throwable cause)
    {
        super(cause);
    }
}