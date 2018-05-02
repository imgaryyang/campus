package com.gzzm.ods.redhead;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.AuthDeptDisplay;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.annotation.Like;

/**
 * 红头标题维护
 *
 * @author camel
 * @date 2010-12-17
 */
@Service(url = "/ods/RedHeadTitle")
public class RedHeadTitleCrud extends DeptOwnedNormalCrud<RedHeadTitle, Integer>
{
    @Like
    private String title;

    public RedHeadTitleCrud()
    {
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();

        setDefaultDeptId();
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = (getAuthDeptIds() != null && getAuthDeptIds().size() == 1) ? new PageTableView() :
                new ComplexTableView(new AuthDeptDisplay(), "deptId");

        view.addComponent("标题", "title");

        view.addColumn("标题", "title");
        view.defaultInit();
        view.addButton(Buttons.sort());

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("标题", "title");
        view.addDefaultButtons();

        return view;
    }
}
