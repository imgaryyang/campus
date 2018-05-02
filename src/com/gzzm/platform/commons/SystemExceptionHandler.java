package com.gzzm.platform.commons;

import net.cyan.arachne.*;
import net.cyan.proteus.TemplateException;

/**
 * 系统使用的错误拦截
 *
 * @author camel
 * @date 2009-7-25
 */
public class SystemExceptionHandler extends BaseExceptionHandler
{
    public SystemExceptionHandler()
    {
    }

    protected SystemMessageException getMessageException(Throwable ex)
    {
        while (ex != null && !(ex instanceof SystemMessageException))
        {
            ex = ex.getCause();
        }

        return (SystemMessageException) ex;
    }

    protected boolean isError0(Throwable ex)
    {
        if (ex instanceof TemplateException)
            ex = ex.getCause();

        if (getMessageException(ex) != null)
            return false;

        String message = ex.getMessage();
        return !(message != null && message.startsWith("zm_msg:")) && super.isError0(ex);
    }

    protected String getMessage(Throwable ex, ResultType resultType)
    {
        SystemMessageException messageException = getMessageException(ex);
        if (messageException != null)
            return messageException.getDisplayMessage();

        String message = ex.getMessage();
        if (message != null && message.startsWith("zm_msg:"))
            return message.substring(7);

        return super.getMessage(ex, resultType);
    }

    protected String forward(String message, Throwable ex)
    {
        try
        {
            //直接将文字输出给客户端
            RequestContext context = RequestContext.getContext();
            context.setAttribute("message", context.getMessage(message));
            context.forward("/500.jsp");
        }
        catch (Throwable th)
        {
            //处理异常再出错，忽略，不再重复作异常处理
        }
        return null;
    }
}
