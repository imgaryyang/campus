package com.gzzm.platform.commons.crud;

import com.gzzm.platform.annotation.AuthDeptIds;
import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.BeanUtils;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 部门相关的树修改
 *
 * @author camel
 * @date 12-3-29
 */
@Service
public abstract class DeptOwnedTreeCrud<E, K> extends BaseTreeCrud<E, K> implements DeptOwnedEntityCrud<E, K>
{
    private static final String[] ORDERWITHFIELDS = {"deptId"};

    @Inject
    protected DeptService deptService;

    /**
     * 用部门id作查询条件
     */
    private Integer deptId;

    private DeptInfo dept;

    /**
     * 拥有权限的部门列表，通过setAuthDeptIds注入，以方便子类覆盖注入方式
     */
    @NotSerialized
    private Collection<Integer> authDeptIds;

    private AuthDeptTreeModel deptTree;

    public DeptOwnedTreeCrud()
    {
    }

    public String getOrderField()
    {
        //默认认为此类对象的crud维护支持排序，并约定排序字段为orderId
        return "orderId";
    }

    protected String[] getOrderWithFields()
    {
        return ORDERWITHFIELDS;
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

    protected DeptInfo getDept() throws Exception
    {
        if (dept == null)
        {
            if (deptId != null)
                dept = deptService.getDept(deptId);
        }

        return dept;
    }

    protected String getDeptName() throws Exception
    {
        DeptInfo dept = getDept();
        if (dept != null)
            return dept.getDeptName();

        return null;
    }

    public Integer getDeptId(E entity) throws Exception
    {
        return (Integer) BeanUtils.getValue(entity, "deptId");
    }

    public void setDeptId(E entity, Integer deptId) throws Exception
    {
        BeanUtils.setValue(entity, "deptId", deptId);
    }

    public Integer getUserId(E entity) throws Exception
    {
        return null;
    }

    public Integer getQueryUserId()
    {
        return null;
    }

    public void setQueryUserId(Integer queryUserId)
    {
    }

    public List<Integer> getDeptIds()
    {
        return null;
    }

    public Collection<Integer> getTopDeptIds()
    {
        return null;
    }

    public Collection<Integer> getQueryDeptIds()
    {
        return null;
    }

    public void setQueryDeptIds(Collection<Integer> queryDeptIds)
    {
    }

    protected Integer getDefaultDeptId() throws Exception
    {
        return DeptOwnedCrudUtils.getDefaultDeptId(this);
    }

    protected void setDefaultDeptId() throws Exception
    {
        if (getDeptId() == null)
            setDeptId(DeptOwnedCrudUtils.getDefaultDeptId(this));
    }

    @Override
    protected void beforeShowTree() throws Exception
    {
        super.beforeShowTree();

        DeptOwnedCrudUtils.beforeQuery(this);
        setDefaultDeptId();
    }

    @Override
    public void initEntity(E entity) throws Exception
    {
        super.initEntity(entity);

        if (getDeptId(entity) == null)
        {
            Integer deptId = getDeptId();
            setDeptId(entity, deptId);
        }
    }

    @Override
    public void beforeLoad(K key) throws Exception
    {
        super.beforeLoad(key);

        DeptOwnedCrudUtils.beforeQuery(this);
    }

    @Override
    public void afterLoad() throws Exception
    {
        super.afterLoad();

        DeptOwnedCrudUtils.afterLoad(this);
    }

    @Override
    public boolean beforeInsert() throws Exception
    {
        super.beforeInsert();

        DeptOwnedCrudUtils.beforeInsert(this);
        return true;
    }

    @Override
    public boolean beforeUpdate() throws Exception
    {
        super.beforeUpdate();

        DeptOwnedCrudUtils.beforeUpdate(this);
        return true;
    }

    @Override
    public boolean beforeDelete(K key) throws Exception
    {
        super.beforeDelete(key);

        DeptOwnedCrudUtils.beforeDelete(Collections.singletonList(key), this);

        return true;
    }

    @Override
    public boolean beforeDeleteAll() throws Exception
    {
        super.beforeDeleteAll();

        DeptOwnedCrudUtils.beforeDelete(Arrays.asList(getKeys()), this);

        return true;
    }

    public List<Integer> getDeptIds(Collection<K> keys) throws Exception
    {
        return getCrudService().getFieldByKeys(getEntityType(), "deptId", keys, Integer.class);
    }

    public List<Integer> getUserIds(Collection<K> keys) throws Exception
    {
        return null;
    }

    @Override
    public boolean checkDeptId(E entity, Collection<Integer> authDeptIds) throws Exception
    {
        return DeptOwnedCrudUtils.checkDept(this, entity, authDeptIds);
    }

    @Override
    protected Object createTreeView() throws Exception
    {
        PageTreeView view = new PageTreeView();

        view.defaultInit();

        return view;
    }

    @Select(field = "deptId")
    public AuthDeptTreeModel getDeptTree()
    {
        if (deptTree == null)
        {
            deptTree = new AuthDeptTreeModel();
        }

        return deptTree;
    }
}
