package com.gzzm.platform.flow;

import com.gzzm.platform.commons.*;
import net.cyan.arachne.RequestContext;
import net.cyan.commons.util.*;
import net.cyan.thunwind.meta.*;

import java.util.*;

/**
 * @author camel
 * @date 2014/9/19
 */
public abstract class TableFlowComponent<C extends FlowComponentContext> extends AbstractFlowComponent<C>
{
    private SimpleDao dao;

    private String tableName;

    private String database;

    private String sequenceName;

    private Map<String, Object> data;

    private TableInfo tableInfo;

    private String key;

    private ColumnInfo keyColumn;

    public TableFlowComponent(String tableName, String database, String sequenceName)
    {
        this.tableName = tableName;
        this.database = database;
        this.sequenceName = sequenceName;
    }

    public String getTableName()
    {
        return tableName;
    }

    public String getDatabase()
    {
        return database;
    }

    public String getSequenceName()
    {
        return sequenceName;
    }

    public Map<String, Object> getData()
    {
        return data;
    }

    public void setData(Map<String, Object> data)
    {
        this.data = data;
    }

    protected abstract String getLinkId(C context) throws Exception;

    protected abstract void setLinkId(String linkId, C context) throws Exception;

    protected SimpleDao getDao() throws Exception
    {
        if (dao == null)
            dao = SimpleDao.getInstance(database);

        return dao;
    }

    protected TableInfo getTableInfo() throws Exception
    {
        if (tableInfo == null)
            tableInfo = getDao().getTableInfo(tableName);

        return tableInfo;
    }

    public String getKey() throws Exception
    {
        if (key == null)
            key = getTableInfo().getPrimaryKey().getColumns().get(0);

        return key;
    }

    public ColumnInfo getKeyColumn() throws Exception
    {
        if (keyColumn == null)
            keyColumn = getTableInfo().getColumn(getKey());

        return keyColumn;
    }

    public Class getKeyType() throws Exception
    {
        return getKeyColumn().getColumnClass();
    }

    public int getKeyLength() throws Exception
    {
        return getKeyColumn().getScale();
    }

    protected Object getKeyValue(C context) throws Exception
    {
        String linkId = getLinkId(context);

        if (!StringUtils.isEmpty(linkId))
            return DataConvert.convertValue(getKeyType(), linkId);

        if (StringUtils.isEmpty(sequenceName))
        {
            Object defaultKeyValue = getDefaultKeyValue(context);

            if (defaultKeyValue != null)
                return DataConvert.convertType(getKeyType(), defaultKeyValue);
        }

        return null;
    }

    protected Object getDefaultKeyValue(C context) throws Exception
    {
        return context.getInstanceId();
    }

    protected Object generateKey() throws Exception
    {
        if (!StringUtils.isEmpty(sequenceName))
            return getDao().getId(sequenceName, getKeyLength(), getKeyType());

        return null;
    }


    protected Map<String, Object> loadData(C context) throws Exception
    {
        Object keyValue = getKeyValue(context);

        Map<String, Object> data;

        if (keyValue != null)
        {
            String key = getKey();

            String sql = "select * from " + tableName + " where " + key + "=:1";

            data = getDao().sqlQueryFirst(sql, keyValue);

            if (data == null)
            {
                data = createData();

                data.put(key, keyValue);
            }
        }
        else
        {
            data = createData();
        }

        return data;
    }

    protected Map<String, Object> createData() throws Exception
    {
        TableInfo tableInfo = getTableInfo();

        Map<String, Object> data = new CollectionUtils.IgnoreCaseMap<Object>();

        for (ColumnInfo columnInfo : tableInfo.getColumns())
        {
            data.put(columnInfo.getColumnName(), null);
        }

        return data;
    }

    @Override
    public void beforeShow(C context) throws Exception
    {
        super.beforeShow(context);

        data = loadData(context);

        context.getFlowPage().setData(data);
    }

    @Override
    public void extractData(C context) throws Exception
    {
        super.extractData(context);

        data = createData();

        RequestContext.getContext().fillForm(data, "data");

        changeData(data);

        context.getFlowPage().setData(data);
    }

    @Override
    public void saveData(C context) throws Exception
    {
        super.saveData(context);

        data.remove(getKey());

        Object keyValue = getKeyValue(context);
        if (keyValue == null)
            keyValue = generateKey();

        if (keyValue == null)
            throw new SystemException("key may not be null for " + tableName + "." + getKey());

        boolean empty = true;
        for (Map.Entry<String, Object> entry : data.entrySet())
        {
            if (entry.getValue() != null)
            {
                empty = false;
                break;
            }
        }

        if (!empty)
        {
            getDao().sqlInsertOrUpdate(tableName, data, Collections.singletonMap(getKey(), keyValue));
        }

        setLinkId(DataConvert.toString(keyValue), context);
    }

    protected void changeData(Map<String, Object> data) throws Exception
    {
        TableInfo tableInfo = dao.getTableInfo(tableName);

        for (Map.Entry<String, Object> entry : data.entrySet())
        {
            ColumnInfo column = tableInfo.getColumn(entry.getKey());

            if (column == null)
                entry.setValue(null);
            else
                entry.setValue(DataConvert.convertType(column.getColumnClass(), entry.getValue()));
        }
    }
}
