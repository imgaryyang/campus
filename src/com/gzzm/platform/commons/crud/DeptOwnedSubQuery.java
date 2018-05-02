package com.gzzm.platform.commons.crud;

import com.gzzm.platform.annotation.*;
import com.gzzm.platform.commons.*;
import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.*;
import net.cyan.crud.CrudConfig;
import net.cyan.crud.annotation.NotCondition;
import net.cyan.crud.util.CrudBeanUtils;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 部门所拥有数据的子表查询，这些表没有直接关联部门，而是关联一个父表，父表关联部门
 *
 * @author camel
 * @date 13-11-12
 */
@Service
public abstract class DeptOwnedSubQuery<E, K> extends BaseQueryCrud<E, K> implements DeptOwnedEntityBaseCrud<E, K>
{
    @Inject
    protected UserOnlineInfo userOnlineInfo;

    @AuthDeptIds
    @NotSerialized
    protected Collection<Integer> authDeptIds;

    @MenuId
    protected String menuId;

    private String ownerField;

    private String ownerKeyField;

    /**
     * 最顶层对象的class（即Dept的下一级对象）
     */
    private Class<?> topOwnerClass;

    /**
     * 倒数第二层对象的class
     */
    private Class<?> ownerClass;

    private Object ownerKey;

    private String userIdField;

    public DeptOwnedSubQuery()
    {
    }

    public Collection<Integer> getAuthDeptIds()
    {
        return authDeptIds;
    }

    protected abstract String getTopOwnerField();

    protected Class<?> getTopOwnerClass() throws Exception
    {
        if (topOwnerClass == null)
        {
            topOwnerClass = CrudBeanUtils.getType(getTopOwnerField(), getEntityType());
        }

        return topOwnerClass;
    }

    protected String getOwnerField()
    {
        if (ownerField == null)
        {
            String topOwnerField = getTopOwnerField();
            int index = topOwnerField.indexOf('.');

            if (index > 0)
            {
                ownerField = topOwnerField.substring(0, index);
            }
            else
            {
                ownerField = topOwnerField;
            }
        }

        return ownerField;
    }

    @SuppressWarnings("StringEquality")
    protected Class<?> getOwnerClass() throws Exception
    {
        if (ownerClass == null)
        {
            String ownerField = getOwnerField();
            if (ownerField == getTopOwnerField())
            {
                ownerClass = getTopOwnerClass();
            }
            else
            {
                PropertyInfo property = BeanUtils.getProperty(getEntityType(), ownerField);

                ownerClass = property.getType();
            }
        }

        return ownerClass;
    }

    protected String getOwnerKeyField()
    {
        if (ownerKeyField == null)
        {
            try
            {
                ownerKeyField = CrudConfig.getPersistenceDialect().getKeyFields(getOwnerClass()).get(0);
            }
            catch (Exception ex)
            {
                ExceptionUtils.wrapException(ex);
            }
        }

        return ownerKeyField;
    }

    protected Object getOwnerKey() throws Exception
    {
        if (ownerKey == null)
            ownerKey = BeanUtils.getValue(this, getOwnerKeyField());

        return ownerKey;
    }

    protected Object getOwnerKey(E entity) throws Exception
    {
        if (ownerKey == null)
            ownerKey = BeanUtils.getValue(entity, getOwnerKeyField());

        return ownerKey;
    }

    protected void setOwnerKey(E entity, Object ownerKey) throws Exception
    {
        BeanUtils.setValue(entity, getOwnerKeyField(), ownerKey);
    }

    protected String getUserIdField() throws Exception
    {
        if (userIdField == null)
            userIdField = DeptOwnedCrudUtils.getUserIdField(getTopOwnerClass());

        return userIdField;
    }

    public List<Integer> getDeptIds(Collection<K> keys) throws Exception
    {
        return getTopOwnerFieldByKeys("deptId", keys);
    }

