package com.gzzm.portal.cms.information;

import com.gzzm.platform.commons.SimpleDao;
import net.cyan.commons.util.*;
import net.cyan.crud.CrudConfig;
import net.cyan.thunwind.PersistenceManager;
import net.cyan.thunwind.map.*;

import java.util.Date;

/**
 * @author camel
 * @date 2014/9/26
 */
public abstract class EntityInformationComponent<EE, PE> extends AbstractInformationComponent
{
    private SimpleDao dao;

    private SimpleDao editDao;

    private SimpleDao publishDao;

    /**
     * 编辑的实体
     */
    private Class<EE> editEntityType;

    /**
     * 发布的实体
     */
    private Class<PE> publishEntityType;

    private String editManagerName;

    private String publishManagerName;

    private PersistenceManager editManager;

    private PersistenceManager publishManager;

    private EE entity;

    public EntityInformationComponent()
    {
    }

    public EE getEntity()
    {
        return entity;
    }

    public void setEntity(EE entity)
    {
        this.entity = entity;
    }

    @SuppressWarnings("unchecked")
    protected Class<EE> getEditEntityType() throws Exception
    {
        if (editEntityType == null)
        {
            editEntityType = BeanUtils.toClass(
                    BeanUtils.getRealType(EntityInformationComponent.class, "EE", getClass()));
        }

        return editEntityType;
    }

    @SuppressWarnings("unchecked")
    protected Class<PE> getPublishEntityType() throws Exception
    {
        if (publishEntityType == null)
        {
            publishEntityType = BeanUtils.toClass(
                    BeanUtils.getRealType(EntityInformationComponent.class, "PE", getClass()));
        }

        return publishEntityType;
    }

    protected String getEditManagerName() throws Exception
    {
        if (editManagerName == null)
            editManagerName = PersistenceManager.getFactory().getManagerName(getEditEntityType());

        return editManagerName;
    }

    protected String getPublishManagerName() throws Exception
    {
        if (publishManagerName == null)
            publishManagerName = PersistenceManager.getFactory().getManagerName(getEditEntityType());

        return publishManagerName;
    }

    protected PersistenceManager getEditManager() throws Exception
    {
        if (editManager == null)
            editManager = PersistenceManager.getManager(getEditManagerName());

        return editManager;
    }

    protected PersistenceManager getPublishManager() throws Exception
    {
        if (publishManager == null)
            publishManager = PersistenceManager.getManager(getPublishManagerName());

        return publishManager;
    }

    protected SimpleDao getDao() throws Exception
    {
        if (dao == null)
            dao = SimpleDao.getInstance();

        return dao;
    }

    protected SimpleDao getEditDao() throws Exception
    {
        String editManagerName = getEditManagerName();

        if (StringUtils.isEmpty(editManagerName))
            return getDao();

        if (editDao == null)
            editDao = SimpleDao.getInstance(editManagerName);
        return editDao;
    }

    protected SimpleDao getPublishDao() throws Exception
    {
        String publishManagerName = getPublishManagerName();
        if (StringUtils.equal(publishManagerName, getEditManagerName()))
        {
            return getEditDao();
        }
        else
        {
            if (publishDao == null)
                publishDao = SimpleDao.getInstance(publishManagerName);

            return publishDao;
        }
    }

    protected Class getEditKeyType() throws Exception
    {
        EntityMap<EE> entityMap = getEditManager().getEntityMap(getEditEntityType());

        ColumnMap columnMap = entityMap.getKeyFields().get(0);

        return columnMap.getType();
    }

    protected Object toEditKey(String linkId) throws Exception
    {
        return DataConvert.convertValue(getEditKeyType(), linkId);
    }

    protected EE createEditEntity() throws Exception
    {
        return getEditEntityType().newInstance();
    }

    protected Object getEditEntityKey(EE entity) throws Exception
    {
        EntityMap<EE> entityMap = getEditManager().getEntityMap(getEditEntityType());

        return entityMap.fetchKey(entity);
    }

