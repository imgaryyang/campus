package com.gzzm.platform.commons.database;

import net.cyan.thunwind.dao.GeneralDao;
import net.cyan.thunwind.dialect.Dialect;
import net.cyan.thunwind.meta.TableInfo;

import java.sql.*;
import java.util.*;

/**
 * 两个数据库复制的dao
 *
 * @author camel
 * @date 12-8-20
 */
public abstract class DataSynDao extends GeneralDao
{
    public DataSynDao()
    {
    }

    public TableInfo getTableInfo(String tableName) throws Exception
    {
        return getManager().getDialect().getTableInfo(tableName, getConnection());
    }

    public void syn(TableInfo tableInfo) throws Exception
    {
        tableInfo.synchronize(getConnection(), getManager().getDialect());
    }

    public TableSyn getTableSyn(String tableName) throws Exception
    {
        return load(TableSyn.class, tableName);
    }

    public List<String> getAllTables() throws Exception
    {
        Connection connection = getConnection();
        Dialect dialect = getManager().getDialect();

        ResultSet rs = connection.getMetaData().getTables(dialect.getCatalog(connection),
                dialect.getSchema(connection), null, new String[]{"TABLE"});

        try
        {
            List<String> tables = new ArrayList<String>();

            while (rs.next())
            {
                tables.add(rs.getString("TABLE_NAME"));
            }

            return tables;
        }
        finally
        {
            try
            {
                rs.close();
            }
            catch (Throwable ex)
            {
                //释放资源
            }
        }
    }
}