package com.gzzm.platform.timeout;

import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.*;

/**
 * @author camel
 * @date 12-8-16
 */
@Entity(table = "PFTIMEOUTDAY", keys = {"configId", "levelId"})
public class TimeoutDay
{
    private Integer configId;

    @NotSerialized
    private TimeoutConfig config;

    private Integer levelId;

    @NotSerialized
    private TimeoutLevel level;

    @ColumnDescription(type = "number(2)")
    private Integer day;

    public TimeoutDay()
    {
    }

    public Integer getConfigId()
    {
        return configId;
    }

    public void setConfigId(Integer configId)
    {
        this.configId = configId;
    }

    public TimeoutConfig getConfig()
    {
        return config;
    }

    public void setConfig(TimeoutConfig config)
    {
        this.config = config;
    }

    public Integer getLevelId()
    {
        return levelId;
    }

    public void setLevelId(Integer levelId)
    {
        this.levelId = levelId;
    }

    public TimeoutLevel getLevel()
    {
        return level;
    }

    public void setLevel(TimeoutLevel level)
    {
        this.level = level;
    }

    public Integer getDay()
    {
        return day;
    }

    public void setDay(Integer day)
    {
        this.day = day;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof TimeoutDay))
            return false;

        TimeoutDay that = (TimeoutDay) o;

        return configId.equals(that.configId) && levelId.equals(that.levelId);
    }

    @Override
    public int hashCode()
    {
        int result = configId.hashCode();
        result = 31 * result + levelId.hashCode();
        return result;
    }
}
