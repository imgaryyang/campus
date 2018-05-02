package com.gzzm.platform.commons.filestore;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.StringUtils;
import net.cyan.nest.annotation.Inject;

import java.util.List;

/**
 * @author camel
 * @date 13-12-3
 */
@Service
public class FileCatalogDisplay extends BaseTreeShow<CatalogItem, String>
{
    @Inject
    private UserOnlineInfo userOnlineInfo;

    @Inject
    private FileStoreService service;

    private Integer deptId;

    private boolean readOnly = true;

    public FileCatalogDisplay()
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

    @Override
    public CatalogItem getRoot() throws Exception
    {
        return CatalogItem.getRoot();
    }

    public String getKey(CatalogItem entity) throws Exception
    {
        return entity.valueOf();
    }

    @Override
    public CatalogItem getNode(String key) throws Exception
    {
        String type;
        String id;
        int index = key.indexOf(':');
        if (index > 0)
        {
            type = key.substring(0, index);
            id = key.substring(index + 1);
        }
        else
        {
            type = key;
            id = "";
        }

        return new CatalogItem(type, id, "", false);
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

    @Override
    public List<CatalogItem> getChildren(String parent) throws Exception
    {
        String type;
        String id;
        int index = parent.indexOf(':');
        if (index > 0)
        {
            type = parent.substring(0, index);
            id = parent.substring(index + 1);
        }
        else
        {
            type = parent;
            id = "";
        }

        return service.getChildCatalogs(type, id, userOnlineInfo.getUserId(), getDeptId(), readOnly);
    }

    @Override
    public boolean hasChildren(CatalogItem item) throws Exception
    {
        if ("root".equals(item.getType()))
            return true;

        if (StringUtils.isEmpty(item.getId()))
            return getChildren(item).size() > 0;

        return !item.isLeaf();
    }

    @Override
    protected Object createTreeView() throws Exception
    {
        SelectableTreeView view = new SelectableTreeView();

        view.setRootVisible(false);

        return view;
    }
}
