package com.gzzm.platform.commons.event;

import com.gzzm.platform.annotation.Listener;
import net.cyan.commons.util.ClassResolver;

import java.lang.reflect.*;
import java.util.*;

/**
 * 用于发现事件监听器
 *
 * @author camel
 * @date 2011-4-25
 */
public class ListenerResolver implements ClassResolver
{
    /**
     * 用于防止有些class重复加载
     */
    private Set<String> loadedClass = new HashSet<String>();

    @SuppressWarnings("unchecked")
    public void resolve(Class<?> c) throws Exception
    {
        if (c.getAnnotation(Listener.class) != null)
        {
            String className = c.getName();

            if (loadedClass.contains(className))
                return;

            loadedClass.add(className);

            for (Method method : c.getDeclaredMethods())
            {
                if (Modifier.isPublic(method.getModifiers()))
                {
                    Listener listener = method.getAnnotation(Listener.class);

                    if (listener != null)
                    {
                        Class[] types = method.getParameterTypes();

                        if (types.length == 1)
                        {
                            Events.addLisenter(types[0], listener.value(), new MethodEventListener(method));
                        }
                    }
                }
            }
        }
    }
}