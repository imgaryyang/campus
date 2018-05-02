package com.gzzm.platform.organ;

import net.cyan.commons.util.Filter;

import java.util.*;

/**
 * 只接收某个指定部门的过滤器
 *
 * @author camel
 * @date 2009-7-29
 */
public class SingleDeptFilter implements Filter<DeptInfo>
{
    private static final Map<Integer, SingleDeptFilter> FILTER_MAP = new HashMap<Integer, SingleDeptFilter>();

    private Integer id;

    private SingleDeptFilter(Integer id)
    {
        this.id = id;
    }

    public static synchronized SingleDeptFilter getFilter(Integer deptId)
    {
        SingleDeptFilter filter = FILTER_MAP.get(deptId);
        if (filter == null)
            FILTER_MAP.put(deptId, filter = new SingleDeptFilter(deptId));
        return filter;
    }

    public Integer getId()
    {
        return id;
    }

    public boolean accept(DeptInfo dept) throws Exception
    {
        return dept.getDeptId().equals(id);
    }

    public boolean equals(Object o)
    {
        return this == o || o instanceof SingleDeptFilter && id.equals(((SingleDeptFilter) o).id);
    }

    public int hashCode()
    {
        return id.hashCode();
    }
}