    protected void setEditEntityKey(EE entity, Object key) throws Exception
    {
        EntityMap<EE> entityMap = getEditManager().getEntityMap(getEditEntityType());

        ColumnMap columnMap = entityMap.getKeyFields().get(0);

        columnMap.set(entity, key);
    }

    protected Class getPublishKeyType() throws Exception
    {
        EntityMap<PE> entityMap = getPublishManager().getEntityMap(getPublishEntityType());

        ColumnMap columnMap = entityMap.getKeyFields().get(0);

        return columnMap.getType();
    }

    protected Object toPublishKey(String linkId) throws Exception
    {
        return DataConvert.convertValue(getPublishKeyType(), linkId);
    }

    protected PE createPublishEntity() throws Exception
    {
        return getPublishEntityType().newInstance();
    }

    protected Object getPublishEntityKey(PE entity) throws Exception
    {
        EntityMap<PE> entityMap = getPublishManager().getEntityMap(getPublishEntityType());

        return entityMap.fetchKey(entity);
    }

    protected void setPublishEntityKey(PE entity, Object key) throws Exception
    {
        EntityMap<PE> entityMap = getPublishManager().getEntityMap(getPublishEntityType());

        ColumnMap columnMap = entityMap.getKeyFields().get(0);

        columnMap.set(entity, key);
    }

    @Override
    public void init(InformationEdit information, InformationCrud crud) throws Exception
    {
        super.init(information, crud);

        if (entity == null)
            entity = createEditEntity();
        copyInformationToEntity(information, entity, true, crud);
    }

    @Override
    public void load(InformationEdit information, InformationCrud crud) throws Exception
    {
        super.load(information, crud);

        entity = loadEntity(information);
    }

    protected EE loadEntity(InformationEdit information) throws Exception
    {
        return loadEntity(information.getLinkId());
    }

    protected EE loadEntity(String linkId) throws Exception
    {
        EE entity;
        if (StringUtils.isEmpty(linkId))
        {
            entity = createEditEntity();
        }
        else
        {
            Object key = toEditKey(linkId);
            entity = getEditDao().load(getEditEntityType(), key);

            if (entity == null)
            {
                entity = createEditEntity();

                setEditEntityKey(entity, key);
            }
        }

        return entity;
    }

