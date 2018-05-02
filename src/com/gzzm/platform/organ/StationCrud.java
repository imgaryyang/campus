package com.gzzm.platform.organ;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.components.CTextArea;

/**
 * 岗位维护
 *
 * @author camel
 * @date 2009-7-18
 */
@Service(url = "/StationCrud")
public class StationCrud extends DeptOwnedNormalCrud<Station, Integer>
{
    @Like("stationName")
    private String name;

    public StationCrud()
    {
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
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
        ComplexTableView view = new ComplexTableView(new AuthDeptDisplay(), "deptId", true);

        view.addComponent("名称", "name");

        view.addColumn("名称", "stationName");
        view.addColumn("所属部门", "dept.deptName");
        view.addColumn("被下属部门使用", "inheritable");
        view.addColumn("说明", "remark");

        view.defaultInit();
        view.addButton(Buttons.sort());

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("岗位名称", "stationName");
        view.addComponent("被子部门使用", "inheritable");
        view.addComponent("岗位说明", new CTextArea("remark"));

        view.addDefaultButtons();

        return view;
    }
}