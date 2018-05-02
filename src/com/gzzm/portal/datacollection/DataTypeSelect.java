package com.gzzm.portal.datacollection;

import com.gzzm.platform.commons.crud.BaseQueryCrud;
import com.gzzm.platform.commons.crud.SelectableListView;

/**
 * @author ldp
 * @date 2018/4/25
 */
public class DataTypeSelect extends BaseQueryCrud<DataType, Integer> {
    public DataTypeSelect() {
    }

    @Override
    protected Object createListView() throws Exception {
        return new SelectableListView();
    }
}
