package com.gzzm.platform.commons.database;

import net.cyan.thunwind.annotation.*;

/**
 * @author camel
 * @date 12-8-23
 */
@Entity(table = "PFTABLESYN", keys = "tableName")
public class TableSyn
{
    @ColumnDescription(type = "varchar(200)")
    private String tableName;

    @ColumnDescription(type = "varchar(200)")
    private String lastValue;

    public TableSyn()
    {
    }

    public String getTableName()
    {
        return tableName;
    }

    public void setTableName(String tableName)
    {
        this.tableName = tableName;
    }

    public String getLastValue()
    {
        return lastValue;
    }

    public void setLastValue(String lastValue)
    {
        this.lastValue = lastValue;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof TableSyn))
            return false;

        TableSyn tableSyn = (TableSyn) o;

        return tableName.equals(tableSyn.tableName);
    }

    @Override
    public int hashCode()
    {
        return tableName.hashCode();
    }
}
