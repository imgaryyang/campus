package com.gzzm.ods.flow;

import net.cyan.arachne.components.LazyPageTreeModel;
import net.cyan.nest.annotation.Inject;

import java.util.List;

/**
 * @author camel
 * @date 12-4-26
 */
public class OdInstanceCatalogTreeModel implements LazyPageTreeModel<OdInstanceCatalog>
{
    @Inject
    private OdFlowDao dao;

    private Integer deptId;

    private List<OdInstanceCatalog> topCatalogs;

    public OdInstanceCatalogTreeModel()
    {
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public OdInstanceCatalog getRoot() throws Exception
    {
        return dao.getRootInstanceCatalog();
    }

    private List<OdInstanceCatalog> getTopCatalogs() throws Exception
    {
        if (topCatalogs == null)
            topCatalogs = dao.getTopInstanceCatalogs(deptId);

        return topCatalogs;
    }

    public OdInstanceCatalog getNode(String id) throws Exception
    {
        return dao.getInstanceCatalog(Integer.valueOf(id));
    }

    public boolean isLazyLoad(OdInstanceCatalog odInstanceCatalog) throws Exception
    {
        return odInstanceCatalog.getCatalogId() != 0;
    }

    public void beforeLazyLoad(String id) throws Exception
    {
    }

    public boolean isLeaf(OdInstanceCatalog odInstanceCatalog) throws Exception
    {
        return odInstanceCatalog.getCatalogId() != 0 && odInstanceCatalog.getChildren().size() == 0;
    }

    public int getChildCount(OdInstanceCatalog parent) throws Exception
    {
        if (parent.getCatalogId() == 0)
            return getTopCatalogs().size();

        return parent.getChildren().size();
    }

    public OdInstanceCatalog getChild(OdInstanceCatalog parent, int index) throws Exception
    {
        if (parent.getCatalogId() == 0)
            return getTopCatalogs().get(index);

        return parent.getChildren().get(index);
    }

    public String getId(OdInstanceCatalog odInstanceCatalog) throws Exception
    {
        return odInstanceCatalog.getCatalogId().toString();
    }

    public String toString(OdInstanceCatalog odInstanceCatalog) throws Exception
    {
        return odInstanceCatalog.toString();
    }

    public Boolean isRootVisible()
    {
        return false;
    }
}
