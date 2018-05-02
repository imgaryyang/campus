package com.gzzm.in;

import com.gzzm.platform.commons.*;
import net.cyan.arachne.*;

/**
 * @author camel
 * @date 13-12-17
 */
public class InterfacePageInterceptor implements PageInterceptor
{
    public InterfacePageInterceptor()
    {
    }

    public boolean accept(RequestContext context)
    {
        return context.getForm() instanceof InterfacePage;
    }

    public Object before(RequestContext context) throws Exception
    {
        Class<?> returnType = context.getPageMethod().getMethod().getReturnType();
        if (InterfaceResult.class.isAssignableFrom(returnType))
        {
            InterfaceUserCheck.check(context.getRequest());
        }

        return null;
    }

    public Object after(RequestContext context, Object result) throws Exception
    {
        return null;
    }

    public Object catchHandle(RequestContext context, Throwable ex)
    {
        Class<?> returnType = context.getPageMethod().getMethod().getReturnType();

        if (InterfaceResult.class.isAssignableFrom(returnType))
        {
            try
            {
                InterfaceResult result = (InterfaceResult) returnType.newInstance();

                if (ex instanceof SystemMessageException)
                    result.setError(((SystemMessageException) ex).getDisplayMessage());
                else
                    result.setError(Tools.getMessage("interface.unknown_error"));

                if (!(ex instanceof NoErrorException))
                    Tools.log(ex);

                return result;
            }
            catch (Throwable ex1)
            {
                Tools.log(ex1);
            }
        }

        return null;
    }

    public void finallyHandle(RequestContext context)
    {
    }

    @Override
    public void logResult(RequestContext context, Object result)
    {
    }
}
