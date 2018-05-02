package com.gzzm.platform.commons.database;

import com.gzzm.platform.commons.*;
import net.cyan.commons.util.DataConvert;
import net.cyan.thunwind.meta.*;

import java.util.*;

/**
 * 用于同步两个数据库中的同一个表
 *
 * @author camel
 * @date 12-8-20
 */
public class DataSynService
{
    private String source;

    private String target;

    private DataSynDao sourceDao;

    private DataSynDao targetDao;

    public DataSynService(String source, String target)
    {
        this.source = source;
        this.target = target;
    }

    public String getSource()
    {
        return source;
    }

    public String getTarget()
    {
        return target;
    }

    public DataSynDao getSourceDao() throws Exception
    {
        if (sourceDao == null)
            sourceDao = Tools.getBean(DataSynDao.class, source);
        return sourceDao;
    }

    public DataSynDao getTargetDao() throws Exception
    {
        if (targetDao == null)
            targetDao = Tools.getBean(DataSynDao.class, target);

        return targetDao;
    }

    public void synTable(String tableName) throws Exception
    {
        Tools.log("同步表结构:" + tableName);

        TableInfo tableInfo = getSourceDao().getTableInfo(tableName);

        for (Iterator<Constraint> iterator = tableInfo.getConstraints().iterator(); iterator.hasNext(); )
        {
            Constraint constraint = iterator.next();

            if (constraint instanceof ForeignKeyConstraint)
            {
                iterator.remove();
            }
        }

        getTargetDao().syn(tableInfo);
    }

    public void synData(String sourceTableName, String targetTableName, String increaseColumn,
                        Map<String, String> columnsMap) throws Exception
    {
        synData(sourceTableName, targetTableName,
                increaseColumn == null ? null : Collections.singletonList(increaseColumn), columnsMap);
    }

