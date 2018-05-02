package com.gzzm.safecampus.campus.bus;

import net.cyan.commons.util.DateUtils;

import java.util.Date;

/**
 * @author czy
 * @date 2018/3/20 16:50
 */
public class BusScheduleItem
{
    private String scheduleName;

    private Date scheduleTime;

    private ScheduleType type;

    public BusScheduleItem()
    {
    }

    public String getScheduleName()
    {
        return scheduleName;
    }

    public void setScheduleName(String scheduleName)
    {
        this.scheduleName = scheduleName;
    }

    public Date getScheduleTime()
    {
        return scheduleTime;
    }

    public void setScheduleTime(Date scheduleTime)
    {
        this.scheduleTime = scheduleTime;
    }

    public String getScheduleTimeStr()
    {
        if(scheduleTime != null)
        return DateUtils.toString(scheduleTime,"yyyy-MM-dd");
        return null;
    }

    public ScheduleType getType()
    {
        return type;
    }

    public void setType(ScheduleType type)
    {
        this.type = type;
    }
}
