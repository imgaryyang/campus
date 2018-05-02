package com.gzzm.portal.org;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.view.components.CTextArea;
import net.cyan.nest.annotation.Inject;

/**
 * @author lk
 * @date 13-9-30
 */
@Service(url = "/portal/org/internalorg")
public class InternalOrgCrud extends SubListCrud<InternalOrg, Integer>
{
    @Inject
    private OrgInfoDao dao;

    private Integer orgId;

    public InternalOrgCrud()
    {
        setLog(true);
    }

    public Integer getOrgId()
    {
        return orgId;
    }

    public void setOrgId(Integer orgId)
    {
        this.orgId = orgId;
    }

    @Override
    protected String getParentField()
    {
        return "orgId";
    }

    protected void initListView(SubListView view) throws Exception
    {
        view.addColumn("内部机构名称", "deptName");
        view.addColumn("联系电话", "phone");
        view.addColumn("传真号码", "fax");
        view.addColumn("电子邮箱", "email");
        view.addColumn("职能", "duty");
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("机构名称", "deptName");
        view.addComponent("联系电话", "phone");
        view.addComponent("电子邮件", "email");

        view.addComponent("职能", new CTextArea("duty"));

        view.addDefaultButtons();

        return view;
    }
}
