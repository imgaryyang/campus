package com.gzzm.platform.commons.callback;

import java.io.Serializable;

/**
 * @author camel
 * @date 2015/3/6
 */
public class CallbackMessage implements Serializable
{
    private static final long serialVersionUID = 7572246611447464691L;

    private String id;

    private String js;

    private String method;

    @SuppressWarnings("NonSerializableFieldInSerializableClass")
    private Object[] args;

    public CallbackMessage()
    {
    }

    public CallbackMessage(String id, String js, String method)
    {
        this.id = id;
        this.js = js;
        this.method = method;
    }

    public CallbackMessage(String id, String js, String method, Object... args)
    {
        this.id = id;
        this.js = js;
        this.method = method;
        this.args = args;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getJs()
    {
        return js;
    }

    public void setJs(String js)
    {
        this.js = js;
    }

    public String getMethod()
    {
        return method;
    }

    public void setMethod(String method)
    {
        this.method = method;
    }

    public Object[] getArgs()
    {
        return args;
    }

    public void setArgs(Object[] args)
    {
        this.args = args;
    }
}
