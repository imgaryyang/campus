package com.gzzm.platform.commons.crud;

import com.gzzm.platform.annotation.AuthDeptIds;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.annotation.NotCondition;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 部门统计
 *
 * @author camel
 * @date 2010-7-12
 */
@Service
public abstract class DeptTreeStat extends BaseTreeStat<Dept, Integer> implements DeptOwnedCrud
{
    @Inject
    protected DeptService service;

    /**
     * 用部门id作查询条件
     */
    private Integer deptId;

    /**
     * 根级部门的id，表示查询此部门的所有子部门的数据
     */
    private Collection<Integer> topDeptIds;

    /**
     * 拥有权限的部门列表，通过setAuthDeptIds注入，以方便子类覆盖注入方式
     */
    @NotSerialized
    private Collection<Integer> authDeptIds;

    /**
     * 部门id列表，可同时查询多个部门
     */
    @NotCondition
    private List<Integer> deptIds;

    /**
     * 部门id列表，可同时查询多个部门
     */
    @NotSerialized
    private Collection<Integer> queryDeptIds;

    private AuthDeptTreeModel deptTree;

    public DeptTreeStat()
    {
    }

    public Collection<Integer> getAuthDeptIds()
    {
        return authDeptIds;
    }

    @AuthDeptIds
    protected void setAuthDeptIds(Collection<Integer> authDeptIds)
    {
        this.authDeptIds = authDeptIds;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public Collection<Integer> getTopDeptIds()
    {
        return topDeptIds;
    }

    public void setTopDeptIds(Collection<Integer> topDeptId)
    {
        this.topDeptIds = topDeptId;
    }

    public List<Integer> getDeptIds()
    {
        return deptIds;
    }

    public void setDeptIds(List<Integer> deptIds)
    {
        this.deptIds = deptIds;
    }

    public Collection<Integer> getQueryDeptIds()
    {
        return queryDeptIds;
    }

    public void setQueryDeptIds(Collection<Integer> queryDeptIds)
    {
        this.queryDeptIds = queryDeptIds;
    }

    @Select(field = {"deptIds", "topDeptIds"})
    public AuthDeptTreeModel getDeptTree()
    {
        if (deptTree == null)
        {
            deptTree = new AuthDeptTreeModel();
            deptTree.setShowBox(true);
        }

        return deptTree;
    }

    @Override
    protected void beforeShowTree() throws Exception
    {
        super.beforeShowTree();

        DeptOwnedCrudUtils.beforeQuery(this);
    }

    @Override
    protected void beforeLoad() throws Exception
    {
        super.beforeLoad();

        DeptOwnedCrudUtils.beforeQuery(this);
    }

    protected Integer getDefaultDeptId() throws Exception
    {
        return DeptOwnedCrudUtils.getDefaultDeptId(this);
    }

    @SuppressWarnings("UnusedDeclaration")
    protected void setDefaultDeptId() throws Exception
    {
        if (getDeptId() == null)
            setDeptId(DeptOwnedCrudUtils.getDefaultDeptId(this));

        setDeptIds(null);
        setQueryDeptIds(null);
    }

    protected void initStats() throws Exception
    {
        addColumn("deptName", "dept.deptName");
    }

    @Override
    public Map<String, Object> getRoot() throws Exception
    {
        return getEmptyRoot(getRootKey());
    }

    protected Integer getRootKey() throws Exception
    {
        return -1;
    }

    @Override
    protected List<Integer> getChildKeys(Integer parent) throws Exception
    {
        List<SimpleDeptInfo> depts =
                service.getAuthDeptTree(parent == -1 ? null : parent, null, getQueryDeptIds());
        List<Integer> results = new ArrayList<Integer>(depts.size());

        for (SimpleDeptInfo dept : depts)
            results.add(dept.getDeptId());

        return results;
    }

    @Override
    protected Collection<Integer> getDescendantKeys(Integer key) throws Exception
    {
        DeptInfo dept = key == -1 ? service.getRoot() : service.getDept(key);

        if (dept == null)
            return Collections.emptyList();

        List<Integer> result = dept.allSubDeptIds();
        if (queryDeptIds != null)
            result.retainAll(queryDeptIds);

        return result;
    }

    @Override
    public List<Map<String, Object>> getChildren(Integer parent) throws Exception
    {
        if (parent == -2)
            return Collections.emptyList();

        List<Map<String, Object>> result = super.getChildren(parent);

        if (parent == -1 && result.size() > 1)
            addTotal(result);

        return result;
    }

    protected void addTotal(List<Map<String, Object>> result) throws Exception
    {
        String totalLabel = getTotalLabel();
        if (totalLabel != null)
        {
            Map<String, Object> total = loadNode(-1);
            setKey(total, -2);
            setLeaf(total, true);
            total.put("deptName", Tools.getMessage(totalLabel, this));

            addTotal(result, total);
        }
    }

    protected void addTotal(List<Map<String, Object>> result, Map<String, Object> total) throws Exception
    {
        result.add(total);
    }

    protected String getTotalLabel()
    {
        return "crud.total";
    }

    @Override
    protected Object createTreeView() throws Exception
    {
        PageTreeTableView view = new PageTreeTableView();

        initView(view);

        return view;
    }

    protected abstract void initView(PageTreeTableView view) throws Exception;
}
