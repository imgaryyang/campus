package com.gzzm.platform.organ;

import net.cyan.arachne.components.*;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 部门树，不做权限过滤
 *
 * @author camel
 * @date 2011-6-7
 */
public class DeptTreeModel implements LazyPageTreeModel<DeptInfo>, SearchablePageTreeModel<DeptInfo>,
        CheckBoxTreeModel<DeptInfo>
{
    @Inject
    private static Provider<DeptService> serviceProvider;

    /**
     * 根部门ID
     */
    private Integer rootId;

    private boolean showBox;

    private DeptService service;

    public DeptTreeModel()
    {
    }

    public boolean isShowBox()
    {
        return showBox;
    }

    public void setShowBox(boolean showBox)
    {
        this.showBox = showBox;
    }

    public Integer getRootId()
    {
        return rootId;
    }

    public void setRootId(Integer rootId)
    {
        this.rootId = rootId;
    }

    private DeptService getService()
    {
        if (service == null)
            service = serviceProvider.get();
        return service;
    }

    public boolean isLazyLoad(DeptInfo dept) throws Exception
    {
        return true;
    }

    public void beforeLazyLoad(String id) throws Exception
    {
    }

    @SuppressWarnings("unchecked")
    public Collection<DeptInfo> search(String text) throws Exception
    {
        return (List<DeptInfo>) getService().searchDept(text, null);
    }

    public DeptInfo getParent(DeptInfo dept) throws Exception
    {
        return dept.parentDept();
    }

    public DeptInfo getRoot() throws Exception
    {
        return rootId == null ? getService().getRoot() : getService().getDept(rootId);
    }

    public boolean isLeaf(DeptInfo dept) throws Exception
    {
        return dept.subDepts().size() == 0;
    }

    public int getChildCount(DeptInfo dept) throws Exception
    {
        return dept.subDepts().size();
    }

    public DeptInfo getChild(DeptInfo parent, int index) throws Exception
    {
        return parent.subDepts().get(index);
    }

    public String getId(DeptInfo parent) throws Exception
    {
        return parent.getDeptId().toString();
    }

    public String toString(DeptInfo dept) throws Exception
    {
        return dept.getDeptName();
    }

    public DeptInfo getNode(String id) throws Exception
    {
        return getService().getDept(Integer.valueOf(id));
    }

    public Boolean isRootVisible()
    {
        return true;
    }

    public boolean hasCheckBox(DeptInfo dept) throws Exception
    {
        return showBox;
    }

    public Boolean isChecked(DeptInfo dept) throws Exception
    {
        return false;
    }

    public boolean isSearchable() throws Exception
    {
        return true;
    }
}
