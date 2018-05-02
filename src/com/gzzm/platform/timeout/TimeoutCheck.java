package com.gzzm.platform.timeout;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.workday.WorkDayService;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 超时检查
 *
 * @author camel
 * @date 14-4-3
 */
public class TimeoutCheck
{
    @Inject
    private static Provider<TimeoutService> serviceProvider;

    @Inject
    private static Provider<WorkDayService> workDayServiceProvider;

    private TimeoutService service;

    private WorkDayService workDayService;

    /**
     * 超时配置
     */
    private TimeoutConfigInfo config;

    /**
     * 超时类型ID
     */
    private String typeId;

    /**
     * 开始计时时间
     */
    private Date startTime;

    /**
     * 截止时间，超过此时间则超时
     */
    private Date deadline;

    /**
     * 时限，和dealline只需要一个
     */
    private Integer timeLimit;

    /**
     * 用于判断超时的时间，如果未办结则是当前时间，否则为办结时间
     */
    private Date time;

    private Date endTime;

    private Integer deptId;

    /**
     * 超时的业务记录的ID
     */
    private Long recordId;

    private Object obj;

    public TimeoutCheck()
    {
    }

    void setService(TimeoutService service)
    {
        this.service = service;
    }

    private TimeoutService getService()
    {
        if (service == null)
            service = serviceProvider.get();

        return service;
    }

    private WorkDayService getWorkDayService()
    {
        if (workDayService == null)
            workDayService = workDayServiceProvider.get();

        return workDayService;
    }

    public TimeoutConfigInfo getConfig() throws Exception
    {
        if (config == null)
        {
            if (typeId == null)
                throw new IllegalArgumentException("config and typeId is both null");

            config = getService().getTimeoutConfig(typeId, obj, deptId);
        }

        return config;
    }

    public void setConfig(TimeoutConfigInfo config)
    {
        this.config = config;
    }

    public String getTypeId()
    {
        if (typeId == null)
        {
            if (config == null)
                throw new IllegalArgumentException("config and typeId is both null");

            typeId = config.getTypeId();
        }

        return typeId;
    }

    public void setTypeId(String typeId)
    {
        this.typeId = typeId;
    }

    public Date getStartTime()
    {
        return startTime;
    }

    public void setStartTime(Date startTime)
    {
        this.startTime = startTime;
    }

    public Date getDeadline() throws Exception
    {
        if (deadline == null)
        {
            Integer timeLimit = getTimeLimit();

            if (timeLimit != null)
            {
                switch (getConfig().getUnit())
                {
                    case WORKDAY:
                        deadline = getWorkDayService().add(startTime, timeLimit, deptId);
                        break;
                    case DAY:
                        deadline = DateUtils.addDate(startTime, timeLimit);
                        break;
                    case HOUR:
                        deadline = DateUtils.addHour(startTime, timeLimit);
                        break;
                }
            }
        }

        return deadline;
    }

    public void setDeadline(Date deadline)
    {
        this.deadline = deadline;
    }

    public void setDeadline(java.sql.Date deadline)
    {
        this.deadline = DateUtils.truncate(new Date(deadline.getTime()));
    }

    public Integer getTimeLimit() throws Exception
    {
        if (timeLimit == null)
        {
            TimeoutConfigInfo config = getConfig();
            if (config == null)
                return null;
            timeLimit = config.getTimeLimit();
        }

        return timeLimit;
    }

    public void setTimeLimit(Integer timeLimit)
    {
        this.timeLimit = timeLimit;
    }

    public Date getTime()
    {
        return time;
    }

    public void setTime(Date time)
    {
        this.time = time;
    }

    public Date getEndTime()
    {
        return endTime;
    }

    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public Long getRecordId()
    {
        return recordId;
    }

    public void setRecordId(Long recordId)
    {
        this.recordId = recordId;
    }

    public Object getObj()
    {
        return obj;
    }

    public void setObj(Object obj)
    {
        this.obj = obj;
    }

