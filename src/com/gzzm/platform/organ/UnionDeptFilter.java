package com.gzzm.platform.organ;

import net.cyan.commons.util.Filter;

import java.util.*;

/**
 * 组合的部门过滤器，只要一个过滤器满足条件则全部满足条件
 *
 * @author camel
 * @date 2009-7-29
 */
public class UnionDeptFilter implements Filter<DeptInfo>
{
    private List<Filter<DeptInfo>> filters = new ArrayList<Filter<DeptInfo>>();

    public UnionDeptFilter()
    {
    }

    public void addFilter(Filter<DeptInfo> filter)
    {
        filters.add(filter);
    }

    public boolean accept(DeptInfo dept) throws Exception
    {
        for (Filter<DeptInfo> filter : filters)
        {
            if (filter.accept(dept))
                return true;
        }

        return false;
    }

    public List<Filter<DeptInfo>> getFilters()
    {
        return filters;
    }
}
