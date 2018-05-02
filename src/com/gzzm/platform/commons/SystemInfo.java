package com.gzzm.platform.commons;

/**
 * 定义一个系统的基本信息
 *
 * @author camel
 * @date 2009-10-29
 */
public class SystemInfo
{
    private String systemName;

    private int pageSize = -1;

    public SystemInfo()
    {
    }

    public String getSystemName()
    {
        return systemName;
    }

    public void setSystemName(String systemName)
    {
        this.systemName = systemName;
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
