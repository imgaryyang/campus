package com.gzzm.platform.commons.filestore;

import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.arachne.annotation.Service;
import net.cyan.arachne.components.LazyPageTreeModel;
import net.cyan.commons.util.StringUtils;
import net.cyan.nest.annotation.Inject;

import java.util.List;

/**
 * @author camel
 * @date 13-10-17
 */
@Service
public class FileCatalogTreeModel implements LazyPageTreeModel<CatalogItem>
{
    private Integer deptId;

    private boolean readOnly;

    @Inject
    private UserOnlineInfo userOnlineInfo;

    @Inject
    private FileStoreService service;

    public FileCatalogTreeModel()
    {
    }

    public Integer getDeptId()
    {
        if (deptId == null)
            deptId = userOnlineInfo.getDeptId();
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public boolean isReadOnly()
    {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly)
    {
        this.readOnly = readOnly;
    }

    public boolean isLazyLoad(CatalogItem item) throws Exception
    {
        return !StringUtils.isEmpty(item.getId());
    }

    public void beforeLazyLoad(String id) throws Exception
    {
    }

    public CatalogItem getRoot() throws Exception
    {
        return CatalogItem.getRoot();
    }

    public boolean isLeaf(CatalogItem item) throws Exception
    {
        if ("root".equals(item.getType()))
            return false;

        if (StringUtils.isEmpty(item.getId()))
            return getChildren(item).size() == 0;

        return item.isLeaf();
    }

    private List<CatalogItem> getChildren(CatalogItem item) throws Exception
    {
        List<CatalogItem> children = item.getChildren();

        if (children == null)
        {
            children = service.getChildCatalogs(item.getType(), item.getId(), userOnlineInfo.getUserId(),
                    getDeptId(), readOnly);

            item.setChildren(children);
        }

        return children;
    }

    public int getChildCount(CatalogItem parent) throws Exception
    {
        return getChildren(parent).size();
    }

    public CatalogItem getChild(CatalogItem parent, int index) throws Exception
    {
        return getChildren(parent).get(index);
    }

    public String getId(CatalogItem item) throws Exception
    {
        return item.valueOf();
    }

    public String toString(CatalogItem item) throws Exception
    {
        return item.toString();
    }

    public CatalogItem getNode(String id) throws Exception
    {
        String type;
        int index = id.indexOf(':');
        if (index > 0)
        {
            type = id.substring(0, index);
            id = id.substring(index + 1);
        }
        else
        {
            type = id;
            id = "";
        }

        return new CatalogItem(type, id, "", false);
    }

    public Boolean isRootVisible()
    {
        return false;
    }
}
