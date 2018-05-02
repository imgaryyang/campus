package com.gzzm.platform.commons.crud;

import com.gzzm.platform.annotation.AuthDeptIds;
import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.Value;
import net.cyan.crud.TreeCrud;
import net.cyan.crud.annotation.NotCondition;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 基于部门树的树型结构
 *
 * @author camel
 * @date 12-7-4
 */
@Service
public abstract class DeptBaseTree<I extends DeptBaseTree.Item> extends TreeCrud<I, String> implements DeptOwnedCrud
{
    public static abstract class Item implements Value
    {
        private static final long serialVersionUID = 5230927104560704297L;

        private String itemId;

        private String itemName;

        public Item()
        {
        }

        public String getItemId()
        {
            return itemId;
        }

        public void setItemId(String itemId)
        {
            this.itemId = itemId;
        }

        public String getItemName()
        {
            return itemName;
        }

        public void setItemName(String itemName)
        {
            this.itemName = itemName;
        }

        public Object valueOf()
        {
            return itemId;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o)
                return true;

            if (!(o instanceof Item))
                return false;

            Item item = (Item) o;

            return itemId.equals(item.itemId);
        }

        @Override
        public int hashCode()
        {
            return itemId.hashCode();
        }

        @Override
        public String toString()
        {
            return itemName;
        }
    }

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

    public DeptBaseTree()
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

    public String getKey(I item) throws Exception
    {
        return item.getItemId();
    }

    @Override
    public I getRoot() throws Exception
    {
        I item = getNodeType().newInstance();

        Integer rootId = getRootKey();

        if (rootId == -1)
        {
            item.setItemId("#");
        }
        else
        {
            item.setItemId("d" + rootId);
        }

        return item;
    }

    protected Integer getRootKey() throws Exception
    {
        Collection<Integer> authDeptIds = getAuthDeptIds();
        return authDeptIds != null && authDeptIds.size() == 1 ? authDeptIds.iterator().next() : -1;
    }

    @Override
    public boolean hasChildren(I node) throws Exception
    {
        String key = node.getItemId();

        if ("#".equals(key))
            return true;

        if (key.startsWith("d"))
        {
            Integer deptId = Integer.valueOf(key.substring(1));

            return service.containsSubAuthDept(deptId, null, getQueryDeptIds()) || hasOtherChildren(node);
        }
        else
        {
            return hasOtherChildren(node);
        }
    }

    protected abstract boolean hasOtherChildren(I node) throws Exception;

    @Override
    public I getNode(String key) throws Exception
    {
        I item = getNodeType().newInstance();

        item.setItemId(key);

        return item;
    }

    @Override
    public List<I> getChildren(String parent) throws Exception
    {
        if ("#".equals(parent) || parent.startsWith("d"))
        {
            Integer deptId = "#".equals(parent) ? null : Integer.valueOf(parent.substring(1));

            List<SimpleDeptInfo> depts = service.getAuthDeptTree(deptId, null, getQueryDeptIds());
            List<Integer> deptIds = new ArrayList<Integer>(depts.size());

            for (SimpleDeptInfo dept : depts)
                deptIds.add(dept.getDeptId());

            List<I> items = loadDeptItems(deptIds);

            List<I> otherItems = loadOtherItems(parent);

            if (otherItems != null && otherItems.size() > 0)
                items.addAll(otherItems);

            return items;
        }
        else
        {
            return loadOtherItems(parent);
        }
    }

    public abstract List<I> loadDeptItems(List<Integer> deptIds) throws Exception;

    public abstract List<I> loadOtherItems(String parent) throws Exception;

    @Override
    protected Object createTreeView() throws Exception
    {
        PageTreeTableView view = new PageTreeTableView();

        initView(view);

        return view;
    }

    protected abstract void initView(PageTreeTableView view) throws Exception;

    @Override
    public String showTree() throws Exception
    {
        return SystemCrudUtils.treeForward(super.showTree(), this);
    }

    @Override
    public void setString(I node, String s) throws Exception
    {
        node.setItemName(s);
    }
}
