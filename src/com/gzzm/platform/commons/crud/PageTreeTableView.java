package com.gzzm.platform.commons.crud;

import net.cyan.crud.*;
import net.cyan.crud.view.*;

/**
 * 本系统使用的TreeView
 *
 * @author camel
 * @date 2009-10-21
 */
public class PageTreeTableView extends AbstractPageTableView<BaseTreeTableView, PageTreeTableView>
        implements TreeTableView
{
    public PageTreeTableView()
    {
        super(new BaseTreeTableView());
    }

    public boolean isRootVisible()
    {
        return mainBody.isRootVisible();
    }

    public PageTreeTableView setRootVisible(boolean rootVisible)
    {
        mainBody.setRootVisible(rootVisible);
        return this;
    }

    public boolean isSupportSearch()
    {
        return mainBody.isSupportSearch();
    }

    public PageTreeTableView enableSupportSearch()
    {
        mainBody.enableSupportSearch();
        return this;
    }

    @Override
    public PageTreeTableView defaultInit(String editForward, boolean duplicate)
    {
        super.defaultInit(editForward, duplicate);

        if (CrudConfig.getContext().getCrud() instanceof SearchTreeCrud)
            enableSupportSearch();

        return this;
    }
}
