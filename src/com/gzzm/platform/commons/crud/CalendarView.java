package com.gzzm.platform.commons.crud;

import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.DateUtils;
import net.cyan.crud.view.*;
import net.cyan.crud.view.components.CDateButton;

import java.text.*;
import java.util.*;

/**
 * 日历视图，通过一个日历来展示一些数据，每个格子表示一个时间区间，显示属于这个区间内的数据
 *
 * @author camel
 * @date 12-3-5
 */
@Service
public abstract class CalendarView<I> extends BaseListCrud<CalendarItemList<I>>
{
    protected CalendarViewType type = CalendarViewType.week;

    /**
     * 时间点，表示显示的日历包括这个时间点
     */
    protected Date time;

    /**
     * 当前选中的日期
     */
    protected Date date;

    /**
     * 日期列表，仅对周视图有效
     */
    protected Date[] dateList;

    protected int startHour;

    protected int endHour = 24;

    public CalendarView()
    {
    }

    public CalendarViewType getType()
    {
        return type;
    }

    public Date getTime()
    {
        return time;
    }

    public void setTime(Date time)
    {
        this.time = time;
    }

    public Date getDate()
    {
        return date;
    }

    public int getStartHour()
    {
        return startHour;
    }

    public void setStartHour(int startHour)
    {
        this.startHour = startHour;
    }

    public int getEndHour()
    {
        return endHour;
    }

    public void setEndHour(int endHour)
    {
        this.endHour = endHour;
    }

    /**
     * 获得一个数据的时间
     *
     * @param i 数据
     * @return 此数据的时间
     */
    protected abstract Date getTime(I i);

    protected void initWeekTime()
    {
        if (date == null)
            date = DateUtils.truncate(time);

        if (type == CalendarViewType.weekday)
            time = date;
        else
            time = DateUtils.getWeekStart(time);

        if (dateList == null)
        {
            if (date == null)
                date = new Date();

            date = DateUtils.truncate(date);

            dateList = new Date[8];

            if (type == CalendarViewType.week)
            {
                dateList[0] = time;
                for (int i = 1; i < 8; i++)
                {
                    dateList[i] = DateUtils.addDate(time, i);
                }
            }
            else
            {
                dateList[3] = date;
                for (int i = 1; i <= 3; i++)
                {
                    dateList[3 - i] = DateUtils.addDate(date, -i);
                }

                for (int i = 1; i <= 4; i++)
                {
                    dateList[3 + i] = DateUtils.addDate(date, i);
                }
            }
        }
    }

    protected void initTime()
    {
        if (time == null)
            time = new Date();

        switch (type)
        {
            case day:
                time = DateUtils.truncate(time);
                if (date == null)
                    date = time;
                break;
            case week:
                initWeekTime();
                break;
            case weekday:
                initWeekTime();
                break;
            default:
                if (date == null)
                    date = DateUtils.truncate(time);
                time = DateUtils.getMonthStart(time);
        }
    }

    public boolean isAutoSelect()
    {
        return type == CalendarViewType.day || type == CalendarViewType.month;
    }

    @Override
    public int getPageSize()
    {
        //不分页
        return 0;
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        initTime();
    }

    protected void loadList() throws Exception
    {
        //默认显示当前时间的日程
        initTime();


        Date startTime;
        Date endTime;

        switch (type)
        {
            case day:
                startTime = time;
                endTime = DateUtils.addDate(startTime, 1);
                break;
            case week:
            case weekday:
                startTime = dateList[0];
                endTime = dateList[7];
                break;
            default:
                startTime = time;
                endTime = DateUtils.getMonthEnd(time);
        }

        setList(loadList(startTime, endTime));
    }

