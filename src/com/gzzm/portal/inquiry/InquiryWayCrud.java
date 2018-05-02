package com.gzzm.portal.inquiry;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;

/**
 * @author camel
 * @date 12-11-7
 */
@Service(url = "/portal/inquiry/way")
public class InquiryWayCrud extends BaseNormalCrud<InquiryWay, Integer>
{
    public InquiryWayCrud()
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

        view.addColumn("来信方式", "wayName");

        view.defaultInit();
        view.addButton(Buttons.sort());

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("来信方式", "wayName");
        view.addDefaultButtons();

        return view;
    }
}
