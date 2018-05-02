package com.gzzm.oa.schedule;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.User;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.DateUtils;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 用户日程视图，同时展示多个用户一周的日常
 *
 * @author camel
 * @date 12-3-4
 */
@Service(url = "/oa/schedule/userview")
public class UserScheduleView extends UserCalendarView<Schedule>
{
    @Inject
    private ScheduleDao dao;

    public UserScheduleView()
    {
    }

    public boolean isReadOnly()
    {
        return true;
    }

    @Override
    protected Date getTime(Schedule schedule)
    {
        return schedule.getStartTime();
    }

    @Override
    protected List<Schedule> loadItems(Date startTime, Date endTime) throws Exception
    {
        return dao.getUsersSchedules(getUserIds(), startTime, endTime);
    }

    @Override
    protected boolean isItemInCalendarItemList(Schedule schedule, CalendarItemList<Schedule> calendarItemList)
    {
        Integer userId = (Integer) calendarItemList.getId();

        if (schedule.getTag() == ScheduleTag.user)
        {
            return schedule.getCreator().equals(userId);
        }

        for (User user : schedule.getParticipants())
        {
            if (user.getUserId().equals(userId))
                return true;
        }

        return false;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView(false);

        initView(view);

        view.importCss("/oa/schedule/view.css");
        view.importJs("/oa/schedule/view.js");

        return view;
    }

    @Service(url = "/oa/schedule/userview/{$0}")
    public Schedule loadSchedule(Integer scheduleId) throws Exception
    {
        return dao.getSchedule(scheduleId);
    }

    @Override
    protected List<CalendarItemList<Schedule>> loadList(Date startTime, Date endTime) throws Exception
    {
        List<Schedule> items = loadItems(startTime, endTime);
        List<CalendarItemList<Schedule>> list = createList();
        initWeekList(list, items);
        return list;
    }

    @SuppressWarnings("unchecked")
    protected void initWeekList(List<CalendarItemList<Schedule>> list, List<Schedule> items)
    {
        for (CalendarItemList<Schedule> calendarItemList : list)
        {
            CalendarItem<Schedule>[] calendarItems = new CalendarItem[7];
            for (int i = 0; i < 7; i++)
                calendarItems[i] = new CalendarItem<Schedule>(dateList[i], dateList[i + 1]);
            calendarItemList.setCalendarItems(calendarItems);
        }

/*        for(CalendarItemList<Schedule> calendarItemList:list){
            for(CalendarItem<Schedule> calendarItem:calendarItemList.getCalendarItems()){
                for (Schedule item : items){
                    calendarItem.addItem(item);
                }
            }
        }*/
        int index = 0;
        //分配每项数据到相应的日历，数据从数据库中加载时已经排序过
        for (Schedule item : items)
        {
            for (CalendarItemList<Schedule> calendarItemList : list)
            {
                for(CalendarItem<Schedule> calendarItem:calendarItemList.getCalendarItems()){
                    if (isItemInCalendarItemList(item, calendarItemList)&&
                            DateUtils.truncate(getTime(item)).getTime()<=calendarItem.getStartTime().getTime()
                            && item.getEndTime().after(calendarItem.getStartTime())){
                        calendarItem.addItem(item);
                    }
                }
            }
        }
    }
}