    protected List<CalendarItemList<I>> loadList(Date startTime, Date endTime) throws Exception
    {
        List<I> items = loadItems(startTime, endTime);
        List<CalendarItemList<I>> list = createList();

        if (list == null)
        {
            switch (type)
            {
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
        }
        else
        {
            if (type == CalendarViewType.week || type == CalendarViewType.weekday)
                initWeekList(list, items);
        }

        return list;
    }

    protected List<CalendarItemList<I>> createList() throws Exception
    {
        return null;
    }

    @SuppressWarnings("unchecked")
    protected void initWeekList(List<CalendarItemList<I>> list, List<I> items)
    {
        for (CalendarItemList<I> calendarItemList : list)
        {
            CalendarItem<I>[] calendarItems = new CalendarItem[7];
            for (int i = 0; i < 7; i++)
                calendarItems[i] = new CalendarItem<I>(dateList[i], dateList[i + 1]);
            calendarItemList.setCalendarItems(calendarItems);
        }

        int index = 0;
        //分配每项数据到相应的日历，数据从数据库中加载时已经排序过
        for (I item : items)
        {
            //计算此数据所属的区间，一个数据所属的区别必定不小于前一个数据的区间
            Date itemTime = getTime(item);
            for (; index < 6 && !itemTime.before(dateList[index + 1]); index++)
                ;

            for (CalendarItemList<I> calendarItemList : list)
            {
                if (isItemInCalendarItemList(item, calendarItemList))
                    calendarItemList.getCalendarItems()[index].addItem(item);
            }
        }
    }

    protected boolean isItemInCalendarItemList(I i, CalendarItemList<I> calendarItemList)
    {
        return false;
    }

    protected abstract List<I> loadItems(Date startTime, Date endTime) throws Exception;

    /**
     * 将数据列表转化为日视图
     *
     * @param items 数据列表
     * @param time  日视图某天的开始时间，用于计算每一行的开始时间和结束时间
     * @return 日视图，CalendarItemList列表，每一行代表一个小时
     */
    @SuppressWarnings("unchecked")
    private List<CalendarItemList<I>> toDayList(List<I> items, Date time)
    {
        List<CalendarItemList<I>> list = new ArrayList<CalendarItemList<I>>();

        int index = 0;
        int size = items.size();

        if (startHour > 0)
            time = DateUtils.addHour(time, startHour);

        //24小时，每小时为一行
        for (int hour = startHour; hour < endHour; hour++)
        {
            String s = hour + ":00";
            if (hour < 10)
                s = "0" + s;

            CalendarItemList<I> calendarItemList = new CalendarItemList<I>(s);
            list.add(calendarItemList);

            CalendarItem<I> calendarItem = new CalendarItem<I>(time, time = DateUtils.addHour(time, 1));
            calendarItemList.setCalendarItems(calendarItem);

            //搜索在这个小时内的数据，items已经按时间排好续，只要按顺序搜索即可
            for (; index < size; index++)
            {
                I i = items.get(index);
                Date iTime = getTime(i);

                if (!iTime.before(time))
                    break;

                //日程在这个小时内
                calendarItem.addItem(i);
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
    private List<CalendarItemList<I>> toWeekList(List<I> items, Date time)
    {
        List<CalendarItemList<I>> list = new ArrayList<CalendarItemList<I>>();

        int index = 0;
        int size = items.size();

        //一周七天，每天为一列
        for (int week = 0; week < 7; week++)
        {

            if (week == 0)
            {
                //第一次执行的时候time是0时0分0秒
                if (startHour > 0)
                    time = DateUtils.addHour(time, startHour);
            }
            else
            {
                //新的一天
                if (startHour > 0 || endHour > 0)
                    time = DateUtils.addHour(time, 24 - endHour + startHour);
            }


            //24小时，每小时为一行
            for (int hour = startHour; hour < endHour; hour++)
            {
                CalendarItem calendarItem = new CalendarItem(time, time = DateUtils.addHour(time, 1));

                if (week == 0)
                {
                    String s = hour + ":00";
                    if (hour < 10)
                        s = "0" + s;

                    CalendarItemList<I> calendarItemList = new CalendarItemList(s);
                    list.add(calendarItemList);

                    CalendarItem[] calendarItems = new CalendarItem[7];
                    calendarItems[0] = calendarItem;

                    calendarItemList.setCalendarItems(calendarItems);
                }
                else
                {
                    list.get(hour - startHour).getCalendarItems()[week] = calendarItem;
                }

                //搜索在这个小时内的数据，itmes已经按时间排好续，只要按顺序搜索即可
                for (; index < size; index++)
                {
                    I i = items.get(index);
                    Date iTime = getTime(i);

                    if (!iTime.before(time))
                        break;

                    //日程在这个小时内
                    calendarItem.addItem(i);
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
    private List<CalendarItemList<I>> toMonthList(List<I> items, Date time, Date endTime)
    {
        List<CalendarItemList<I>> list = new ArrayList<CalendarItemList<I>>();

        int index = 0;
        int week = DateUtils.getDay(time) - 1;

        int size = items.size();

        CalendarItem<I>[] calendarItems = null;

        DateFormat format = new SimpleDateFormat("MMM d");

        while (time.before(endTime))
        {
            if (week == 0 || calendarItems == null)
            {
                CalendarItemList<I> calendarItemList = new CalendarItemList<I>();
                list.add(calendarItemList);

                calendarItems = new CalendarItem[7];
                calendarItemList.setCalendarItems(calendarItems);
            }

            //以日期为区间的名称
            CalendarItem calendarItem = new CalendarItem(format.format(time), time, time = DateUtils.addDate(time, 1));
            calendarItems[week] = calendarItem;

            week = (week + 1) % 7;

            //搜索在这天内的数据，items已经按时间排好续，只要按顺序搜索即可
            for (; index < size; index++)
            {
                I i = items.get(index);
                Date iTime = getTime(i);

                if (!iTime.before(time))
                    break;

                //日程在这天
                calendarItem.addItem(i);
            }
        }


        return list;
    }

    protected void initButtons(PageTableView view)
    {
        view.addButton(new CDateButton("转到", "turn(this.date)")).setIcon(Buttons.getIcon("calendar_go"));

        switch (type)
        {
            case day:
            case weekday:
            {
                view.addButton("前一天", "previous()").setIcon(Buttons.getIcon("calendar_previous"));
                view.addButton("后一天", "next()").setIcon(Buttons.getIcon("calendar_next"));

                break;
            }
            case week:
            {
                view.addButton("前一个星期", "previous()").setIcon(Buttons.getIcon("calendar_previous"));
                view.addButton("后一个星期", "next()").setIcon(Buttons.getIcon("calendar_next"));

                break;
            }
            default:
            {
                view.addButton("上一个月", "previous()").setIcon(Buttons.getIcon("calendar_previous"));
                view.addButton("下一个月", "next()").setIcon(Buttons.getIcon("calendar_next"));
            }
        }

        view.addButton("今天", "today()").setIcon(Buttons.getIcon("today"));
    }

    protected String getFirstColumnSize()
    {
        return "80";
    }

    protected PageTableView initView(PageTableView view)
    {
        view.setDocType();
        view.setCellSelectable(true);
        view.setForceFit(true);

        initTime();

        initButtons(view);

        switch (type)
        {
            case day:
            {
                //第一列显示小时
                view.addColumn("时间",
                        new LabelCell(new FieldCell("name").setOrderable(false)).setClass("name")).setWidth("80")
                        .setLocked(true).setAlign(Align.center);

                //第二列显示日程
                view.addColumn(new ObjectColumn(new SimpleDateFormat("MMM dd").format(time), "calendarItems[0]"))
                        .setLocked(true);
                break;
            }
            case week:
            case weekday:
            {
                //第一列显示小时，或者其他的名称
                view.addColumn("", new LabelCell(new FieldCell("name").setOrderable(false)).setClass("name"))
                        .setWidth(getFirstColumnSize()).setLocked(true);

                initWeekTime();

                //一星期七天，每天为一列
                DateFormat format = new SimpleDateFormat("EEE(M/dd)");

                for (int i = 0; i < 7; i++)
                {
                    view.addColumn(new ObjectColumn(format.format(dateList[i]), "calendarItems[" + i + "]")).setLocked(
                            true).setAlign(Align.center);
                }

                break;
            }
            default:
            {
                //一星期七天，每天为一列
                String[] weekdays = DateFormatSymbols.getInstance().getWeekdays();
                for (int i = 0; i < 7; i++)
                {
                    view.addColumn(new ObjectColumn(weekdays[i + 1], "calendarItems[" + i + "]")).setLocked(true)
                            .setAlign(Align.center);
                }
            }
        }

        view.importCss("/platform/commons/calendar.css");
        view.importJs("/platform/commons/calendar.js");

        return view;
    }
}
