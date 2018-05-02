package com.gzzm.portal.inquiry;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;

/**
 * @author camel
 * @date 12-11-7
 */
@Service(url = "/portal/inquiry/type")
public class InquiryTypeCrud extends BaseNormalCrud<InquiryType, Integer>
{
    public InquiryTypeCrud()
    {
        setLog(true);
    }

    @Override
    public String getOrderField()
    {
        return "orderId";
    }

    @Override
    public int getOrderFieldLength()
    {
        return 3;
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
