package com.gzzm.portal.tag;

import net.cyan.commons.util.*;

import java.util.*;

/**
 * 查询数据的标签页，包含了分页处理
 *
 * @author camel
 * @date 2011-6-13
 */
public abstract class QueryTag implements PortalTag
{
    public static final String PAGESIZE = "pageSize";

    public static final String PAGENO = "pageNo";

    public static final String TOTALCOUNT = "totalCount";

    public static final String PAGINGSIZE = "pagingSize";

    public static final String PAGECOUNT = "pageCount";

    public static final String PAGESIZE$ = "pageSize$";

    public static final String PAGENO$ = "pageNo$";

    public static final String COUNT = "count$";

    public static final String LIMIT = "limit";

    public QueryTag()
    {
    }

    protected int getDefaultPageSize() throws Exception
    {
        //默认不分页
        return -1;
    }

    protected int getLimit(Map<String, Object> context) throws Exception
    {
        Object value = context.get(LIMIT);
        if (value != null)
        {
            try
            {
                return DataConvert.toInt(value);
            }
            catch (NumberFormatException ex)
            {
                //解析数字异常,跳过
            }
        }

        return -1;
    }

    protected int getPageSize(Map<String, Object> context) throws Exception
    {
        Object value = context.get(PAGESIZE);
        if (value != null)
        {
            try
            {
                return DataConvert.toInt(value);
            }
            catch (NumberFormatException ex)
            {
                //解析数字异常,跳过
            }
        }

        return getDefaultPageSize();
    }

    protected int getPageNo(Map<String, Object> context) throws Exception
    {
        int pageNo = 1;

        Object value = context.get(PAGENO);
        if (value != null)
        {
            try
            {
                pageNo = DataConvert.toInt(value);
                if (pageNo < 1)
                    pageNo = 1;
            }
            catch (NumberFormatException ex)
            {
                //解析数字异常,跳过
            }
        }

        return pageNo;
    }

    @SuppressWarnings("unchecked")
    public Object getValue(Map<String, Object> context) throws Exception
    {
        int limit = getLimit(context);
        int pageSize = limit <= 0 ? getPageSize(context) : limit;

        TagQueryResult result = new TagQueryResult();
        result.setContext(context);

        if (pageSize > 0)
        {
            //分页查询
            int pageNo = limit <= 0 ? getPageNo(context) : 1;

            QueryResult<?> queryResult = query(pageSize, pageNo, context);

            if (limit <= 0)
            {
                result.setRecords(queryResult.getRecords());
                result.setPageSize(pageSize);
                result.setPageNo(pageNo);
                result.setTotalCount(queryResult.getTotalCount());
                result.setPageCount(queryResult.getPageCount());
            }
            else
            {
                result.setRecords(queryResult.getRecords());
                result.setCount(queryResult.getTotalCount());
            }
        }
        else
        {
            List<?> list = query(context);

            result.setRecords(list);
            result.setCount(list.size());
        }

        return result;
    }

    /**
     * 不分页查询
     *
     * @param parameters 参数
     * @return 查询的结果
     * @throws Exception 允许实现类抛出异常
     */
    protected abstract List<?> query(Map<String, Object> parameters) throws Exception;

    /**
     * 分页查询
     *
     * @param pageSize   页面大小，即每页多少条记录
     * @param pageNo     页面序号，从1开始
     * @param parameters 参数
     * @return 查询的结果，包括总记录数和页面数
     * @throws Exception 允许实现类抛出异常
     */
    protected abstract QueryResult<?> query(int pageSize, int pageNo, Map<String, Object> parameters) throws Exception;
}
