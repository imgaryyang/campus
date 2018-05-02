package com.gzzm.portal.cms.commons;

import com.gzzm.platform.annotation.CacheInstance;
import com.gzzm.platform.commons.*;
import net.cyan.commons.security.SecurityUtils;
import net.cyan.commons.util.Provider;
import net.cyan.commons.util.io.CacheData;
import net.cyan.nest.annotation.Inject;

import java.io.IOException;
import java.util.*;

/**
 * @author camel
 * @date 2015/7/2
 */
@CacheInstance("cms.pagecache")
public class PageCache
{
    @Inject
    private static Provider<CmsConfig> configProvider;

    private static final Runnable UPDATE = new PageCacheRefresh();

    /**
     * url和缓存的路径
     */
    private static final Map<String, String> paths = new HashMap<String, String>();

    /**
     * 已经加载的页面
     */
    private Set<String> loadedPaths = new HashSet<String>();

    public PageCache()
    {
    }

    public static void updateCache()
    {
        Tools.run(UPDATE);
    }

    static void updateCache0()
    {
        try
        {
            UpdateTimeService.updateLastTime0("cms.pagecache", new Date());
        }
        catch (Throwable ex)
        {
            Tools.log(ex);
        }
    }

    public synchronized String getPath(String url)
    {
        if (loadedPaths.contains(url))
        {
            return paths.get(url);
        }

        return null;
    }

    public String addCache(String url, CacheData data) throws IOException
    {
        String path;
        synchronized (this)
        {
            path = paths.get(url);
            if (path == null)
            {
                String cachePath = configProvider.get().getCachePath();

                path = cachePath + "/" + urlToPath(url);

                paths.put(url, path);
            }
        }

        data.saveAs(path);

        synchronized (this)
        {
            loadedPaths.add(url);
        }

        return path;
    }

    public static String urlToPath(String url)
    {
        return SecurityUtils.md5(url);
    }
}
