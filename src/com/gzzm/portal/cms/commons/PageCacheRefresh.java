package com.gzzm.portal.cms.commons;

/**
 * @author camel
 * @date 2017/6/5
 */
public class PageCacheRefresh implements Runnable
{
    public PageCacheRefresh()
    {
    }

    @Override
    public void run()
    {
        PageCache.updateCache0();
    }
}
