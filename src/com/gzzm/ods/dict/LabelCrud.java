package com.gzzm.ods.dict;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.AuthDeptDisplay;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.view.components.CTextArea;

/**
 * @author camel
 * @date 13-11-13
 */
@Service(url = "/ods/dict/Label")
public class LabelCrud extends DeptOwnedNormalCrud<Label, Integer>
{
    public LabelCrud()
    {
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

        view.addColumn("标签名称", "labelName");
        view.addColumn("条件", "condition").setAutoExpand(true);

        view.defaultInit();
        view.addButton(Buttons.sort());

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("标签名称", "labelName");
        view.addComponent("条件", new CTextArea("condition"));

        view.addDefaultButtons();

        return view;
    }
}
