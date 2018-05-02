package com.gzzm.ods.sendnumber;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.AuthDeptDisplay;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.annotation.Like;


/**
 * 发文字号管理
 *
 * @author fwj
 * @date 2011-6-30
 */
@Service(url = "/ods/SendNumber")
public class SendNumberCrud extends DeptOwnedNormalCrud<SendNumber, Integer>
{
    @Like
    private String sendNumberName;

    public SendNumberCrud()
    {
    }

    public String getSendNumberName()
    {
        return sendNumberName;
    }

    public void setSendNumberName(String sendNumberName)
    {
        this.sendNumberName = sendNumberName;
    }

    @NotSerialized
    public String getText() throws Exception
    {
        SendNumber sendNumber = getEntity();
        if (sendNumber != null)
        {
            return sendNumber.getText();
        }

        return null;
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

        view.addComponent("发文字号名称", "sendNumberName");

        view.addColumn("发文字号名称", "sendNumberName").setWidth("250");
        view.addColumn("发文字号内容", "text").setOrderFiled("sendNumber");

        view.defaultInit();
        view.addButton(Buttons.sort());

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addHidden("entity.sendNumber");

        view.addComponent("字号名称", "sendNumberName");
        view.addComponent("发文字号", "this.text");

        view.addDefaultButtons();

        view.importJs("/ods/sendnumber/sendnumber.js");

        return view;
    }
}
