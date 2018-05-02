package com.gzzm.safecampus.campus.trusteeship;

import com.gzzm.safecampus.campus.base.BaseDeptQueryCrud;

/**
 * 托管室列表控件
 */
public class TrusteeshipRoomListDisplay extends BaseDeptQueryCrud<TrusteeshipRoom, Integer>
{
    public TrusteeshipRoomListDisplay()
    {
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        return "(deleteTag=0 or deleteTag is null)";
    }

    @Override
    protected TrusteeshipRoom getRoot()
    {
        return new TrusteeshipRoom(0,"全部");
    }
}