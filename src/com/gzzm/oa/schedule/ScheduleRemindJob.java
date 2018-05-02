package com.gzzm.oa.schedule;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.message.Message;
import com.gzzm.platform.organ.User;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

import java.util.Date;

/**
 * 日程任务提醒
 *
 * @author camel
 * @date 11-11-3
 */
public class ScheduleRemindJob implements Runnable
{

    //延后误差为一个小时，即如果一小时之内的提醒还未提醒，则继续提醒
    public static final long L = 1000 * 60 * 60;

    //提前误差为五分钟
    public static final long R = 1000 * 60 * 5;

    @Inject
    private static Provider<ScheduleDao> daoProvider;

    private Integer scheduleId;

    /**
     * 是否第二次提醒
     */
    private boolean second;

    public ScheduleRemindJob(Integer scheduleId)
    {
        this.scheduleId = scheduleId;
    }

    public ScheduleRemindJob(Integer scheduleId, boolean second)
    {
        this.scheduleId = scheduleId;
        this.second = second;
    }

    public void run()
    {
        try
        {
            ScheduleDao dao = daoProvider.get();
            Schedule schedule = dao.getSchedule(scheduleId);

            if (schedule == null)
                return;

            Date remindTime = second ? schedule.getRemindTime1() : schedule.getRemindTime();

            if (remindTime != null)
            {
                long nowTimeValue = System.currentTimeMillis();
                long remindTimeValue = remindTime.getTime();

                if (remindTimeValue > nowTimeValue - L && remindTimeValue < nowTimeValue + R)
                {
                    //提醒时间和当前时间在误差范围内

                    Date lastRemindTime = second ? schedule.getLastRemindTime1() : schedule.getLastRemindTime();

                    if (lastRemindTime != null)
                    {
                        //已经提醒过，判断提醒时间是否在误差范围内
                        long lastRemindTimeValue = lastRemindTime.getTime();

                        if (remindTimeValue > lastRemindTimeValue - L && remindTimeValue < lastRemindTimeValue + R)
                        {
                            //提醒时间在误差范围内，不重复提醒
                            return;
                        }
                    }

                    //更新最后提醒时间
                    if (second)
                        schedule.setLastRemindTime1(new Date());
                    else
                        schedule.setLastRemindTime(new Date());
                    dao.update(schedule);
                }

                ScheduleRemindType[] types = schedule.getRemindType();
                String[] methods = new String[types.length];
                for (int i = 0; i < types.length; i++)
                    methods[i] = types[i].name();

                if (schedule.getTag() == ScheduleTag.user)
                {
                    //个人日程，提醒创建者
                    Message message = new Message();

                    message.setSender(schedule.getCreator());
                    message.setUserId(schedule.getCreator());
                    message.setMessage(Tools.getMessage("oa.schedule.remind", schedule));
                    message.setApp("schedule");
                    message.setMethods(methods);
                    message.setForce(true);

                    message.send();
                }
                else
                {
                    //发送给每一个参与者
                    for (User user : schedule.getParticipants())
                    {
                        Message message = new Message();

                        message.setSender(schedule.getCreator());
                        message.setUserId(user.getUserId());
                        message.setFromDeptId(schedule.getDeptId());
                        message.setToDeptId(schedule.getDeptId());
                        message.setMessage(Tools.getMessage("oa.schedule.remind", schedule));
                        message.setApp("schedule");
                        message.setMethods(methods);
                        message.setForce(true);

                        message.send();
                    }
                }
            }
        }
        catch (Throwable ex)
        {
            Tools.log("schedule remind failed,scheduleId:" + scheduleId, ex);
        }
    }
}
