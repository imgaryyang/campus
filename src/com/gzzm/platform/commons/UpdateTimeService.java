package com.gzzm.platform.commons;

import net.cyan.commons.cache.Cache;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

import java.util.Date;

/**
 * 更新时间相关的服务
 *
 * @author camel
 * @date 2009-10-5
 */
public class UpdateTimeService
{
    @Inject
    private static Provider<CommonDao> daoProvider;

    @Inject("updateTime")
    private static Cache<String, Date> cache;

    private UpdateTimeService()
    {
    }

    public static void updateLastTime(final String name, final Date lastTime) throws Exception
    {
        Tools.run(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    updateLastTime0(name, lastTime);
                }
                catch (Throwable ex)
                {
                    Tools.log(ex);
                }
            }
        });
    }

    public static void updateLastTime0(String name, Date lastTime) throws Exception
    {
        daoProvider.get().updateLastTime(name, lastTime);
        cache.update(name, lastTime);
    }

    public static Date getLastTime(String name) throws Exception
    {
        if (cache.contains(name))
            return cache.getCache(name);

        Date lastTime = daoProvider.get().getLastTime(name);
        cache.set(name, lastTime);

        return lastTime;
    }
}
