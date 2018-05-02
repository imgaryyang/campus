package com.gzzm.ods.exchange.back;

import com.gzzm.platform.commons.crud.*;

/**
 * 退文理由类型。用于复杂实体左边控件
 *
 * @author ldp
 * @date 2018/1/10
 */
public class BackReasonTypeDisplay extends BaseQueryCrud<BackReasonType, Integer>
{
    public BackReasonTypeDisplay()
    {
        addOrderBy("orderId");
    }

    @Override
    protected Object createListView() throws Exception
    {
        return new SelectableListView();
    }
}
