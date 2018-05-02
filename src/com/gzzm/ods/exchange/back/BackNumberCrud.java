package com.gzzm.ods.exchange.back;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.AuthDeptDisplay;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.annotation.Like;

/**
 * 退文编号维护
 *
 * @author ldp
 * @date 2018/1/11
 */
@Service(url = "/ods/exchange/backnumber")
public class BackNumberCrud extends DeptOwnedNormalCrud<BackNumber, Integer>
{

    @Like
    private String backNumberName;

    public BackNumberCrud()
    {
    }

    @NotSerialized
    public String getText() throws Exception
    {
        BackNumber backNumber = getEntity();
        if (backNumber != null)
        {
            return backNumber.getText();
        }

        return null;
    }

    public String getBackNumberName()
    {
        return backNumberName;
    }

    public void setBackNumberName(String backNumberName)
    {
        this.backNumberName = backNumberName;
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

        view.addComponent("编号名称", "backNumberName");

        view.addColumn("编号名称", "backNumberName").setWidth("250");
        view.addColumn("编号内容", "text").setOrderFiled("backNumber");

        view.defaultInit(false);
        view.addButton(Buttons.sort());

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addHidden("entity.backNumber");

        view.addComponent("编号名称", "backNumberName");
        view.addComponent("退文编号", "this.text");

        view.addDefaultButtons();

        view.importJs("/ods/exchange/back/backnumber.js");

        return view;
    }
}
