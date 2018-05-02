package com.gzzm.platform.syn;

import net.cyan.datasyn.*;
import net.cyan.thunwind.PersistenceManager;
import net.cyan.thunwind.meta.TableInfo;

import java.util.*;

/**
 * @author camel
 * @date 13-5-22
 */
public class SynInit
{
    private static Map<String, DataSynchronizer> dataSynchronizers;

    private static Map<String, Set<String>> initedTablesMap;

    private static Map<String, Set<Class>> initedEntitysMap;

    public SynInit()
    {
    }

    private static synchronized DataSynchronizer getDataSynchronizer(String schema)
    {
        if (dataSynchronizers == null)
            dataSynchronizers = new HashMap<String, DataSynchronizer>();

        DataSynchronizer dataSynchronizer = dataSynchronizers.get(schema);

        if (dataSynchronizer == null)
            dataSynchronizers.put(schema, dataSynchronizer = new DataSynchronizer(schema, null, null, false));

        return dataSynchronizer;
    }

    public static synchronized void initTable(String table, String source, String synSchema) throws Exception
    {
        initTable(table, source, synSchema, null);
    }

    public static synchronized void initTable(String table, String source, String synSchema, String condition)
            throws Exception
    {
        if (initedTablesMap == null)
            initedTablesMap = new HashMap<String, Set<String>>();

        Set<String> initedTables = initedTablesMap.get(synSchema);
        if (initedTables == null)
            initedTablesMap.put(synSchema, initedTables = new HashSet<String>());

        if (initedTables.contains(table))
            return;

        initedTables.add(table);

        SynchronizedInfo synchronizedInfo = new SynchronizedInfo(table);
        synchronizedInfo.setSynSchema(synSchema);
        synchronizedInfo.setMode(SynchronizeMode.table);

        if (condition != null)
            synchronizedInfo.setCondition(condition);

        getDataSynchronizer(source).initTable(synchronizedInfo);
    }

    public static synchronized void initEntity(Class<?> entityType, String schema) throws Exception
    {
        if (initedEntitysMap == null)
            initedEntitysMap = new HashMap<String, Set<Class>>();

        Set<Class> initedEntitys = initedEntitysMap.get(entityType);

        if (initedEntitys == null)
            initedEntitys = new HashSet<Class>();

        if (initedEntitys.contains(entityType))
            return;

        initedEntitys.add(entityType);

        TableInfo.synchronize(PersistenceManager.getManager(schema), entityType);
    }
}
