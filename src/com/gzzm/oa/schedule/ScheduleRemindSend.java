package com.gzzm.oa.schedule;

import java.util.*;

import net.cyan.nest.annotation.Inject;

public class ScheduleRemindSend implements Runnable
{
    @Inject
    private ScheduleDao dao;
    
    public void run()
    {
        
    }
    
    public void sendRemind() throws Exception
    {
        List<Schedule> remindList = dao.getRemaindSchdule(new Date());
        for(Schedule schedule : remindList)
        {
            if(schedule.getTag() == ScheduleTag.user)
            {
                
            }
        }
    }
}
