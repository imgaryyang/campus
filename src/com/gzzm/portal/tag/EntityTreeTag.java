package com.gzzm.portal.tag;

import com.gzzm.platform.commons.CommonDao;
import net.cyan.commons.util.*;
import net.cyan.crud.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * @author camel
 * @date 12-9-5
 */
public abstract class EntityTreeTag<E> extends TreeTag<E>
{
    @Inject
    private static Provider<CommonDao> commonDaoProvider;

    private CommonDao commonDao;

    private Class<E> entityType;

    private PropertyInfo childrenPropertyInfo;

    public EntityTreeTag()
    {
    }

    protected CommonDao getCommonDao() throws Exception
    {
        if (commonDao == null)
            commonDao = commonDaoProvider.get();

        return commonDao;
    }

    protected abstract Object getRootKey(Map<String, Object> context);

    @Override
    protected E getRoot(Map<String, Object> context) throws Exception
    {
        return getCommonDao().get(getEntityType(), getRootKey(context));
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Collection<E> getChildren(E parent, Map<String, Object> context) throws Exception
    {
        return (Collection<E>) getChildrenPropertyInfo().get(parent);
    }

    @SuppressWarnings("unchecked")
    protected Class<E> getEntityType() throws Exception
    {
        if (entityType == null)
            entityType = BeanUtils.toClass(BeanUtils.getRealType(EntityTreeTag.class, "E", this.getClass()));

        return entityType;
    }

    public PropertyInfo getChildrenPropertyInfo() throws Exception
    {
        if (childrenPropertyInfo == null)
        {
            Class<E> entityType = getEntityType();
            childrenPropertyInfo = BeanUtils
                    .getProperty(entityType, CrudConfig.getPersistenceDialect().getChildrenField(entityType));
        }

        return childrenPropertyInfo;
    }
}
