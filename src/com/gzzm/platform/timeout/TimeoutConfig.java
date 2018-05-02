package com.gzzm.platform.timeout;

import com.gzzm.platform.commons.UpdateTimeService;
import com.gzzm.platform.organ.Dept;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.util.Provider;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.nest.annotation.Inject;
import net.cyan.thunwind.annotation.*;

import java.util.*;

/**
 * 超时配置，配置每类事项，黄牌警告多少天，红牌警告多少天，预受理多少天，等
 *
 * @author camel
 * @date 12-8-16
 */
@Entity(table = "PFTIMEOUTCONFIG", keys = "configId")
@Indexes(@Index(columns = {"TYPEID", "DEPTID"}))
public class TimeoutConfig
{
    @Inject
    private static Provider<TimeoutService> serviceProvider;

    @Generatable(length = 6)
    private Integer configId;

    private Integer deptId;

    @NotSerialized
    private Dept dept;

    @Require
    @ColumnDescription(type = "varchar(250)")
    private String configName;

    /**
     * 超时内容的ID
     */
    @ColumnDescription(type = "varchar(250)")
    private String typeId;

    /**
     * 默认的时限
     */
    @ColumnDescription(type = "number(5)")
    private Integer timeLimit;

    @Require
    @ColumnDescription(nullable = false, defaultValue = "0")
    private TimeUnit unit;

    @ColumnDescription(type = "varchar(4000)")
    private String condition;

    @NotSerialized
    @OneToMany
    private List<TimeoutDay> days;

    public TimeoutConfig()
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

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public Dept getDept()
    {
        return dept;
    }

    public void setDept(Dept dept)
    {
        this.dept = dept;
    }

    public String getTypeId()
    {
        return typeId;
    }

    public void setTypeId(String typeId)
    {
        this.typeId = typeId;
    }

    public TimeUnit getUnit()
    {
        return unit;
    }

    public void setUnit(TimeUnit unit)
    {
        this.unit = unit;
    }

    public String getCondition()
    {
        return condition;
    }

    public void setCondition(String condition)
    {
        this.condition = condition;
    }

    @NotSerialized
    public String getTypeName() throws Exception
    {
        if (typeId != null)
        {
            TimeoutType type = serviceProvider.get().getTimeoutType(typeId);
            if (type != null)
                return type.getTypeName();
        }

        return null;
    }

    public String getConfigName()
    {
        return configName;
    }

    public void setConfigName(String configName)
    {
        this.configName = configName;
    }

    public Integer getTimeLimit()
    {
        return timeLimit;
    }

    public void setTimeLimit(Integer timeLimit)
    {
        this.timeLimit = timeLimit;
    }

    public List<TimeoutDay> getDays()
    {
        return days;
    }

    public void setDays(List<TimeoutDay> days)
    {
        this.days = days;
    }

    public Integer getDay(Integer levelId)
    {
        for (TimeoutDay day : getDays())
        {
            if (day.getLevelId().equals(levelId))
                return day.getDay();
        }

        return null;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof TimeoutConfig))
            return false;

        TimeoutConfig that = (TimeoutConfig) o;

        return configId.equals(that.configId);
    }

    @Override
    public int hashCode()
    {
        return configId.hashCode();
    }

    @Override
    public String toString()
    {
        return configName;
    }

    public static void setUpdated() throws Exception
    {
        //数据被修改之后更新菜单更新时间
        UpdateTimeService.updateLastTime("Timeout", new Date());
    }
}