    public List<Integer> getUserIds(Collection<K> keys) throws Exception
    {
        String userIdField = getUserIdField();

        if (userIdField.length() > 0)
        {
            return getTopOwnerFieldByKeys(userIdField, keys);
        }

        return null;
    }

    @Override
    public boolean checkDeptId(E entity, Collection<Integer> authDeptIds) throws Exception
    {
        return DeptOwnedCrudUtils.checkDept(this, entity, authDeptIds);
    }

    private List<Integer> getTopOwnerFieldByKeys(String field, Collection<K> keys) throws Exception
    {
        String keyField = CrudConfig.getPersistenceDialect().getKeyFields(getEntityType()).get(0);
        return getCrudService().oqlQuery("select " + getTopOwnerField() + "." + field + " from " +
                getEntityName() + " where " + keyField + " in :", keys, Integer.class);
    }

    @NotSerialized
    public Integer getDeptId() throws Exception
    {
        return getDeptIdByOwnerKey(getOwnerKey());
    }

    public Integer getDeptId(E entity) throws Exception
    {
        return getDeptIdByOwnerKey(getOwnerKey(entity));
    }

    @NotSerialized
    @NotCondition
    public Integer getUserId() throws Exception
    {
        return getUserIdByOwnerKey(getOwnerKey());
    }

    public Integer getUserId(E entity) throws Exception
    {
        return getUserIdByOwnerKey(getOwnerKey(entity));
    }

    protected Integer getDeptIdByOwnerKey(Object ownerKey) throws Exception
    {
        return getTopOwnerFieldByOwnerKey("deptId", ownerKey);
    }

    protected Integer getUserIdByOwnerKey(Object ownerKey) throws Exception
    {
        String userIdField = getUserIdField();
        if (userIdField.length() > 0)
            return getTopOwnerFieldByOwnerKey(userIdField, ownerKey);

        return null;
    }

    private Integer getTopOwnerFieldByOwnerKey(String field, Object ownerKey) throws Exception
    {
        if (ownerKey == null)
            return null;

        StringBuilder buffer = new StringBuilder("select ");

        String topOwnerField = getTopOwnerField();

        int index = topOwnerField.indexOf('.');
        if (index > 0)
            buffer.append(topOwnerField.substring(index + 1)).append(".");

        buffer.append(field).append(" from ").append(CrudBeanUtils.getClassWord(getOwnerClass())).append(" where ")
                .append(getOwnerKeyField()).append("=:");

        return getCrudService().oqlQueryFirst(buffer.toString(), Integer.class, ownerKey);
    }

    protected void checkOwnerKey(Object ownerKey) throws Exception
    {
        if (userOnlineInfo == null)
            throw new LoginExpireException();

        if (!userOnlineInfo.isAdmin())
        {
            if (authDeptIds != null)
            {
                //判断父对象所属的部门是否在权限范围内
                Integer deptId = getDeptIdByOwnerKey(ownerKey);
                if (!authDeptIds.contains(deptId))
                {
                    throw new SystemMessageException(Messages.NO_AUTH_RECORD, "no auth," + getEntityType().getName() +
                            ",deptId:" + deptId + "," + getOwnerKeyField() + ":" + ownerKey);
                }
            }

            if (userOnlineInfo.isSelf(menuId))
            {
                Integer userId = getUserIdByOwnerKey(ownerKey);

                if (!userOnlineInfo.getUserId().equals(userId))
                {
                    //判断父对象是否此用户拥有
                    throw new SystemMessageException(Messages.NO_AUTH_RECORD, "no auth," + getEntityType().getName() +
                            ",userId:" + userId + "," + getOwnerKeyField() + ":" + ownerKey);
                }
            }
        }
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        checkOwnerKey(getOwnerKey());

        super.beforeShowList();
    }

    @Override
    protected void beforeQuery() throws Exception
    {
        super.beforeQuery();

        checkOwnerKey(getOwnerKey());
    }

    @Override
    public void afterLoad() throws Exception
    {
        super.afterLoad();

        checkOwnerKey(getOwnerKey(getEntity()));
    }
}
