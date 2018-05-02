package com.gzzm.platform.flow.worksheet;

import com.gzzm.platform.annotation.UserId;
import net.cyan.arachne.components.LazyPageTreeModel;
import net.cyan.nest.annotation.Inject;

import java.util.List;

/**
 * @author camel
 * @date 11-10-26
 */
public class WorkSheetCatalogTreeModel implements LazyPageTreeModel<WorkSheetCatalog>
{
    @Inject
    private WorkSheetCatalogDao dao;

    @UserId
    private Integer userId;

    private String type;

    private List<WorkSheetCatalog> topCatalogs;

    public WorkSheetCatalogTreeModel()
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

    public Boolean isRootVisible()
    {
        return false;
    }

    public WorkSheetCatalog getRoot() throws Exception
    {
        return dao.getRootCatalog();
    }

    private List<WorkSheetCatalog> getTopCatalogs() throws Exception
    {
        if (topCatalogs == null)
            topCatalogs = dao.getTopCatalogs(userId, type);

        return topCatalogs;
    }

    public WorkSheetCatalog getNode(String id) throws Exception
    {
        return dao.getCatalog(Integer.valueOf(id));
    }

    public boolean isLazyLoad(WorkSheetCatalog workSheetCatalog) throws Exception
    {
        return workSheetCatalog.getCatalogId() != 0;
    }

    public void beforeLazyLoad(String id) throws Exception
    {
    }

    public boolean isLeaf(WorkSheetCatalog workSheetCatalog) throws Exception
    {
        return workSheetCatalog.getCatalogId() != 0 && workSheetCatalog.getChildren().size() == 0;
    }

    public int getChildCount(WorkSheetCatalog parent) throws Exception
    {
        if (parent.getCatalogId() == 0)
            return getTopCatalogs().size();

        return parent.getChildren().size();
    }

    public WorkSheetCatalog getChild(WorkSheetCatalog parent, int index) throws Exception
    {
        if (parent.getCatalogId() == 0)
            return getTopCatalogs().get(index);

        return parent.getChildren().get(index);
    }

    public String getId(WorkSheetCatalog workSheetCatalog) throws Exception
    {
        return workSheetCatalog.getCatalogId().toString();
    }

    public String toString(WorkSheetCatalog workSheetCatalog) throws Exception
    {
        return workSheetCatalog.toString();
    }
}
