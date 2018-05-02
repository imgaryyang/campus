package com.gzzm.platform.timeout;

import net.cyan.commons.util.*;

import java.util.*;

/**
 * @author camel
 * @date 2016/6/18
 */
public class TimeoutConfigInfo
{
    private Integer configId;

    private String typeId;

    private Integer timeLimit;

    private TimeUnit unit;

    private String condition;

    private Map<Integer, Integer> days = new HashMap<Integer, Integer>();

    public TimeoutConfigInfo(TimeoutConfig config)
    {
        configId = config.getConfigId();
        typeId = config.getTypeId();
        unit = config.getUnit();
        timeLimit = config.getTimeLimit();
        condition = config.getCondition();

        for (TimeoutDay day : config.getDays())
        {
            if (day.getDay() != null)
                days.put(day.getLevelId(), day.getDay());
        }
    }

    public Integer getConfigId()
    {
        return configId;
    }

    public String getTypeId()
    {
        return typeId;
    }

    public Integer getTimeLimit()
    {
        return timeLimit;
    }

    public TimeUnit getUnit()
    {
        return unit;
    }

    Map<Integer, Integer> getDays()
    {
        return days;
    }

    public Integer getDay(Integer levelId)
    {
        return days.get(levelId);
    }

    public String getCondition()
    {
        return condition;
    }

    public boolean accept(Object obj) throws Exception
    {
        return StringUtils.isEmpty(condition) || DataConvert.toBoolean(BeanUtils.eval(condition, obj, null));
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof TimeoutConfigInfo))
            return false;

        TimeoutConfigInfo that = (TimeoutConfigInfo) o;

        return configId.equals(that.configId);
    }

    @Override
    public int hashCode()
    {
        return configId.hashCode();
    }
}
