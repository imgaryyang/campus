package com.gzzm.platform.commons.event;

import com.gzzm.platform.commons.Tools;
import net.cyan.commons.util.ExceptionUtils;

import java.lang.reflect.*;

/**
 * 通过反射来调用某个方法触发事件
 *
 * @author camel
 * @date 2011-4-25
 */
public class MethodEventListener implements EventListener<Object>
{
    /**
     * 要触发的方法
     */
    private Method method;

    public MethodEventListener(Method method)
    {
        this.method = method;
    }

    public void invoke(Object obj) throws Exception
    {
        Object instance = null;

        if (!Modifier.isStatic(method.getModifiers()))
        {
            instance = Tools.getBean(method.getDeclaringClass());
        }

        try
        {
            method.invoke(instance, obj);
        }
        catch (InvocationTargetException ex)
        {
            ExceptionUtils.handleInvocationTargetException(ex);
        }
    }
}
