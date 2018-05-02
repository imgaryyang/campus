package com.gzzm.platform.message.sms;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.AuthDeptDisplay;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.view.components.CButton;

/**
 * @author camel
 * @date 12-4-28
 */
@Service(url = "/phonelist")
public class PhoneListCrud extends DeptOwnedNormalCrud<PhoneList, Integer>
{
    @Like
    private String listName;

    public PhoneListCrud()
    {
    }

    public String getListName()
    {
        return listName;
    }

    public void setListName(String listName)
    {
        this.listName = listName;
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
        PageTableView view = getAuthDeptIds() == null || getAuthDeptIds().size() > 1 ?
                new ComplexTableView(new AuthDeptDisplay(), "deptId", true) : new PageTableView(true);

        view.addComponent("列表名称", "listName");

        view.addColumn("列表名称", "listName");
        view.addColumn("列表管理", new CButton("列表管理", "showItems(${listId})"));

        view.defaultInit();
        view.addButton(Buttons.sort());

        view.importJs("/platform/message/phonelist.js");

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("列表名称", "listName");

        view.addDefaultButtons();

        return view;
    }
}
