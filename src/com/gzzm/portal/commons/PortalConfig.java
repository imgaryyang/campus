package com.gzzm.portal.commons;

import net.cyan.nest.annotation.Injectable;

/**
 * 门户配置
 *
 * @author camel
 * @date 2011-6-16
 */
@Injectable(singleton = true)
public class PortalConfig
{
    /**
     * 默认每页显示20条数据
     */
    private int pageSize = 20;

    public PortalConfig()
    {
    }

    public int getPageSize()
    {
        return pageSize;
    }

    public void setPageSize(int pageSize)
    {
        this.pageSize = pageSize;
    }
}
