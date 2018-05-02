package com.gzzm.ods.exchange.back;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;

/**
 * 退文类型维护
 *
 * @author ldp
 * @date 2018/1/9
 */
@Service(url = "/ods/exchange/backreasontype")
public class BackReasonTypeCrud extends BaseNormalCrud<BackReasonType, Integer>
{
    public BackReasonTypeCrud()
    {
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

        view.defaultInit(false);
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
