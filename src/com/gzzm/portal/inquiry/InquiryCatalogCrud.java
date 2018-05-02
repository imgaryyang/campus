package com.gzzm.portal.inquiry;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.nest.annotation.Inject;

/**
 * @author camel
 * @date 12-11-7
 */
@Service(url = "/portal/inquiry/catalog")
public class InquiryCatalogCrud extends BaseTreeCrud<InquiryCatalog, Integer>
{
    @Inject
    private InquiryDao dao;

    public InquiryCatalogCrud()
    {
        setLog(true);
    }

    @Override
    public InquiryCatalog getRoot() throws Exception
    {
        return dao.getRootCatalog();
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
    protected Object createTreeView() throws Exception
    {
        PageTreeView view = new PageTreeView();

        view.defaultInit();

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("归类名称", "catalogName");
        view.addDefaultButtons();

        return view;
    }
}
