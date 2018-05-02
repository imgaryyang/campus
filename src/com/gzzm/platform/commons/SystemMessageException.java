package com.gzzm.platform.commons;

import net.cyan.commons.util.CollectionUtils;

import java.util.Map;

/**
 * 系统消息异常
 * @author camel
 * @date 2009-7-22
 */

public class SystemMessageException extends SystemRuntimeException
{
     private static final long serialVersionUID = 6862371556363828249L;

    /**
     * 消息
     */
    private String message;

    /**
     * 参数
     */
    private Map<?, ?> args;

    public SystemMessageException(String message, String error)
    {
        super(error);
        this.message = message;
    }

    public SystemMessageException(String message, String error, Throwable cause)
    {
        super(error, cause);
        this.message = message;
    }

    public SystemMessageException(String message, Throwable cause)
    {
        super(cause);
        this.message = message;
    }

    public SystemMessageException(String message, String error, Map<?, ?> args)
    {
        super(error);
        this.message = message;
        this.args = args;
    }

    public SystemMessageException(String message, String error, Throwable cause, Map<?, ?> args)
    {
        super(error, cause);
        this.message = message;
        this.args = args;
    }

    public SystemMessageException(String message, Throwable cause, Map<?, ?> args)
    {
        super(cause);
        this.message = message;
        this.args = args;
    }

    public SystemMessageException(String message, String error, Object... args)
    {
        super(error);
        this.message = message;
        this.args = CollectionUtils.asMap(0, args);
    }

    public SystemMessageException(String message, String error, Throwable cause, Object... args)
    {
        super(error, cause);
        this.message = message;
        this.args = CollectionUtils.asMap(0, args);
    }

    public SystemMessageException(String message, Throwable cause, Object... args)
    {
        super(cause);
        this.message = message;
        this.args = CollectionUtils.asMap(0, args);
    }

    public String getDisplayMessage()
    {
        return Tools.getMessage(message, args);
    }

    public String getOriginalMessage()
    {
        return message;
    }
}