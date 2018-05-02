package com.gzzm.platform.commons.commonfile;

import com.gzzm.platform.attachment.Attachment;
import com.gzzm.platform.commons.Tools;
import net.cyan.commons.util.*;
import net.cyan.datasyn.*;
import net.cyan.thunwind.PersistenceManager;
import net.cyan.thunwind.map.*;
import net.cyan.thunwind.storage.*;

import java.util.*;

/**
 * @author camel
 * @date 2017/5/22
 */
public class CommonFileSynchronizedRecordListener implements SynchronizedRecordListener
{
    private Map<String, EntityMap<?>> entityMaps;

    public CommonFileSynchronizedRecordListener()
    {
    }

    private synchronized Map<String, EntityMap<?>> getEntityMaps() throws Exception
    {
        if (entityMaps == null)
        {
            entityMaps = new HashMap<String, EntityMap<?>>();
            for (EntityMap<?> entityMap : PersistenceManager.getManager("").getEntityMaps())
            {
                for (ColumnMap columnMap : entityMap.getMembers(ColumnMap.class))
                {
                    if (columnMap.getStorage() instanceof CommonFileColumnStorage)
                    {
                        entityMaps.put(entityMap.getTable(), entityMap);
                    }
                }
            }
        }

        return entityMaps;
    }

    @Override
    public boolean beforeInsert(SynchronizedInfo table, Map<String, Object> keys, Map<String, Object> values)
            throws Exception
    {
        EntityMap<?> entityMap = getEntityMaps().get(table.getTable());
        if (entityMap != null)
        {
            Object entity = createEntity(entityMap, keys, values);
            for (ColumnMap columnMap : entityMap.getMembers(ColumnMap.class))
            {
                ColumnStorage storage = columnMap.getStorage();
                if (storage instanceof CommonFileColumnStorage)
                {
                    String column = columnMap.getColumn();
                    if (entity instanceof Attachment)
                        column = "INPUTABLE";

                    Object value = values.get(column);
                    if (value != null && !Null.isNull(value))
                    {
                        if (storage.save(value, entity, columnMap, entityMap, null, null))
                            values.remove(column);

                        String pathColumn = ((CommonFileColumnStorage) storage).getPathColumn();
                        ColumnMap pathColumnMap = (ColumnMap) entityMap.getMember(pathColumn);
                        String pathColumn1 = pathColumnMap.getColumn();
                        values.put(pathColumn1, pathColumnMap.get(entity));
                    }
                }
            }
        }

        return true;
    }

    @Override
    public void afterInsert(SynchronizedInfo table, Map<String, Object> keys, Map<String, Object> values)
            throws Exception
    {
    }

    @Override
    public boolean beforeUpdate(SynchronizedInfo table, Map<String, Object> keys, Map<String, Object> values)
            throws Exception
    {
        return beforeInsert(table, keys, values);
    }

    @Override
    public void afterUpdate(SynchronizedInfo table, Map<String, Object> keys, Map<String, Object> values)
            throws Exception
    {
    }

    @Override
    public boolean beforeDelete(SynchronizedInfo table, Map<String, Object> keys) throws Exception
    {
        return true;
    }

    @Override
    public void afterDelete(SynchronizedInfo table, Map<String, Object> keys) throws Exception
    {
    }

    @Override
    public void beforeExists(SynchronizedInfo table, Map<String, Object> keys) throws Exception
    {
    }

    @Override
    public void afterLoad(SynchronizedInfo table, Map<String, Object> keys, Map<String, Object> values) throws Exception
    {
        if (values != null)
        {
            EntityMap<?> entityMap = getEntityMaps().get(table.getTable());
            if (entityMap != null)
            {
                Object entity = createEntity(entityMap, keys, values);
                for (ColumnMap columnMap : entityMap.getMembers(ColumnMap.class))
                {
                    ColumnStorage storage = columnMap.getStorage();
                    if (storage instanceof CommonFileColumnStorage)
                    {
                        String column = columnMap.getColumn();
                        if (entity instanceof Attachment)
                            column = "INPUTABLE";

                        Tools.log("load commonfile sorage table:" + entityMap.getTable() + ",column:" +
                                columnMap.getColumn());

                        Object value = storage.load(entity, columnMap, entityMap, null);
                        if (value != null)
                        {
                            if (Null.isNull(value))
                                value = null;
                            values.put(column, value);
                        }
                    }
                }
            }
        }
    }

    public Object createEntity(EntityMap<?> entityMap, Map<String, Object> keys, Map<String, Object> values)
            throws Exception
    {
        Object entity = entityMap.getType().newInstance();
        for (ColumnMap columnMap : entityMap.getMembers(ColumnMap.class))
        {
            Class type = columnMap.getType();
            if (DataConvert.isBaseType(type))
            {
                String column = columnMap.getColumn();

                Object value = keys.get(column);
                if (value == null)
                    value = values.get(column);

                if (!Null.isNull(value))
                {
                    columnMap.set(entity, DataConvert.convertType(type, value));
                }
            }
        }

        return entity;
    }
}
