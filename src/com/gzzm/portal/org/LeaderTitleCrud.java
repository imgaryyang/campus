package com.gzzm.portal.org;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.AuthDeptDisplay;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.annotation.Like;

/**
 * @author camel
 * @date 2014/8/13
 */
@Service(url = "/portal/org/title")
public class LeaderTitleCrud extends DeptOwnedNormalCrud<LeaderTitle, Integer>
{
    @Like
    private String title;

    public LeaderTitleCrud()
    {
        setLog(true);
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
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("职位名称", "titleName");

        view.addDefaultButtons();

        return view;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view;

        if (getAuthDeptIds() == null || getAuthDeptIds().size() > 0)
            view = new ComplexTableView(new AuthDeptDisplay(), "deptId");
        else
            view = new PageTableView();

        view.addComponent("职位名称", "titleName");
        view.addColumn("职位名称", "titleName");

        view.defaultInit();
        view.addButton(Buttons.sort());

        if (view instanceof ComplexTableView)
            ((ComplexTableView) view).enableDD();

        return view;
    }
}
