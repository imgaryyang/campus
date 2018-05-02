package com.gzzm.safecampus.campus.wx.tag;

import com.gzzm.platform.commons.components.EntityPageListModel;

/**
 * @author Neo
 * @date 2018/4/23 11:19
 */
public class TagList  extends EntityPageListModel<Tag, Integer>
{
    public TagList()
    {
        addOrderBy("orderId");
    }

    @Override
    protected String getTextField() throws Exception
    {
        return "tagName";
    }
}
