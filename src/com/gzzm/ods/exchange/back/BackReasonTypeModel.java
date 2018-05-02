package com.gzzm.ods.exchange.back;

import com.gzzm.platform.commons.components.EntityPageListModel;

/**
 * 退文理由类型选择控件
 *
 * @author ldp
 * @date 2018/1/10
 */
public class BackReasonTypeModel extends EntityPageListModel<BackReasonType, Integer>
{
    public BackReasonTypeModel()
    {
        addOrderBy("orderId");
    }
}
