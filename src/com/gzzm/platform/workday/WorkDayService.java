package com.gzzm.platform.workday;

import com.gzzm.platform.annotation.CacheInstance;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 提供工作日相关操作的服务
 *
 * @author camel
 * @date 2010-5-26
 */
@CacheInstance("WorkDay")
public class WorkDayService
{
    @Inject
    private static Provider<WorkDayService> workDayServiceProvider;


    @Inject
    private static Provider<WorkDayDao> daoProvider;

    /**
     * 缓存所有的工作日
     */
    private List<Date> workdays = new ArrayList<Date>();

    /**
     * 缓存所有的假期
     */
    private List<Date> holidays = new ArrayList<Date>();

    public static WorkDayService getInstance() throws Exception
    {
        return workDayServiceProvider.get();
    }

    public WorkDayService() throws Exception
    {
        for (WorkDay workDay : daoProvider.get().getAllWorkDays())
        {
            if (workDay.getType() == WorkDayType.workday)
                workdays.add(workDay.getWorkDayDate());
            else
                holidays.add(workDay.getWorkDayDate());
        }
    }

    /**
     * 判断某个日期是工作日还是假日
     *
     * @param date   日期
     * @param deptId 业务部门ID
     * @return 工作日类型
     */
    @SuppressWarnings("UnusedDeclaration")
    public WorkDayType getWorkDayType(Date date, Integer deptId)
    {
        if (workdays.indexOf(date) >= 0)
            return WorkDayType.workday;

        if (holidays.indexOf(date) >= 0)
            return WorkDayType.hiloday;

        //星期天和星期六是假日，其他是工作日
        int day = DateUtils.getDay(date);
        return day == 1 || day == 7 ? WorkDayType.hiloday : WorkDayType.workday;
    }

    /**
     * 计算经过若干个工作日之后是哪天
     *
     * @param start  开始时间
     * @param day    工作日数，计算从start开始经过day个工作日后是哪天，day必须大于或等于0
     * @param deptId 业务部门ID
     * @return 计算的结果
     */
    public Date add(Date start, int day, Integer deptId)
    {
        Date result = DateUtils.truncate(start);

        if (day >= 0)
        {
            int count = 0;
            while (count < day)
            {
                result = DateUtils.addDate(result, 1);

                //工作日，加一天
                if (getWorkDayType(result, deptId) == WorkDayType.workday)
                    count++;
            }
        }
        else
        {
            day = -day;
            int count = 0;
            while (count < day)
            {
                result = DateUtils.addDate(result, -1);

                //工作日，加一天
                if (getWorkDayType(result, deptId) == WorkDayType.workday)
                    count++;
            }
        }

        return result;
    }

    /**
     * 计算两个时间之间间隔的工作日数量
     *
     * @param date1  开始时间
     * @param date2  结束时间 必须在date1之后
     * @param deptId 业务部门ID
     * @return 两个时间之间间隔的工作日数，当date1在date2后面的时候，返回负数，否则返回正数
     */
    public int diff(Date date1, Date date2, Integer deptId)
    {
        if (date1.after(date2))
        {
            return -diff(date2, date1, deptId);
        }

        date1 = DateUtils.truncate(date1);
        date2 = DateUtils.truncate(date2);

        Date date = date1;
        int result = 0;

        while (date.before(date2))
        {
            date = DateUtils.addDate(date, 1);

            //为工作日，加上一天
            if (getWorkDayType(date, deptId) == WorkDayType.workday)
                result++;
        }

        return result;
    }
}
