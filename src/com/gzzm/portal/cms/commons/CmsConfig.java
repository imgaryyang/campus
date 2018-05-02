package com.gzzm.portal.cms.commons;

import net.cyan.commons.util.KeyValue;
import net.cyan.nest.annotation.Injectable;

import java.util.List;

/**
 * 门户相关配置
 *
 * @author camel
 * @date 13-11-7
 */
@Injectable(singleton = true)
public class CmsConfig
{
    private List<KeyValue<String>> informationPages;

    private boolean fetchImg;

    private boolean cache;

    private String cachePath;

    public CmsConfig()
    {
    }

    public String getCachePath()
    {
        return cachePath;
    }

    public void setCachePath(String cachePath)
    {
        this.cachePath = cachePath;
    }

    public List<KeyValue<String>> getInformationPages()
    {
        return informationPages;
    }

    public void setInformationPages(List<KeyValue<String>> informationPages)
    {
        this.informationPages = informationPages;
    }

    public boolean isFetchImg()
    {
        return fetchImg;
    }

    public void setFetchImg(boolean fetchImg)
    {
        this.fetchImg = fetchImg;
    }

    public boolean isCache()
    {
        return cache;
    }

    public void setCache(boolean cache)
    {
        this.cache = cache;
    }
}