    public boolean check() throws Exception
    {
        if (deptId == null)
            throw new IllegalArgumentException("deptId is null,recordId:" + recordId);

        if (startTime == null)
            throw new IllegalArgumentException("startTime is null,recordId:" + recordId);

        if (time == null)
            throw new IllegalArgumentException("time is null,recordId:" + recordId);

        if (recordId == null)
            throw new IllegalArgumentException("recordId is null,recordId:" + recordId);

        TimeoutConfigInfo config = getConfig();

        if (config == null)
            return false;

        String typeId = getTypeId();
        Date deadline = getDeadline();

        if (deadline == null)
            return false;

        //没有配置超时，不检查
        if (config == null)
            return false;

        //计算当前时间或结束时间离截至时间还有多少单位时间
        Integer day;
        //计算开始时间离截至时间是多少单位时间
        Integer maxDay;

        switch (config.getUnit())
        {
            case WORKDAY:
                day = getWorkDayService().diff(deadline, time, deptId);
                maxDay = getWorkDayService().diff(startTime, deadline, deptId);
                break;
            case DAY:
                day = DateUtils.getDaysInterval(deadline, time);
                maxDay = DateUtils.getDaysInterval(startTime, deadline);
                break;
            case HOUR:
                day = DateUtils.getHoursInterval(deadline, time);
                if (deadline.after(time))
                    day--;
                maxDay = DateUtils.getHoursInterval(startTime, deadline);
                if (startTime.after(deadline))
                    maxDay--;
                break;
            default:
                day = null;
                maxDay = null;

        }

        TimeoutDao dao = getService().getDao();

        //当前起作用的那个超时记录
        Timeout validTimeout = null;

        //新记录的timeout
        List<Timeout> timeouts = null;

        //原来数据库里的timeout，用来做比较
        List<Timeout> timeouts0 = null;

        //对每个警告级别做判断
        for (TimeoutLevel level : dao.getLevelLists())
        {
            Integer levelId = level.getLevelId();
            Integer levelDay = config.getDay(levelId);

            if (levelDay != null)
            {
                //达到了此警告级别
                Timeout timeout0 = dao.getTimeout(typeId, recordId, levelId, startTime);

                if (day >= levelDay && (levelDay > 0 || maxDay > levelDay))
                {
                    Timeout timeout = new Timeout();
                    timeout.setRecordId(recordId);
                    timeout.setLevelId(levelId);
                    timeout.setTypeId(typeId);
                    timeout.setStartTime(startTime);
                    timeout.setDeptId(deptId);

                    Date timeoutTime;
                    switch (config.getUnit())
                    {
                        case WORKDAY:
                            timeoutTime = workDayService.add(deadline, levelDay, deptId);
                            break;
                        case DAY:
                            timeoutTime = DateUtils.addDate(deadline, levelDay);
                            break;
                        case HOUR:
                            timeoutTime = DateUtils.addHour(deadline, levelDay);
                            break;
                        default:
                            timeoutTime = null;

                    }

                    timeout.setTimeoutTime(timeoutTime);
                    timeout.setEndTime(endTime);
                    timeout.setDeadline(deadline);
                    timeout.setUnit(config.getUnit());
                    timeout.setDay(levelDay);

                    validTimeout = timeout;

                    if (timeouts == null)
                    {
                        timeouts = new ArrayList<Timeout>();
                        timeouts0 = new ArrayList<Timeout>();
                    }
                    timeouts.add(timeout);
                    timeouts0.add(timeout0);
                }
                else if (timeout0 != null)
                {
                    if (timeouts == null)
                    {
                        timeouts = new ArrayList<Timeout>();
                        timeouts0 = new ArrayList<Timeout>();
                    }
                    timeouts.add(null);
                    timeouts0.add(timeout0);
                }
            }
        }

        boolean result = false;

        if (timeouts != null)
        {
            for (int i = 0, n = timeouts.size(); i < n; i++)
            {
                Timeout timeout = timeouts.get(i);
                Timeout timeout0 = timeouts0.get(i);

                boolean bak = false;
                if (timeout != null)
                {
                    timeout.setValid(timeout == validTimeout);
                    if (timeout0 == null)
                    {
                        Tools.log("record timeout,typeId:" + typeId + ",recordId:" + recordId + ",level:" +
                                timeout.getLevelId());
                        dao.add(timeout);
                        result = true;
                    }
                    else if (!timeout.equals1(timeout0))
                    {
                        bak = true;
                        timeout.setRecordTime(new Date());
                        timeout.setTimeoutId(timeout0.getTimeoutId());
                        dao.update(timeout);
                    }
                }
                else if (timeout0 != null)
                {
                    dao.delete(timeout0);
                    bak = true;
                    Tools.log("cancel timeout,typeId:" + typeId + ",recordId:" + recordId + ",level:" +
                            timeout0.getLevelId());
                }

                if (bak)
                {
                    TimeoutBak timeoutBak = new TimeoutBak();
                    timeoutBak.setTimeoutId(timeout0.getTimeoutId());
                    timeoutBak.setTypeId(timeout0.getTypeId());
                    timeoutBak.setRecordId(timeout0.getRecordId());
                    timeoutBak.setStartTime(timeout0.getStartTime());
                    timeoutBak.setDeadline(timeout0.getDeadline());
                    timeoutBak.setTimeoutTime(timeout0.getTimeoutTime());
                    timeoutBak.setLevelId(timeout0.getLevelId());
                    timeoutBak.setDay(timeout0.getDay());
                    timeoutBak.setValid(timeout0.getValid());
                    timeoutBak.setDeptId(timeout0.getDeptId());
                    timeoutBak.setRecordTime(timeout0.getRecordTime());
                    timeoutBak.setBakTime(new Date());

                    dao.add(timeoutBak);
                }
            }
        }

        return result;
    }
}
