package com.gzzm.platform.commons;

import net.cyan.thunwind.annotation.*;

/**
 * 系统配置表
 *
 * @author camel
 * @date 2009-7-23
 */
@Entity(table = "PFCONFIG", keys = "configName")
public class Config
{
    @ColumnDescription(type = "varchar(50)")
    private String configName;

    @ColumnDescription(type = "varchar(2000)")
    private String configValue;

    public Config()
    {
    }

    public String getConfigName()
    {
        return configName;
    }

    public void setConfigName(String configName)
    {
        this.configName = configName;
    }

    public String getConfigValue()
    {
        return configValue;
    }

    public void setConfigValue(String configValue)
    {
        this.configValue = configValue;
    }

    public boolean equals(Object o)
    {
        return this == o || o instanceof Config && configName.equals(((Config) o).configName);
    }

    public int hashCode()
    {
        return configName.hashCode();
    }
}
