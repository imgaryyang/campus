package com.gzzm.safecampus.campus.common;

import net.cyan.crud.view.Column;

import java.util.List;

/**
 * @author camel
 * @date 2010-12-7
 */
public class ScSubListViewModel
{
    private ScSubListView view;

    private String id;

    public ScSubListViewModel(ScSubListView view, String id)
    {
        this.view = view;
        this.id = id;
    }

    public ScSubListView getView()
    {
        return view;
    }

    public String getId()
    {
        return id;
    }

    public String getName() throws Exception
    {
        return view.getName();
    }

    public String getField()
    {
        return view.getField();
    }

    public boolean isOrderable()
    {
        return view.isOrderable();
    }

    public boolean isReadOnly()
    {
        return view.isReadOnly();
    }

    public List<Column> getColumns()
    {
        return view.getColumns();
    }
}
