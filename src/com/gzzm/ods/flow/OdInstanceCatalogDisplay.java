package com.gzzm.ods.flow;

import com.gzzm.platform.annotation.AuthDeptIds;
import com.gzzm.platform.commons.crud.*;
import net.cyan.nest.annotation.Inject;

import java.util.Collection;

/**
 * @author camel
 * @date 12-4-25
 */
public class OdInstanceCatalogDisplay extends BaseTreeDisplay<OdInstanceCatalog, Integer>
{
    @Inject
    private OdFlowDao dao;

    @AuthDeptIds
    private Collection<Integer> authDeptIds;

    private Integer deptId;

    public OdInstanceCatalogDisplay()
    {
        addOrderBy("orderId");
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    @Override
    public OdInstanceCatalog getRoot() throws Exception
    {
        return dao.getRootInstanceCatalog();
    }

    @Override
    protected void beforeShowTree() throws Exception
    {
        super.beforeShowTree();

        if (deptId == null)
            deptId = DeptOwnedCrudUtils.getDefaultDeptId(authDeptIds, this);
    }

    @Override
    protected Object createTreeView() throws Exception
    {
        return new SelectableTreeView();
    }
}
