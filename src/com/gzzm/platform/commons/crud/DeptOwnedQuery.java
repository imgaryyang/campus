package com.gzzm.platform.commons.crud;

import com.gzzm.platform.annotation.*;
import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.platform.organ.AuthDeptTreeModel;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.*;
import net.cyan.crud.annotation.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 部门所拥有的数据的查询
 *
 * @author camel
 * @date 2009-10-10
 */
@Service
public abstract class DeptOwnedQuery<E, K> extends BaseQueryCrud<E, K> implements DeptOwnedEntityCrud<E, K>
{
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
    @In("deptId")
    @NotSerialized
    private Collection<Integer> queryDeptIds;

    private AuthDeptTreeModel deptTree;

    @NotSerialized
    private String userIdField;

    private Integer queryUserId;

    @Inject
    protected UserOnlineInfo userOnlineInfo;

    @MenuId
    protected String menuId;

    public DeptOwnedQuery()
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

    public Integer getQueryUserId()
    {
        return queryUserId;
    }

    public void setQueryUserId(Integer queryUserId)
    {
        this.queryUserId = queryUserId;
    }

    public Integer getDeptId(E entity) throws Exception
    {
        return (Integer) BeanUtils.getValue(entity, "deptId");
    }

    public void setDeptId(E entity, Integer deptId) throws Exception
    {
        BeanUtils.setValue(entity, "deptId", deptId);
    }

    protected String getUserIdField() throws Exception
    {
        if (userIdField == null)
            userIdField = DeptOwnedCrudUtils.getUserIdField(this);

        return userIdField;
    }

    public Integer getUserId(E entity) throws Exception
    {
        String field = getUserIdField();

        if (field.length() > 0)
            return (Integer) BeanUtils.getValue(entity, field);

        return null;
    }

    @Override
    public String createCondition() throws Exception
    {
        String s = super.createCondition();
        if (userOnlineInfo.isSelf(menuId))
        {
            String userIdField = getUserIdField();
            if (userIdField.length() > 0)
            {
                if (StringUtils.isEmpty(s))
                    return userIdField + "=:queryUserId";
                else
                    return s + " and (" + userIdField + "=:queryUserId)";
            }
        }

        return s;
    }

    public void setUserId(E entity, Integer userId) throws Exception
    {
        String field = getUserIdField();

        if (field.length() > 0)
            BeanUtils.setValue(entity, field, userId);
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();

        DeptOwnedCrudUtils.beforeQuery(this);
    }

    @Override
    protected void beforeQuery() throws Exception
    {
        super.beforeQuery();

        DeptOwnedCrudUtils.beforeQuery(this);
    }

    @Override
    public void afterLoad() throws Exception
    {
        super.afterLoad();

        DeptOwnedCrudUtils.afterLoad(this);
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

    public List<Integer> getDeptIds(Collection<K> keys) throws Exception
    {
        return getCrudService().getFieldByKeys(getEntityType(), "deptId", keys, Integer.class);
    }

    public List<Integer> getUserIds(Collection<K> keys) throws Exception
    {
        String userIdField = getUserIdField();
        if (userIdField.length() > 0)
            return getCrudService().getFieldByKeys(getEntityType(), userIdField, keys, Integer.class);

        return null;
    }

    @Override
    public boolean checkDeptId(E entity, Collection<Integer> authDeptIds) throws Exception
    {
        return DeptOwnedCrudUtils.checkDept(this, entity, authDeptIds);
    }

    public String getOwnerField()
    {
        return "deptId";
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
}