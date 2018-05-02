package com.gzzm.portal.cms.information;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.nest.annotation.Inject;

/**
 * @author camel
 * @date 12-8-29
 */
@Service(url = "/portal/information_catalog")
public class InformationCatalogCrud extends BaseTreeCrud<InformationCatalog, Integer>
{
    @Inject
    private InformationDao dao;

    public InformationCatalogCrud()
    {
        setLog(true);
    }

    @Override
    public InformationCatalog getRoot() throws Exception
    {
        return dao.getRootCatalog();
    }

    @Override
    public String getOrderField()
    {
        return "orderId";
    }

    @Override
    protected Object createTreeView() throws Exception
    {
        PageTreeView view = new PageTreeView();

        view.addDefaultButtons();

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("分类名称", "catalogName");

        view.addDefaultButtons();

        return view;
    }
}
