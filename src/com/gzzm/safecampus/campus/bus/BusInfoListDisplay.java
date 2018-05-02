package com.gzzm.safecampus.campus.bus;

import com.gzzm.safecampus.campus.base.BaseDeptQueryCrud;

/**
 * 校巴信息列表控件
 *
 * @author czy
 */
public class BusInfoListDisplay extends BaseDeptQueryCrud<BusInfo, Integer>
{
    public BusInfoListDisplay()
    {
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        return "(deleteTag=0 or deleteTag is null)";
    }

    @Override
    protected BusInfo getRoot() throws Exception
    {
        return new BusInfo(0, "全部");
    }
}
