package com.gzzm.oa.schedule;

import com.gzzm.platform.commons.Tools;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.util.Date;

/**
 * 启动时遍历所有未提醒的业务，添加到提醒任务里
 *
 * @author camel
 * @date 11-11-3
 */
public class ScheduleRemindStart implements Runnable
{
    @Inject
    private static Provider<ScheduleDao> daoProvider;

    public ScheduleRemindStart()
    {
    }

    public void run()
    {
        ScheduleDao dao = daoProvider.get();

        Date time = new Date(System.currentTimeMillis() - ScheduleRemindJob.L);

        try
        {
            for (Schedule schedule : dao.getShouldRemindSchedules(time))
            {
                Date remindTime = schedule.getRemindTime();
                Date remindTime1 = schedule.getRemindTime1();
                Date lastRemindTime = schedule.getLastRemindTime();

                if (remindTime != null)
                {
                    boolean b = true;
                    long remindTimeValue = remindTime.getTime();

                    if (lastRemindTime != null)
                    {
                        long lastRemindTimeValue = lastRemindTime.getTime();

                        if (remindTimeValue > lastRemindTimeValue - ScheduleRemindJob.L &&
                                remindTimeValue < lastRemindTimeValue + ScheduleRemindJob.R)
                        {
                            //提醒时间在误差范围内，不重复提醒
                            b = false;
                        }
                    }

                    if (b)
                    {
                        Jobs.addJob(new ScheduleRemindJob(schedule.getScheduleId()), remindTime,
                                "schedule_remind_" + schedule.getScheduleId());
                    }
                }


                if (remindTime1 != null)
                {
                    boolean b = true;
                    long remindTimeValue = remindTime1.getTime();

                    if (lastRemindTime != null)
                    {
                        long lastRemindTimeValue = lastRemindTime.getTime();

                        if (remindTimeValue > lastRemindTimeValue - ScheduleRemindJob.L &&
                                remindTimeValue < lastRemindTimeValue + ScheduleRemindJob.R)
                        {
                            //提醒时间在误差范围内，不重复提醒
                            b = false;
                        }
                    }

                    if (b)
                    {
                        Jobs.addJob(new ScheduleRemindJob(schedule.getScheduleId(), true), remindTime1,
                                "schedule_remind_" + schedule.getScheduleId() + "_1");
                    }
                }
            }
        }
        catch (Throwable ex)
        {
            //已经最外层，不能继续抛出异常
            Tools.log(ex);
        }
    }
}
