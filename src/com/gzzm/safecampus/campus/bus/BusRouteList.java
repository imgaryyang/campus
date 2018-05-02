package com.gzzm.safecampus.campus.bus;

import com.gzzm.safecampus.campus.base.DeptOwnedEntityPageList;

/**
 * 路线选择控件
 *
 * @author Neo
 * @date 2018/4/10 14:48
 */
public class BusRouteList extends DeptOwnedEntityPageList<BusRoute, Integer>
{
    public BusRouteList()
    {
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        return "(deleteTag=0 or deleteTag is null)";
    }

    @Override
    protected String getSearchCondition() throws Exception
    {
        return "routeName like ?text";
    }
}
