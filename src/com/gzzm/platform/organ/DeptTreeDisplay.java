package com.gzzm.platform.organ;

import com.gzzm.platform.commons.crud.SelectableTreeView;
import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.commons.util.*;
import net.cyan.crud.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 部门树展示
 *
 * @author camel
 * @date 2009-12-15
 */
public class DeptTreeDisplay extends TreeCrud<DeptInfo, Integer> implements SearchTreeCrud<DeptInfo, Integer>
{
    @Inject
    private static Provider<DeptService> serviceProvider;

    @Inject
    private static Provider<UserOnlineInfo> userOnlineInfoProvider;

    /**
     * 根部门ID
     */
    private Integer rootId;

    /**
     * 部门级别，只加载此级别的部门
     */
    private int level = -1;

    /**
     * 部门过滤器，只显示此过滤器接受的部门
     */
    private Filter<DeptInfo> filter;

    /**
     * 条件，之显示满足此条件的部门，当filter为null是有效
     */
    private String condition;

    private DeptService service;

    public DeptTreeDisplay()
    {
    }

    @SuppressWarnings("unchecked")
    public List<DeptInfo> getChildren(Integer parent) throws Exception
    {
        return (List<DeptInfo>) (getService().getDeptTree(parent, level, filter).subDepts());
    }

    public boolean hasChildren(DeptInfo node) throws Exception
    {
        Collection<? extends DeptInfo> children = node.subDepts();
        return !children.isEmpty();
    }

    public DeptInfo getNode(Integer key) throws Exception
    {
        return getService().getDeptTree(key, level, filter);
    }

    public DeptInfo getRoot() throws Exception
    {
        DeptService deptService = getService();
        Integer rootId = getRootId();
        if (rootId == null)
            rootId = userOnlineInfoProvider.get().getBureauId();

        return rootId == null ? deptService.getRoot() : deptService.getDept(rootId);
    }

    public Integer getKey(DeptInfo entity) throws Exception
    {
        return entity.getDeptId();
    }

    public Integer getRootId()
    {
        return rootId;
    }

    public void setRootId(Integer rootId)
    {
        this.rootId = rootId;
    }

    public int getLevel()
    {
        return level;
    }

    public void setLevel(int level)
    {
        this.level = level;
    }

    protected Filter<DeptInfo> getFilter()
    {
        if (filter == null && condition != null)
            filter = ExpressionDeptFilter.getFilter(condition);
        return filter;
    }

    public void setFilter(Filter<DeptInfo> filter)
    {
        this.filter = filter;
    }

    public String getCondition()
    {
        return condition;
    }

    public void setCondition(String condition)
    {
        this.condition = condition;
    }

    @Override
    protected Object createView() throws Exception
    {
        return new SelectableTreeView();
    }

    private DeptService getService()
    {
        if (service == null)
            service = serviceProvider.get();
        return service;
    }

    @SuppressWarnings("unchecked")
    public Collection<DeptInfo> search(String text) throws Exception
    {
        return (Collection<DeptInfo>) getService().searchDept(text, null);
    }

    public DeptInfo getParent(DeptInfo dept) throws Exception
    {
        return dept.parentDept();
    }

    public boolean supportSearch() throws Exception
    {
        return true;
    }
}
