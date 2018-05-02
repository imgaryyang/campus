package com.gzzm.portal.cms.commons;

import net.cyan.commons.util.*;

import java.util.*;

/**
 * 定义某个具体的发布周期，包括周期的名字和开始时间
 *
 * @author camel
 * @date 13-12-12
 */
public class PublishPeriodTime implements Value<Date>
{
    private static final long serialVersionUID = 8913930934476652101L;

    /**
     * 周期的类型
     */
    private PublishPeriod period;

    /**
     * 周期的开始时间
     */
    private Date time;

    public PublishPeriodTime(PublishPeriod period, Date time)
    {
        this.period = period;
        this.time = getStartTime(period, time);
    }

    public PublishPeriod getPeriod()
    {
        return period;
    }

    public Date getTime()
    {
        return time;
    }

    @Override
    public String toString()
    {
        switch (period)
        {
            case WEEK:

                Date endTime = DateUtils.getWeekEnd(time);

                return DateUtils.toString(time, "yyyy年第w周(M月d号-") + DateUtils.toString(endTime, "M月d号)");

            case MONTH:

                return DateUtils.toString(time, "yyyy年MM月");

            case QUARTER:

                return DateUtils.getYear(time) + "年" + (DateUtils.getQuarter(time) + 1) + "季度";

            case HALF_YEAR:

                int year = DateUtils.getYear(time);
                int month = DateUtils.getMonth(time);

                if (month < 6)
                    return year + "年上半年";
                else
                    return year + "年下半年";

            case YEAR:

                return DateUtils.toString(time, "yyyy年");
        }

        return null;
    }

    public Date valueOf()
    {
        return time;
    }

    public static Date getStartTime(PublishPeriod period, Date time)
    {
        switch (period)
        {
            case WEEK:

                return DateUtils.getWeekStart(time);

            case MONTH:

                return DateUtils.getMonthStart(time);

            case QUARTER:

                return DateUtils.getQuarterStart(time);

            case HALF_YEAR:

                return DateUtils.getHalfYearStart(time);

            case YEAR:

                return DateUtils.getYearStart(time);
        }

        return null;
    }

    /**
     * 获得可选的周期列表
     *
     * @param period 周期类型
     * @param time   当前时间
     * @return 可选的周期列表
     */
    public static List<PublishPeriodTime> getTimes(PublishPeriod period, Date time)
    {
        if (period == null || period == PublishPeriod.NULL)
            return null;

        if (time == null)
            time = new Date();

        time = getStartTime(period, time);

        List<PublishPeriodTime> times = new ArrayList<PublishPeriodTime>(10);

        for (int i = 0; i < 9; i++)
        {
            times.add(new PublishPeriodTime(period, time));

            switch (period)
            {
                case WEEK:

                    time = DateUtils.addWeek(time, -1);
                    break;

                case MONTH:

                    time = DateUtils.addMonth(time, -1);
                    break;

                case QUARTER:

                    time = DateUtils.addQuarter(time, -1);
                    break;

                case HALF_YEAR:

                    time = DateUtils.addMonth(time, -6);
                    break;

                case YEAR:
                    time = DateUtils.addYear(time, -1);
                    break;
            }
        }


        return times;
    }

    public static List<PublishPeriodTime> getTimes(PublishPeriod period)
    {
        return getTimes(period, null);
    }
}
