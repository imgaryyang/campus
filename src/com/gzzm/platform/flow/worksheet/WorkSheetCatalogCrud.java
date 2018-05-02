package com.gzzm.platform.flow.worksheet;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.nest.annotation.Inject;

/**
 * @author camel
 * @date 11-10-26
 */
@Service(url = "/flow/catalog")
public class WorkSheetCatalogCrud extends UserOwnedTreeCrud<WorkSheetCatalog, Integer>
{
    @Inject
    private WorkSheetCatalogDao dao;

    private String type;

    public WorkSheetCatalogCrud()
    {
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    @Override
    public WorkSheetCatalog getRoot() throws Exception
    {
        return dao.getRootCatalog();
    }

    @Override
    public void initEntity(WorkSheetCatalog entity) throws Exception
    {
        super.initEntity(entity);

        entity.setType(type);
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
