package com.gzzm.ods.redhead;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;

/**
 * @author camel
 * @date 2010-12-16
 */
@Service(url = "/ods/RedHeadType")
public class RedHeadTypeCrud extends BaseNormalCrud<RedHeadType, Integer>
{
    public RedHeadTypeCrud()
    {
        setLog(true);
    }

    @Override
    public String getOrderField()
    {
        return "orderId";
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();
        view.addColumn("类型名称", "typeName");

        view.defaultInit();
        view.addButton(Buttons.sort());

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();
        view.addComponent("类型名称", "typeName");

        view.addDefaultButtons();

        return view;
    }
}
