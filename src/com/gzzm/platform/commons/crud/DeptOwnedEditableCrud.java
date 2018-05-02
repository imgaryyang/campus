package com.gzzm.platform.commons.crud;

import com.gzzm.platform.annotation.*;
import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.*;
import net.cyan.crud.annotation.*;
import net.cyan.crud.importers.ColumnDataLoader;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * @author camel
 * @date 2010-3-5
 */
@Service
public abstract class DeptOwnedEditableCrud<E, K> extends BaseNormalCrud<E, K> implements DeptOwnedEntityCrud<E, K>
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

    /**
     * 单选的部门树
     */
    private AuthDeptTreeModel deptTree;

    /**
     * 多选的部门树
     */
    private AuthDeptTreeModel multipleDeptTree;

    private String userIdField;

    private String selfField;

    private Integer queryUserId;

    @Inject
    protected UserOnlineInfo userOnlineInfo;

    @MenuId
    protected String menuId;

    public DeptOwnedEditableCrud()
    {
        setLog(true);
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

    public Collection<Integer> getTopDeptIds()
    {
        return topDeptIds;
    }

    public void setTopDeptIds(Collection<Integer> topDeptIds)
    {
        this.topDeptIds = topDeptIds;
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

    protected String getSelfField() throws Exception
    {
        if (selfField == null && getUserIdField().length() > 0)
        {
            Class<E> entityClass = getEntityType();

            try
            {
                PropertyInfo property = BeanUtils.getProperty(entityClass, "self");
                if (property.getType() == Boolean.class && property.isWritable())
                {
                    selfField = "self";
                }
            }
            catch (Throwable ex)
            {
                //没有此属性，继续下
            }

            if (selfField == null)
                selfField = "";
        }

        return selfField;
    }

    public Integer getUserId(E entity) throws Exception
    {
        String field = getUserIdField();

        if (field.length() > 0)
            return (Integer) BeanUtils.getValue(entity, field);

        return null;
    }

    protected void setUserId(E entity, Integer userId) throws Exception
    {
        String field = getUserIdField();

        if (field.length() > 0)
            BeanUtils.setValue(entity, field, userId);
    }

    public boolean isSelf(E entity) throws Exception
    {
        String selfField = getSelfField();

        if (selfField.length() > 0)
            return (Boolean) BeanUtils.getValue(entity, selfField);

        return false;
    }

    protected void setSelf(E entity, boolean self) throws Exception
    {
        String selfField = getSelfField();

        if (selfField.length() > 0)
            BeanUtils.setValue(entity, selfField, self);
    }

    @Override
    public String duplicate(K key, String forward) throws Exception
    {
        forward = super.duplicate(key, forward);

        if (getDeptId() != null)
            setDeptId(getEntity(), getDeptId());

        return forward;
    }

    protected boolean isSelf()
    {
        return userOnlineInfo.isSelf(menuId);
    }

    @Override
    public String createCondition() throws Exception
    {
        String s = super.createCondition();
        String userIdField = getUserIdField();
        if (userIdField.length() > 0)
        {
            if (isSelf())
            {
                if (StringUtils.isEmpty(s))
                    return userIdField + "=:queryUserId";
                else
                    return "(" + s + ") and " + userIdField + "=:queryUserId";
            }
            else
            {
                String selfField = getSelfField();
                if (selfField.length() > 0)
                {
                    if (StringUtils.isEmpty(s))
                        return selfField + "=0 or " + selfField + " is null";
                    else
                        return "(" + s + ") and (" + selfField + "=0 or " + selfField + " is null)";
                }
            }
        }

        return s;
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();

        DeptOwnedCrudUtils.beforeQuery(this);
    }

    @Override
    public void initEntity(E entity) throws Exception
    {
        super.initEntity(entity);

        if (getDeptId(entity) == null)
        {
            Integer deptId = getDeptId();
            if (!Null.isNull(deptId))
                setDeptId(entity, deptId);
        }

        if (getUserIdField().length() > 0)
        {
            if (getUserId(entity) == null)
            {
                setUserId(entity, userOnlineInfo.getUserId());
            }

            setSelf(entity, isSelf());
        }
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

    protected Integer getDefaultDeptId() throws Exception
    {
        return DeptOwnedCrudUtils.getDefaultDeptId(this);
    }

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

    @Select(field = {"deptId", "entity.deptId"})
    public AuthDeptTreeModel getDeptTree()
    {
        if (deptTree == null)
            deptTree = new AuthDeptTreeModel();

        return deptTree;
    }

    @Select(field = {"deptIds", "topDeptIds"})
    public AuthDeptTreeModel getMultipleDeptTree()
    {
        if (multipleDeptTree == null)
        {
            multipleDeptTree = new AuthDeptTreeModel();
            multipleDeptTree.setShowBox(true);
        }

        return multipleDeptTree;
    }

    protected ColumnDataLoader getDeptIdLoader() throws Exception
    {
        return DeptOwnedCrudUtils.getDeptIdLoader();
    }
}
