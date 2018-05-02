package com.gzzm.oa.schedule;

import com.gzzm.platform.annotation.AuthDeptIds;
import com.gzzm.platform.annotation.UserId;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.AuthDeptDisplay;
import net.cyan.arachne.annotation.ObjectResult;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.DateUtils;
import net.cyan.nest.annotation.Inject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 日程视图
 *
 * @author camel
 * @date 2010-3-20
 */
@Service(url = "/oa/schedule/view")
public class ScheduleView extends CalendarView<Schedule>
{
    @Inject
    private ScheduleDao dao;

    /**
     * 用户或部门
     */
    private ScheduleTag tag = ScheduleTag.user;

    /**
     * 注入当前用户的ID，仅当type=0时有用
     */
    @UserId
    private Integer userId;

    /**
     * 当前所维护的日程的部门ID，仅当type=1时有用
     */
    private Integer deptId;

    /**
     * 注入当前拥有权限操作的部门ID
     */
    @AuthDeptIds
    private Collection<Integer> authDeptIds;

    private boolean readOnly;

    public ScheduleView()
    {
    }

    public Integer getUserId()
    {
        return userId;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public ScheduleTag getTag()
    {
        return tag;
    }

    public void setTag(ScheduleTag tag)
    {
        this.tag = tag;
    }

    public void setType(CalendarViewType type)
    {
        this.type = type;
    }

    public boolean isReadOnly()
    {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly)
    {
        this.readOnly = readOnly;
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();

        if (tag == ScheduleTag.dept && deptId == null && (!readOnly || authDeptIds == null))
        {
            deptId = DeptOwnedCrudUtils.getDefaultDeptId(authDeptIds, this);
        }
    }

    @Override
    protected Date getTime(Schedule schedule)
    {
        return schedule.getStartTime();
    }

    private Date getEndTime(Schedule schedule)
    {
        return schedule.getEndTime();
    }

    @Override
    protected List<Schedule> loadItems(Date startTime, Date endTime) throws Exception
    {
        if (tag == ScheduleTag.user)
        {
            return dao.getUserSchedules(userId, startTime, endTime);
        }
        else
        {
            Collection<Integer> deptIds;
            if (deptId != null)
                deptIds = Collections.singleton(deptId);
            else
                deptIds = authDeptIds;
            return dao.getDeptSchedules(deptIds, startTime, endTime);
        }
    }

    @Override
    public boolean isAutoSelect()
    {
        return true;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view;

        if (tag == ScheduleTag.user || readOnly || authDeptIds != null && authDeptIds.size() == 1)
            view = new PageTableView(false);
        else
            view = new ComplexTableView(new AuthDeptDisplay(), "deptId", false);

        initView(view);

        view.importCss("/oa/schedule/view.css");
        view.importJs("/oa/schedule/view.js");


        return view;
    }

    @Service(url = "/oa/schedule/view/{$0}")
    public Schedule loadSchedule(Integer scheduleId) throws Exception
    {
        return dao.getSchedule(scheduleId);
    }

    @Service(url = "/oa/schedule/view.load")
    public List<Schedule> loadSchedules(Integer[] scheduleIds) throws Exception
    {
        return dao.getSchedules(scheduleIds);
    }

    @ObjectResult
    @Service(url = "/oa/schedule/view.remove")
    public void removeSchedule(Integer scheduleId) throws Exception
    {
        dao.deleteSchedule(scheduleId);
    }

    /**
     * 将数据列表转化为日视图
     *
     * @param items 数据列表
     * @param time  日视图某天的开始时间，用于计算每一行的开始时间和结束时间
     * @return 日视图，CalendarItemList列表，每一行代表一个小时
     */
    @SuppressWarnings("unchecked")
    private List<CalendarItemList<Schedule>> toDayList(List<Schedule> items, Date time) {
        List<CalendarItemList<Schedule>> list = new ArrayList<CalendarItemList<Schedule>>();
        if (startHour > 0) time = DateUtils.addHour(time, startHour);
        //24小时，每小时为一行
        for (int hour = startHour; hour < endHour; hour++) {
            String s = hour + ":00";
            if (hour < 10) s = "0" + s;
            CalendarItemList<Schedule> calendarItemList = new CalendarItemList<Schedule>(s);
            list.add(calendarItemList);
            CalendarItem<Schedule> calendarItem = new CalendarItem<Schedule>(time, time = DateUtils.addHour(time, 1));
            calendarItemList.setCalendarItems(calendarItem);
            for (Schedule i : items) {
                if (getTime(i).before(time)&& getEndTime(i).getTime()>=time.getTime()) {
                    //日程在这个小时内
                    calendarItem.addItem(i);
                }
            }
        }
        return list;
    }

    /**
     * 将数据列表转化为周视图
     *
     * @param items 数据列表
     * @param time  周视图一周的开始时间，用于计算每个单元格的开始时间和结束时间
     * @return 周视图，为一个CalendarItemList列表，每一行代表一个小时，每一列代表一天
     */
    @SuppressWarnings("unchecked")
    private List<CalendarItemList<Schedule>> toWeekList(List<Schedule> items, Date time) {
        List<CalendarItemList<Schedule>> list = new ArrayList<CalendarItemList<Schedule>>();
        //一周七天，每天为一列
        for (int week = 0; week < 7; week++) {
            if (week == 0) {
                //第一次执行的时候time是0时0分0秒
                if (startHour > 0) {
                    time = DateUtils.addHour(time, startHour);
                }
            }
            else {
                //新的一天
                if (startHour > 0 || endHour > 0) {
                    time = DateUtils.addHour(time, 24 - endHour + startHour);
                }
            }
            //24小时，每小时为一行
            for (int hour = startHour; hour < endHour; hour++) {
                CalendarItem calendarItem = new CalendarItem(time, time = DateUtils.addHour(time, 1));
                if (week == 0) {
                    String s = hour + ":00";
                    if (hour < 10) {
                        s = "0" + s;
                    }
                    CalendarItemList<Schedule> calendarItemList = new CalendarItemList(s);
                    list.add(calendarItemList);
                    CalendarItem[] calendarItems = new CalendarItem[7];
                    calendarItems[0] = calendarItem;
                    calendarItemList.setCalendarItems(calendarItems);
                }
                else {
                    list.get(hour - startHour).getCalendarItems()[week] = calendarItem;
                }
                //搜索在这个小时内的数据，itmes已经按时间排好续，只要按顺序搜索即可
                for (Schedule i : items) {
                    if (getTime(i).before(time)&& getEndTime(i).getTime()>=time.getTime()) {
                        //日程在这个小时内
                        calendarItem.addItem(i);
                    }
                }
            }
        }
        return list;
    }

    /**
     * 将数据列表转化为月视图
     *
     * @param items   数据列表
     * @param time    月视图某月的开始时间，用于计算每一格的开始时间和结束时间
     * @param endTime 这一月的结束时间
     * @return 日视图，为一个CalendarItemList列表，每一格代表一天
     */
    @SuppressWarnings("unchecked")
    private List<CalendarItemList<Schedule>> toMonthList(List<Schedule> items, Date time, Date endTime) {
        List<CalendarItemList<Schedule>> list = new ArrayList<CalendarItemList<Schedule>>();
        int week = DateUtils.getDay(time) - 1;
        CalendarItem<Schedule>[] calendarItems = null;
        DateFormat format = new SimpleDateFormat("MMM d");
        while (time.before(endTime)) {
            if (week == 0 || calendarItems == null) {
                CalendarItemList<Schedule> calendarItemList = new CalendarItemList<Schedule>();
                list.add(calendarItemList);
                calendarItems = new CalendarItem[7];
                calendarItemList.setCalendarItems(calendarItems);
            }
            Date nextDay= DateUtils.addDate(time, 1);
            //以日期为区间的名称
            CalendarItem calendarItem = new CalendarItem(format.format(time), time, nextDay);
            calendarItems[week] = calendarItem;
            week = (week + 1) % 7;
            //搜索在这个小时内的数据，itmes已经按时间排好续，只要按顺序搜索即可
            for (Schedule i : items) {
                if (DateUtils.truncate(getTime(i)).getTime()<=time.getTime() && getEndTime(i).after(time)) {
                    //日程在这天内
                    calendarItem.addItem(i);
                }
            }
            time=nextDay;
        }
        return list;
    }

    @Override
    protected List<CalendarItemList<Schedule>> loadList(Date startTime, Date endTime) throws Exception {
        List<Schedule> items = loadItems(startTime, endTime);
        List<CalendarItemList<Schedule>> list = createList();
        switch (type) {
            case day:
                list = toDayList(items, startTime);
                break;
            case week:
            case weekday:
                list = toWeekList(items, startTime);
                break;
            default:
                list = toMonthList(items, startTime, endTime);
        }
        return list;
    }
}