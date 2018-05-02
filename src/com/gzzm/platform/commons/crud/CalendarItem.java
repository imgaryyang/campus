package com.gzzm.platform.commons.crud;

import java.util.*;

/**
 * 一个日历项，表示一个时间区间，存放在此时间区间内的数据
 * 用于在日历视图上的一个格子中展示若干数据
 *
 * @author camel
 * @date 12-3-5
 */
public class CalendarItem<I>
{
    /**
     * 区间的名称，仅在月视图中有用
     */
    private String name;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    private List<I> items;

    public CalendarItem()
    {
    }

    public CalendarItem(Date startTime, Date endTime)
    {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public CalendarItem(String name, Date startTime, Date endTime)
    {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Date getStartTime()
    {
        return startTime;
    }

    public void setStartTime(Date startTime)
    {
        this.startTime = startTime;
    }

    public Date getEndTime()
    {
        return endTime;
    }

    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
    }

    public List<I> getItems()
    {
        return items;
    }

    public void setItems(List<I> items)
    {
        this.items = items;
    }

    public void addItem(I item)
    {
        if (items == null)
            items = new ArrayList<I>();

        items.add(item);
    }
}
