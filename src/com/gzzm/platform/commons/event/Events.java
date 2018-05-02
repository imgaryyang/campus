package com.gzzm.platform.commons.event;

import com.gzzm.platform.commons.Tools;

import java.util.*;

/**
 * 事务管理容器
 *
 * @author camel
 * @date 2011-4-25
 */
public class Events
{
    private static final Map<Class, Map<String, List<EventListener>>> lisenters =
            new HashMap<Class, Map<String, List<EventListener>>>();

    public Events()
    {
    }

    public static <T> void addLisenter(Class<T> c, String name, EventListener<T> listener)
    {
        Map<String, List<EventListener>> lisenterMap;
        synchronized (lisenters)
        {
            lisenterMap = lisenters.get(c);

            if (lisenterMap == null)
                lisenters.put(c, lisenterMap = new HashMap<String, List<EventListener>>());
        }

        synchronized (lisenterMap)
        {
            List<EventListener> oldListeners = lisenterMap.get(name);
            List<EventListener> newListeners;

            if (oldListeners == null)
            {
                newListeners = Collections.singletonList((EventListener) listener);
            }
            else
            {
                newListeners = new ArrayList<EventListener>(oldListeners.size() + 1);
                newListeners.addAll(oldListeners);
                newListeners.add(listener);
            }

            lisenterMap.put(name, newListeners);
        }
    }

    @SuppressWarnings("unchecked")
    private static List<EventListener> getLisenters(Class c, String name)
    {
        Map<String, List<EventListener>> lisenterMap = null;
        synchronized (lisenters)
        {
            while (c != Object.class)
            {
                lisenterMap = lisenters.get(c);

                c = c.getSuperclass();
            }
        }

        if (lisenterMap == null)
            return null;

        synchronized (lisenterMap)
        {
            return lisenterMap.get(name);
        }
    }

    @SuppressWarnings("unchecked")
    public static void invoke(Object obj, String name)
    {
        List<EventListener> listeners = getLisenters(obj.getClass(), name);

        if (listeners != null)
        {
            for (EventListener listener : listeners)
            {
                try
                {
                    listener.invoke(obj);
                }
                catch (Throwable ex)
                {
                    //执行一个动作失败不影响其他动作，只是记录日志
                    Tools.log(ex);
                }
            }
        }
    }
}
