package com.gzzm.platform.commons.crud;

import net.cyan.crud.view.*;

/**
 * 本系统使用的TableView
 *
 * @author camel
 * @date 2009-10-22
 */
public class PageTableView extends AbstractPageTableView<BaseTableView, PageTableView> implements RowRemarkTableView
{
    public PageTableView(BaseTableView body)
    {
        super(body);
    }

    public PageTableView(boolean checkable)
    {
        super(new BaseTableView(checkable));
    }

    public PageTableView()
    {
        super(new BaseTableView());
    }

    public Column getRowRemark()
    {
        return mainBody.getRowRemark();
    }

    @SuppressWarnings("unchecked")
    public PageTableView setRowRemark(Column remark)
    {
        mainBody.setRowRemark(remark);

        return this;
    }

    public PageTableView setRowRemark(Cell cell)
    {
        mainBody.setRowRemark(cell);

        return this;
    }

    public PageTableView setRowRemark(String field)
    {
        mainBody.setRowRemark(field);

        return this;
    }
}
