package com.gzzm.portal.commons;

/**
 * 页面信息
 *
 * @author camel
 * @date 13-10-31
 */
public class PageInfo
{
    /**
     * 页面号
     */
    private int pageNo;

    /**
     * 此页的url
     */
    private String url;

    /**
     * 是否当前页
     */
    private boolean current;

    public PageInfo()
    {
    }

    public PageInfo(int pageNo, String url, boolean current)
    {
        this.pageNo = pageNo;
        this.url = url;
        this.current = current;
    }

    public int getPageNo()
    {
        return pageNo;
    }

    public String getUrl()
    {
        return url;
    }

    public boolean isCurrent()
    {
        return current;
    }
}
