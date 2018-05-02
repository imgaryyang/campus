package com.gzzm.platform.commons;

import com.gzzm.platform.commons.crud.SubListView;
import net.cyan.crud.view.Column;

import java.util.List;

/**
 * @author camel
 * @date 2010-12-7
 */
public class SubListViewModel
{
    private SubListView view;

    private String id;

    public SubListViewModel(SubListView view, String id)
    {
        this.view = view;
        this.id = id;
    }

    public SubListView getView()
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
