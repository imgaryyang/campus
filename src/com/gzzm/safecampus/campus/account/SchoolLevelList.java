package com.gzzm.safecampus.campus.account;

import com.gzzm.platform.commons.components.EntityPageListModel;

/**
 * 学校级别下拉选择框
 *
 * @author Neo
 * @date 2018/4/9 9:52
 */
public class SchoolLevelList extends EntityPageListModel<SchoolLevel, Integer>
{
    public SchoolLevelList()
    {
        addOrderBy("orderId");
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        return "(deleteTag=0 or deleteTag is null)";
    }

    @Override
    protected String getTextField() throws Exception
    {
        return "levelName";
    }
}
