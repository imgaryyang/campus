package com.gzzm.portal.org;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.nest.annotation.Inject;

/**
 * @author lk
 * @date 13-9-27
 */
@Service(url = "/portal/org/type")
public class OrgTypeCrud extends BaseTreeCrud<OrgType, Integer>
{
    @Inject
    private OrgInfoDao dao;

    public OrgTypeCrud()
    {
        setLog(true);
    }

    @Override
    public String getOrderField()
    {
        return "orderId";
    }

    @Override
    public OrgType getRoot() throws Exception
    {
        return dao.getRootType();
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

        view.addComponent("类型名称", "typeName");

        view.addDefaultButtons();

        return view;
    }
}
