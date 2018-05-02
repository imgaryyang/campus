package com.gzzm.portal.tag;

import java.util.*;

/**
 * 通过重写iterator的hasNext方法，来实现里面标签的分页信息不会覆盖外面标签的分页信息
 *
 * @author camel
 * @date 13-12-19
 */
public class TagQueryResult<E> implements Iterable<E>
{
    private int totalCount;

    private int pageCount;

    private int pageNo;

    private int pageSize;

    private int count;

    private List<E> records;

    private Map<String, Object> context;

    public TagQueryResult()
    {
    }

    public int getTotalCount()
    {
        return totalCount;
    }

    public void setTotalCount(int totalCount)
    {
        this.totalCount = totalCount;
    }

    public int getPageCount()
    {
        return pageCount;
    }

    public void setPageCount(int pageCount)
    {
        this.pageCount = pageCount;
    }

    public int getPageNo()
    {
        return pageNo;
    }

    public void setPageNo(int pageNo)
    {
        this.pageNo = pageNo;
    }

    public int getPageSize()
    {
        return pageSize;
    }

    public void setPageSize(int pageSize)
    {
        this.pageSize = pageSize;
    }

    public int getCount()
    {
        return count;
    }

    public void setCount(int count)
    {
        this.count = count;
    }

    public List<E> getRecords()
    {
        return records;
    }

    public void setRecords(List<E> records)
    {
        this.records = records;
    }

    public Map<String, Object> getContext()
    {
        return context;
    }

    public void setContext(Map<String, Object> context)
    {
        this.context = context;
    }

    public Iterator<E> iterator()
    {
        final Iterator<E> iterator = records.iterator();

        return new Iterator<E>()
        {
            public boolean hasNext()
            {
                //设置查询结果的页面数和记录数
                if (pageSize > 0)
                {
                    context.put(QueryTag.TOTALCOUNT, totalCount);
                    context.put(QueryTag.PAGECOUNT, pageCount);
                    context.put(QueryTag.PAGENO$, pageNo);
                    context.put(QueryTag.PAGENO, pageNo);
                    context.put(QueryTag.PAGESIZE$, pageSize);
                }
                else
                {
                    context.put(QueryTag.COUNT, count);
                }

                return iterator.hasNext();
            }

            public E next()
            {
                return iterator.next();
            }

            public void remove()
            {
                throw new UnsupportedOperationException("Method not implemented.");
            }
        };
    }
}
