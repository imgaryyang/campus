package com.gzzm.platform.commons.cache;

import com.gzzm.platform.commons.*;
import net.cyan.commons.util.BeanUtils;
import net.cyan.nest.*;

import java.lang.reflect.Type;
import java.util.Date;

/**
 * 缓存实例的bean工厂
 *
 * @author camel
 * @date 2010-5-26
 */
public class CacheInstanceFactory implements BeanFactory
{
    /**
     * 缓存的名称，关联PFUPDATETIME中的一条记录，根据记录里的lastTime字段决定缓存是否失效
     */
    private String name;

    /**
     * 当前实例
     */
    private Object instance;

    /**
     * 最后更新时间，当此时间落后于PFUPDATETIME中对应记录的lastTime时实例失效，重新加载
     */
    private long lastTime;

    /**
     * 真正的工厂
     */
    private BeanFactory realFactory;

    /**
     * 标识当前是否正在加载数据
     */
    private boolean loading;

    public CacheInstanceFactory(String name, BeanFactory realFactory)
    {
        this.name = name;
        this.realFactory = realFactory;
    }

    public synchronized Object newInstance(final Type type, final BeanContainer container, final String name)
            throws Exception
    {
        if (instance == null)
        {
            //还未加载数据
            instance = realFactory.newInstance(type, container, name);
        }
        else
        {
            if (!loading)
            {
                //如果正在加载数据，不重复加载，直接先返回旧的数据

                Date date = UpdateTimeService.getLastTime(this.name);
                if (date != null && date.getTime() > lastTime)
                {
                    //数据已经更新
                    loading = true;

                    //另起一个线程加载数据，避免加载数据时挂住死锁
                    Tools.run(new Runnable()
                    {
                        public void run()
                        {
                            try
                            {
                                long time = System.currentTimeMillis();
                                Object temp = realFactory.newInstance(type, container, name);
                                synchronized (CacheInstanceFactory.this)
                                {
                                    //数据加载完毕
                                    instance = temp;
                                    lastTime = time;
                                    loading = false;
                                }
                            }
                            catch (Exception ex)
                            {
                                Tools.log("load instance " + BeanUtils.toString(type) + " failed", ex);
                            }
                        }
                    });
                }
            }
        }

        return instance;
    }

    public Class<?> getInstanceType(Class<?> c) throws Exception
    {
        return c;
    }

    @Override
    public String getScope()
    {
        return PROTOTYPE;
    }
}
