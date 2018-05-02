package com.gzzm.ods.flow;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.nest.annotation.Inject;

/**
 * @author camel
 * @date 12-4-25
 */
@Service(url = "/ods/flow/instancecatalog")
public class OdInstanceCatalogCrud extends DeptOwnedTreeCrud<OdInstanceCatalog, Integer>
{
    @Inject
    private OdFlowDao dao;

    public OdInstanceCatalogCrud()
    {
    }

    @Override
    public OdInstanceCatalog getRoot() throws Exception
    {
        return dao.getRootInstanceCatalog();
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

        view.addComponent("目录名称", "catalogName");

        view.addDefaultButtons();

        return view;
    }
}
