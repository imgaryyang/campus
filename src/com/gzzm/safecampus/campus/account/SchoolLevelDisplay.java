package com.gzzm.safecampus.campus.account;

import com.gzzm.platform.commons.crud.BaseQueryCrud;
import com.gzzm.platform.commons.crud.SelectableListView;

/**
 * 学校级别左边选择列表
 *
 * @author Neo
 * @date 2018/4/9 10:35
 */
public class SchoolLevelDisplay extends BaseQueryCrud<SchoolLevel, Integer>
{
    public SchoolLevelDisplay()
    {
        addOrderBy("orderId");
    }

    @Override
    protected void afterQuery() throws Exception
    {
        getList().add(0, new SchoolLevel(0, "全部"));
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        return "(deleteTag=0 or deleteTag is null)";
    }

    @Override
    protected Object createListView() throws Exception
    {
        return new SelectableListView();
    }
}
