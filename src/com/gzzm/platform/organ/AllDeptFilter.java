package com.gzzm.platform.organ;

import net.cyan.commons.util.Filter;

/**
 * @author camel
 * @date 2010-8-7
 */
public class AllDeptFilter implements Filter<DeptInfo>
{
    public static final Filter<DeptInfo> All = new AllDeptFilter();

    public static final Filter<DeptInfo> EXCLUDED = new Filter<DeptInfo>()
    {
        public boolean accept(DeptInfo deptInfo) throws Exception
        {
            return false;
        }
    };

    public static final Filter<DeptInfo> EXCLUDEDALL = new Filter<DeptInfo>()
    {
        public boolean accept(DeptInfo deptInfo) throws Exception
        {
            return false;
        }
    };

    public static final Filter<DeptInfo> PRIORITY_EXCLUDED = new Filter<DeptInfo>()
    {
        public boolean accept(DeptInfo deptInfo) throws Exception
        {
            return false;
        }
    };

    public static final Filter<DeptInfo> PRIORITY_EXCLUDEDALL = new Filter<DeptInfo>()
    {
        public boolean accept(DeptInfo deptInfo) throws Exception
        {
            return false;
        }
    };

    private AllDeptFilter()
    {
    }

    public boolean accept(DeptInfo deptInfo) throws Exception
    {
        return true;
    }
}
