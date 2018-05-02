package com.gzzm.platform.flow;

import com.gzzm.platform.commons.SimpleDao;
import net.cyan.arachne.RequestContext;
import net.cyan.commons.util.*;
import net.cyan.thunwind.PersistenceManager;
import net.cyan.thunwind.map.*;

/**
 * @author camel
 * @date 2014/9/18
 */
public abstract class EntityFlowComponent<C extends FlowComponentContext, E> extends AbstractFlowComponent<C>
{
    private SimpleDao dao;

    private Class<E> entityType;

    private E entity;

    private String managerName;

    private PersistenceManager manager;

    @SuppressWarnings("unchecked")
    public EntityFlowComponent()
    {
        try
        {
            entityType = BeanUtils.toClass(BeanUtils.getRealType(EntityFlowComponent.class, "E", getClass()));
        }
        catch (Exception ex)
        {
            ExceptionUtils.wrapException(ex);
        }
    }

    public EntityFlowComponent(Class<E> entityType)
    {
        this.entityType = entityType;
    }

    protected Class<E> getEntityType()
    {
        return entityType;
    }

    public E getEntity()
    {
        return entity;
    }

    public void setEntity(E entity)
    {
        this.entity = entity;
    }

    protected abstract String getLinkId(C context) throws Exception;

    protected abstract void setLinkId(String linkId, C context) throws Exception;

    protected String getManagerName()
    {
        if (managerName == null)
            managerName = PersistenceManager.getFactory().getManagerName(entityType);

        return managerName;
    }

    protected PersistenceManager getManager() throws Exception
    {
        if (manager == null)
            manager = PersistenceManager.getManager(getManagerName());

        return manager;
    }

    protected SimpleDao getDao() throws Exception
    {
        if (dao == null)
            dao = SimpleDao.getInstance(getManagerName());

        return dao;
    }

    protected E loadEntity(C context) throws Exception
    {
        String linkId = getLinkId(context);

        E entity;

        if (!StringUtils.isEmpty(linkId))
        {
            Object key = toKey(linkId);

            SimpleDao dao = getDao();

            entity = dao.load(entityType, key);

            if (entity == null)
            {
                entity = createEntity();

                setKey(entity, key);
            }
        }
        else
        {
            entity = createEntity();
        }

        return entity;
    }

    protected E createEntity() throws Exception
    {
        return getEntityType().newInstance();
    }

    protected Object getKey(E entity) throws Exception
    {
        EntityMap<E> entityMap = getManager().getEntityMap(entityType);

        return entityMap.fetchKey(entity);
    }

    protected void setKey(E entity, Object key) throws Exception
    {
        EntityMap<E> entityMap = getManager().getEntityMap(entityType);

        ColumnMap columnMap = entityMap.getKeyFields().get(0);

        columnMap.set(entity, key);
    }

    protected Class getKeyType() throws Exception
    {
        EntityMap<E> entityMap = getManager().getEntityMap(entityType);

        ColumnMap columnMap = entityMap.getKeyFields().get(0);

        return columnMap.getType();
    }

    protected Object toKey(String linkId) throws Exception
    {
        return DataConvert.convertValue(getKeyType(), linkId);
    }

    @Override
    public void beforeShow(C context) throws Exception
    {
        super.beforeShow(context);

        entity = loadEntity(context);

        context.getFlowPage().setData(entity);
    }

    @Override
    public void extractData(C context) throws Exception
    {
        super.extractData(context);

        entity = createEntity();

        RequestContext.getContext().fillForm(entity, "data");

        context.getFlowPage().setData(entity);
    }

    @Override
    public void saveData(C context) throws Exception
    {
        super.saveData(context);

        String linkId = getLinkId(context);
        if (!StringUtils.isEmpty(linkId))
        {
            Object key = toKey(linkId);
            setKey(entity, key);
        }

        SimpleDao dao = getDao();
        dao.save(entity);

        if (StringUtils.isEmpty(linkId))
        {
            linkId = getKey(entity).toString();
            setLinkId(linkId, context);
        }
    }
}