    @SuppressWarnings("unchecked")
    public void synData(String sourceTableName, String targetTableName, List<String> increaseColumns,
                        Map<String, String> columnsMap) throws Exception
    {
        try
        {
            Tools.log("开始同步数据:" + sourceTableName);

            DataSynDao targetDao = getTargetDao();
            DataSynDao sourceDao = getSourceDao();

            TableInfo sourceTableInfo = sourceDao.getTableInfo(sourceTableName);
            TableInfo targetTableInfo = targetDao.getTableInfo(targetTableName);

            if (targetTableInfo == null)
                return;

            PrimaryKeyConstraint primaryKey = targetTableInfo.getPrimaryKey();
            if (primaryKey == null)
                return;

            List<String> keyColumns = primaryKey.getColumns();

            if (increaseColumns != null && increaseColumns.size() == 0)
                increaseColumns = null;

            if (increaseColumns == null && keyColumns.size() > 0)
                increaseColumns = keyColumns;

            Map<String, Object> lastValues = null;
            TableSyn tableSyn = null;
            List<Class<?>> increaseColumnTypes;
            if (increaseColumns != null)
            {
                int increaseColumnCount = increaseColumns.size();

                increaseColumnTypes = new ArrayList<Class<?>>(increaseColumnCount);

                for (String increaseColumn : increaseColumns)
                {
                    increaseColumnTypes.add(sourceTableInfo.getColumn(increaseColumn).getColumnClass());
                }

                tableSyn = targetDao.getTableSyn(targetTableName);
                if (tableSyn != null)
                {
                    String lastValueString = tableSyn.getLastValue();

                    if (lastValueString != null)
                    {
                        String[] ss = lastValueString.split(",,,,,");
                        if (ss.length == increaseColumnCount)
                        {
                            lastValues = new HashMap<String, Object>();

                            for (int i = 0; i < increaseColumnCount; i++)
                            {
                                lastValues.put(increaseColumns.get(i),
                                        DataConvert.convertValue(increaseColumnTypes.get(i), ss[i]));
                            }
                        }
                    }
                }

                if (lastValues == null)
                {
                    lastValues = new HashMap<String, Object>();
                    for (String increaseColumn : increaseColumns)
                    {
                        lastValues.put(increaseColumn, null);
                    }
                }
            }

            StringBuilder queryBuffer = new StringBuilder("select * from " + sourceTableName);

            if (increaseColumns != null)
            {
                int increaseColumnCount = increaseColumns.size();

                queryBuffer.append(" where ");

                if (increaseColumnCount > 1)
                    queryBuffer.append("(");

                boolean first = true;
                for (String increaseColumn : increaseColumns)
                {
                    if (first)
                        first = false;
                    else
                        queryBuffer.append(",");

                    queryBuffer.append("`").append(increaseColumn).append("`");
                }

                if (increaseColumnCount > 1)
                    queryBuffer.append(")");

                queryBuffer.append(">");

                if (increaseColumnCount > 1)
                    queryBuffer.append("(");

                first = true;
                for (String increaseColumn : increaseColumns)
                {
                    if (first)
                        first = false;
                    else
                        queryBuffer.append(",");

                    queryBuffer.append("?").append(increaseColumn);
                }

                if (increaseColumnCount > 1)
                    queryBuffer.append(")");

                queryBuffer.append(" order by ");

                first = true;
                for (String increaseColumn : increaseColumns)
                {
                    if (first)
                        first = false;
                    else
                        queryBuffer.append(",");

                    queryBuffer.append("`").append(increaseColumn).append("`");
                }

                queryBuffer.append(" limit 30");
            }

            StringBuilder existsQuerybuffer = new StringBuilder("exists ").append(targetTableName).append(" where ");
            boolean b = false;
            for (String key : keyColumns)
            {
                if (b)
                    existsQuerybuffer.append(" and ");
                else
                    b = true;

                existsQuerybuffer.append("`").append(key).append("`").append("=?").append(key);
            }

            String query = queryBuffer.toString();
            String existsQuery = existsQuerybuffer.toString();

            Map<String, Object> keyValues = null;
            Map<String, Object> values = null;

            while (true)
            {
                List<Map<String, Object>> records = (List) sourceDao.sqlQuery(query, Map.class, lastValues);

                if (records.size() == 0)
                    return;

                Tools.log("同步" + sourceTableName + "," + records.size() + "条");

                for (Map<String, Object> record : records)
                {
                    if (increaseColumns != null)
                    {
                        if (lastValues == null)
                            lastValues = new HashMap<String, Object>();
                        else
                            lastValues.clear();

                        for (String increaseColumn : increaseColumns)
                        {
                            lastValues.put(increaseColumn, record.get(increaseColumn));
                        }
                    }

                    if (values == null)
                        values = new HashMap<String, Object>();
                    else
                        values.clear();

                    for (Map.Entry<String, Object> entry : record.entrySet())
                    {
                        String key = entry.getKey();
                        if (columnsMap != null)
                        {
                            String key1 = columnsMap.get(key);
                            if (key1 != null)
                                key = key1;
                        }

                        if (targetTableInfo.getColumn(key) != null)
                            values.put(key, entry.getValue());
                    }

                    record = values;

                    if (keyValues == null)
                        keyValues = new HashMap<String, Object>();

                    for (String keyColumn : keyColumns)
                    {
                        keyValues.put(keyColumn, record.get(keyColumn));
                    }

                    if (targetDao.sqlExists(existsQuery, record))
                    {
                        for (String keyColumn : keyColumns)
                        {
                            record.remove(keyColumn);
                        }

                        if (record.size() > 0)
                        {
                            boolean hasValues = false;
                            for (Map.Entry<String, Object> entry : record.entrySet())
                            {
                                if (entry.getValue() != null)
                                {
                                    hasValues = true;
                                    break;
                                }
                            }


                            if (hasValues)
                                targetDao.sqlUpdate(targetTableName, null, record, keyValues);
                        }
                    }
                    else
                    {
                        targetDao.sqlInsert(targetTableName, record);
                    }
                }

                if (increaseColumns != null)
                {
                    if (tableSyn == null)
                    {
                        tableSyn = new TableSyn();
                        tableSyn.setTableName(targetTableName);
                    }

                    StringBuilder buffer = new StringBuilder();
                    for (String increaseColumn : increaseColumns)
                    {
                        if (buffer.length() > 0)
                            buffer.append(",,,,,");

                        buffer.append(DataConvert.toString(lastValues.get(increaseColumn)));
                    }

                    tableSyn.setLastValue(buffer.toString());
                    targetDao.save(tableSyn);
                }
                else
                {
                    break;
                }
            }
        }
        catch (Throwable ex)
        {
            throw new SystemException("syn table:" + sourceTableName + " failed", ex);
        }
    }

    public void synData(String sourceTableName, String targetTableName) throws Exception
    {
        synData(sourceTableName, targetTableName, (List<String>) null, null);
    }

    public void synData(String tableName) throws Exception
    {
        synData(tableName, tableName);
    }

    public void synAllTables() throws Exception
    {
        for (String table : getSourceDao().getAllTables())
        {
            synTable(table);
        }
    }

    public void synAllTablesData() throws Exception
    {
        for (String table : getSourceDao().getAllTables())
        {
            synData(table);
        }
    }

    public void synAllTablesAndData() throws Exception
    {
        List<String> tables = getSourceDao().getAllTables();

        for (String table : tables)
        {
            synTable(table);
        }

        for (String table : tables)
        {
            synData(table);
        }
    }
}
