package com.gzzm.platform.commons.crud;

import java.util.Date;

/**
 * 多个CalendarItem组成的集合，代表日历视图上的一行
 *
 * @author camel
 * @date 12-3-5
 */
public class CalendarItemList<I>
{
    /**
     * 这一列数据的ID，可以是空的
     */
    private Object id;

    /**
     * 日历列表的名称，在日历视图里表现为一行的名称
     */
    private String name;

    /**
     * 这一行里的日常列表
     */
    private CalendarItem<I>[] calendarItems;

    public CalendarItemList()
    {
    }

    public CalendarItemList(String name)
    {
        this.name = name;
    }

    public CalendarItemList(Object id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public Object getId()
    {
        return id;
    }

    public void setId(Object id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public CalendarItem<I>[] getCalendarItems()
    {
        return calendarItems;
    }

    public void setCalendarItems(CalendarItem<I>... calendarItems)
    {
        this.calendarItems = calendarItems;
    }

    public void add(I i, Date time)
    {
        for (CalendarItem<I> calendarItem : calendarItems)
        {
            if (time.before(calendarItem.getEndTime()))
                calendarItem.addItem(i);
        }
    }

    @SuppressWarnings("unchecked")
    public void createCalendarItems(Date[] times)
    {
        int n = times.length - 1;

        calendarItems = new CalendarItem[n];

        for (int i = 0; i < n; i++)
        {
            CalendarItem calendarItem = new CalendarItem();
            calendarItem.setStartTime(times[i]);
            calendarItem.setEndTime(times[i + 1]);

            calendarItems[i] = calendarItem;
        }
    }
}
