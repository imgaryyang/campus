package com.gzzm.oa.schedule;

import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.util.List;

/**
 * @author camel
 * @date 11-10-25
 */
public class ScheduleService
{
    @Inject
    private ScheduleDao dao;

    /**
     * @noinspection MismatchedQueryAndUpdateOfCollection
     */
    @Inject
    private List<Type> types;

    public ScheduleService()
    {
    }

    public void updateSchedule(Schedule schedule) throws Exception
    {
        if (schedule.getLinkId() == null)
            throw new IllegalArgumentException("link cannot be null");

        Integer scheduleId = dao.getScheduleIdByLink(schedule.getLinkId());

        if (scheduleId == null)
        {
            schedule.setScheduleId(null);

            if (schedule.getState() == null)
                schedule.setState(ScheduleState.notStarted);
            dao.add(schedule);
        }
        else
        {
            schedule.setScheduleId(scheduleId);
            schedule.setLinkId(null);
            schedule.setCreator(null);
            dao.update(schedule);
        }

        if (schedule.getRemindType() != null && schedule.getRemindType().length > 0)
        {
            if (schedule.getRemindTime() != null)
            {
                Jobs.addJob(new ScheduleRemindJob(schedule.getScheduleId()), schedule.getRemindTime(),
                        "schedule_remind_" + schedule.getScheduleId());
            }

            if (schedule.getRemindTime1() != null)
            {
                Jobs.addJob(new ScheduleRemindJob(schedule.getScheduleId(), true), schedule.getRemindTime1(),
                        "schedule_remind_" + schedule.getScheduleId() + "_1");
            }
        }
    }

    public boolean removeSchedule(String link) throws Exception
    {
        return dao.deleteScheduleByLink(link) != 0;
    }

    public Type getType(String name)
    {
        if (types != null)
        {
            for (Type type : types)
            {
                if (name.equals(type.getName()))
                    return type;
            }
        }

        return null;
    }
}
