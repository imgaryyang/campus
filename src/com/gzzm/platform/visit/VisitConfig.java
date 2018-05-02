package com.gzzm.platform.visit;

import net.cyan.nest.annotation.Injectable;

/**
 * @author camel
 * @date 2011-6-21
 */
@Injectable(singleton = true)
public class VisitConfig
{
    /**
     * 缓存的大小
     */
    private int cacheSize = 500;

    public VisitConfig()
    {
    }

    public int getCacheSize()
    {
        return cacheSize;
    }

    public void setCacheSize(int cacheSize)
    {
        this.cacheSize = cacheSize;
    }
}
