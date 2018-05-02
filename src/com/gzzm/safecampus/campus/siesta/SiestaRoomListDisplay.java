package com.gzzm.safecampus.campus.siesta;

import com.gzzm.safecampus.campus.base.BaseDeptQueryCrud;

/**
 * 午休室下拉选择控件
 * Created by Huangmincong on 2018/3/13.
 */
public class SiestaRoomListDisplay extends BaseDeptQueryCrud<SiestaRoom, Integer>
{
    public SiestaRoomListDisplay()
    {
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        return "(deleteTag=0 or deleteTag is null)";
    }

    @Override
    protected SiestaRoom getRoot() throws Exception
    {
        return new SiestaRoom(0, "全部");
    }
}
