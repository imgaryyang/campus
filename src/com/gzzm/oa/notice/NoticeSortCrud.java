package com.gzzm.oa.notice;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.AuthDeptDisplay;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.annotation.Like;

/**
 * @author camel
 * @date 12-9-13
 */
@Service(url = "/oa/notice/sort")
public class NoticeSortCrud extends DeptOwnedNormalCrud<NoticeSort, Integer>
{
    @Like
    private String sortName;

    public NoticeSortCrud()
    {
    }

    public String getSortName()
    {
        return sortName;
    }

    public void setSortName(String sortName)
    {
        this.sortName = sortName;
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();

        setDefaultDeptId();
    }

    @Override
    public String getOrderField()
    {
        return "orderId";
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view;
        if (getAuthDeptIds() != null && getAuthDeptIds().size() == 1)
        {
            view = new PageTableView();
        }
        else
        {
            view = new ComplexTableView(new AuthDeptDisplay(), "deptId", true);
        }

        view.addComponent("名称", "sortName");

        view.addColumn("名称", "sortName");

        view.defaultInit();
        view.addButton(Buttons.sort());

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("名称", "sortName");

        view.addDefaultButtons();

        return view;
    }
}
