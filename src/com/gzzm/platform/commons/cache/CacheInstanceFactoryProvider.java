package com.gzzm.platform.commons.cache;

import com.gzzm.platform.annotation.CacheInstance;
import net.cyan.nest.*;

/**
 * @author camel
 * @date 2010-5-26
 */
public class CacheInstanceFactoryProvider implements BeanFactoryProvider
{
    public CacheInstanceFactoryProvider()
    {
    }

    public BeanFactory getFactory(Class c, String name, BeanContainer container) throws Exception
    {
        CacheInstance cacheInstance = ((Class<?>) c).getAnnotation(CacheInstance.class);

        if (cacheInstance != null)
        {
            BeanFactory reanFactory = container.createDefaultFactory(c, null);

            return new CacheInstanceFactory(cacheInstance.value(), reanFactory);
        }

        return null;
    }
}
