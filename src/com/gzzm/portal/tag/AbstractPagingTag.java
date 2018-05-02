package com.gzzm.portal.tag;

import net.cyan.commons.util.*;
import net.cyan.commons.util.io.WebUtils;
import net.cyan.nest.annotation.Inject;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 分页标签
 *
 * @author camel
 * @date 2011-6-14
 */
public abstract class AbstractPagingTag implements PortalTag
{
    @Inject
    private static Provider<HttpServletRequest> requestProvider;

    public AbstractPagingTag()
    {
    }

    protected String getUrl(String pageNo) throws Exception
    {
        return WebUtils.getRequestUrl(requestProvider.get(), "pageNo=" + pageNo);
    }

    protected String getUrl(int pageNo) throws Exception
    {
        return getUrl(Integer.toString(pageNo));
    }

    protected String getAction(String pageNo) throws Exception
    {
        return "Portal.redirect('" + getUrl(pageNo) + "',this)";
    }

    protected String getAction(int pageNo) throws Exception
    {
        return getAction(Integer.toString(pageNo));
    }

    protected String getHref(int pageNo, String text) throws Exception
    {
        return "<a href=\"#\" onclick=\"" + getAction(pageNo) + ";return false;\">" + text + "</a>";
    }

    protected int getPageNo(Map<String, Object> context) throws Exception
    {
        int pageNo = 1;

        try
        {
            Object pageNoObject = context.get(QueryTag.PAGENO);
            if (pageNoObject != null)
                pageNo = DataConvert.toInt(pageNoObject);
        }
        catch (Exception ex)
        {
            //页码不合法，跳过
        }

        return pageNo;
    }

    protected int getTotalCount(Map<String, Object> context) throws Exception
    {
        return DataConvert.toInt(context.get(QueryTag.TOTALCOUNT));
    }

    protected int getPageCount(Map<String, Object> context) throws Exception
    {
        return DataConvert.toInt(context.get(QueryTag.PAGECOUNT));
    }

    protected int getDefaultPagingSize() throws Exception
    {
        return 10;
    }

    protected int getPagingSize(Map<String, Object> context) throws Exception
    {
        int pagingSize;

        try
        {
            Object pagingSizeObject = context.get(QueryTag.PAGINGSIZE);
            if (pagingSizeObject != null)
                pagingSize = DataConvert.toInt(pagingSizeObject);
            else
                pagingSize = getDefaultPagingSize();
        }
        catch (Exception ex)
        {
            //分页大小不合法，跳过，使用默认分页大小
            pagingSize = getDefaultPagingSize();
        }

        return pagingSize;
    }

    public Object getValue(Map<String, Object> context) throws Exception
    {
        int pageNo = getPageNo(context);

        if (pageNo < 1)
            pageNo = 1;

        int totalCount = getTotalCount(context);
        int pageCount = getPageCount(context);

        return getHtml(pageNo, totalCount, pageCount, context);
    }

    /**
     * 生成分页html
     *
     * @param pageNo     当前页码
     * @param totalCount 总记录数
     * @param pageCount  当前页面数
     * @param context    上下文信息，实现类可以获得更多的参数
     * @return 分页的html，由子类实现
     * @throws Exception 允许实现类抛出异常
     */
    protected abstract String getHtml(int pageNo, int totalCount, int pageCount, Map<String, Object> context)
            throws Exception;
}
