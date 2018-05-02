package com.gzzm.portal.cms.station;

import com.gzzm.platform.commons.crud.*;

/**
 * 站点列表
 *
 * @author camel
 * @date 2011-5-30
 */
public class StationDisplay extends DeptOwnedQuery<Station, Integer>
{
    public StationDisplay()
    {
        addOrderBy("type");
        addOrderBy("orderId");
    }

    @Override
    protected Object createListView() throws Exception
    {
        return new SelectableListView();
    }
}
