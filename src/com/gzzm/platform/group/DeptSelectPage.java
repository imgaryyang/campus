package com.gzzm.platform.group;

import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.Filter;
import net.cyan.nest.annotation.Inject;

/**
 * 部门选择页面
 *
 * @author camel
 * @date 11-9-29
 */
@Service
public class DeptSelectPage
{
    @Inject
    private DeptService service;

    /**
     * 可选的部门列表
     */
    private Integer[] selectable;

    /**
     * 已选的部门
     */
    private Integer[] selected;

    private AuthDeptTreeModel deptTree;

    private String appId = Member.DEPT_SELECT_APP;

    public DeptSelectPage()
    {
    }

    public Integer[] getSelectable()
    {
        return selectable;
    }

    public void setSelectable(Integer[] selectable)
    {
        this.selectable = selectable;
    }

    public Integer[] getSelected()
    {
        return selected;
    }

    public void setSelected(Integer[] selected)
    {
        this.selected = selected;
    }

    public String getAppId()
    {
        return appId;
    }

    public void setAppId(String appId)
    {
        this.appId = appId;
    }

    @NotSerialized
    public DeptInfo[] getSelectedDepts() throws Exception
    {
        return service.getDepts(selected);
    }

    @NotSerialized
    public DeptInfo[] getSelectableDepts() throws Exception
    {
        return service.getDepts(selectable);
    }

    public AuthDeptTreeModel getDeptTree()
    {
        if (deptTree == null)
        {
            deptTree = new AuthDeptTreeModel();
            deptTree.setAppId(appId);
            deptTree.setFilter(getFilter());
        }

        return deptTree;
    }

    protected Filter<DeptInfo> getFilter()
    {
        return null;
    }

    @Service(url = "/deptselect")
    public String showPage()
    {
        return "deptselect";
    }
}
