package com.gzzm.platform.commons.crud;

import net.cyan.crud.view.BaseTableView;

/**
 * 子列表，用于在父表的维护页面里嵌入子表的维护功能
 *
 * @author camel
 * @date 2010-12-7
 */
public class SubListView extends BaseTableView
{
    private String field;

    /**
     * 是否支持排序
     */
    private boolean orderable;

    private boolean readOnly;

    public SubListView(String field)
    {
        this.field = field;
    }

    public String getName() throws Exception
    {
        return getPropertyWithLanguage(null, "name");
    }

    public void setName(String name)
    {
        setProperty("name", name);
    }

    public String getField()
    {
        return field;
    }

    public void setField(String field)
    {
        this.field = field;
    }

    public boolean isOrderable()
    {
        return orderable;
    }

    public void setOrderable(boolean orderable)
    {
        this.orderable = orderable;
    }

    public boolean isReadOnly()
    {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly)
    {
        this.readOnly = readOnly;
    }
}