    @SuppressWarnings("UnusedDeclaration")
    protected void copyEntityToInformation(EE entity, InformationEdit information, boolean isNew, InformationCrud crud)
            throws Exception
    {
        String title = getTitle(entity);

        if (title != null)
        {
            information.setTitle(title);
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    protected void copyInformationToEntity(InformationEdit information, EE entity, boolean isNew, InformationCrud crud)
            throws Exception
    {
        if (information.getInformationId() != null)
            setInformationId(entity, information.getInformationId());
        if (information.getDeptId() != null)
            setDeptId(entity, information.getDeptId());
        if (information.getTitle() != null)
            setTitle(entity, information.getTitle());
    }

    protected void copyEntity(EE editEntity, PE publishEntity) throws Exception
    {
        if (publishEntity != null)
        {
            EntityMap<EE> editEntityMap = getEditManager().getEntityMap(getEditEntityType());
            EntityMap<PE> publishEntityMap = getPublishManager().getEntityMap(getPublishEntityType());

            for (ColumnMap columnMap : editEntityMap.getMembers(ColumnMap.class))
            {
                MemberMap member = publishEntityMap.getMember(columnMap.getField());

                if (member != null && (member instanceof ColumnMap))
                {
                    ColumnMap columnMap1 = (ColumnMap) member;
                    if (columnMap1.isKey())
                    {
                        if (StringUtils.isEmpty(columnMap1.getId()) && columnMap1.get(publishEntity) == null)
                        {
                            columnMap1.set(publishEntity, columnMap.get(editEntity));
                        }
                    }
                    else
                    {
                        columnMap1.set(publishEntity, columnMap.get(editEntity));
                    }
                }
            }
        }
    }

    @Override
    public void clone(InformationEdit information, InformationEdit sourceInformation, InformationCrud crud)
            throws Exception
    {
        entity = clone(loadEntity(sourceInformation));
        setInformationId(entity, null);
    }

    public EE clone(EE entity) throws Exception
    {
        return CrudConfig.getPersistenceDialect().clone(entity);
    }

    @SuppressWarnings("UnusedDeclaration")
    protected void initPublishEntity(PE publishEntity, Information information, boolean first) throws Exception
    {
        setPublishTime(publishEntity, information.getPublishTime());
        setUpdateTime(publishEntity, information.getUpdateTime());
    }

    protected void setInformationId(EE entity, Long informationId) throws Exception
    {
        PropertyInfo property = null;
        try
        {
            property = BeanUtils.getProperty(getEditEntityType(), "informationId");
        }
        catch (Exception ex)
        {
            //没有这个属性，跳过
        }

        if (property != null)
            property.setObject(entity, informationId);
    }

    protected void setDeptId(EE entity, Integer deptId) throws Exception
    {
        PropertyInfo property = null;
        try
        {
            property = BeanUtils.getProperty(getEditEntityType(), "deptId");
        }
        catch (Exception ex)
        {
            //没有这个属性，跳过
        }

        if (property != null)
            property.set(entity, deptId);
    }

    @SuppressWarnings("UnusedDeclaration")
    protected void setTitle(EE entity, String title) throws Exception
    {
    }

    @SuppressWarnings("UnusedDeclaration")
    protected String getTitle(EE entity) throws Exception
    {
        return null;
    }

    @SuppressWarnings("UnusedDeclaration")
    protected void setPublishTime(PE entity, Date publishTime) throws Exception
    {
    }

    @SuppressWarnings("UnusedDeclaration")
    protected void setUpdateTime(PE entity, Date updateTime) throws Exception
    {
    }

    @Override
    public boolean beforeSave(InformationEdit information, boolean isNew, InformationCrud crud) throws Exception
    {
        super.beforeSave(information, isNew, crud);

        copyEntityToInformation(entity, information, isNew, crud);

        return true;
    }

    @Override
    public void afterSave(InformationEdit information, boolean isNew, InformationCrud crud) throws Exception
    {
        super.afterSave(information, isNew, crud);

        String linkId = information.getLinkId();
        if (!StringUtils.isEmpty(linkId))
        {
            setEditEntityKey(entity, toEditKey(linkId));
        }
        else
        {
            setEditEntityKey(entity, null);
        }

        copyInformationToEntity(information, entity, isNew, crud);

        getEditDao().save(entity);

        if (StringUtils.isEmpty(linkId))
        {
            information.setLinkId(getEditEntityKey(entity).toString());
            getDao().update(information);
        }
    }

    @Override
    public void afterPublish(InformationEdit informationEdit, Information information, boolean first) throws Exception
    {
        super.afterPublish(informationEdit, information, first);

        PE publishEntity;

        String publishLinkId = information.getLinkId();
        if (StringUtils.isEmpty(publishLinkId))
        {
            publishEntity = createPublishEntity();
        }
        else
        {
            Object key = toPublishKey(publishLinkId);
            publishEntity = getPublishDao().load(getPublishEntityType(), key);

            if (publishEntity == null)
            {
                publishEntity = createPublishEntity();

                setPublishEntityKey(publishEntity, key);
            }
        }

        EE editEntity = loadEntity(informationEdit);
        if (editEntity != null)
            copyEntity(editEntity, publishEntity);

        initPublishEntity(publishEntity, information, first);

        getPublishDao().save(publishEntity);

        if (StringUtils.isEmpty(publishLinkId))
        {
            information.setLinkId(getPublishEntityKey(publishEntity).toString());
            getDao().update(information);
        }
    }
}
