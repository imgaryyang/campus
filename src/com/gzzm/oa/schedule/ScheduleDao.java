package com.gzzm.oa.schedule;

import com.gzzm.platform.commons.crud.SystemCrudUtils;
import com.gzzm.platform.log.LogAction;
import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.*;

/**
 * 和日常相关的数据操作
 *
 * @author czf
 */
public abstract class ScheduleDao extends GeneralDao
{
    public ScheduleDao()
    {
    }

    public Schedule getSchedule(Integer scheduleId) throws Exception
    {
        return load(Schedule.class, scheduleId);
    }

    @OQL("select s from Schedule s where s.scheduleId in :1")
    public abstract List<Schedule> getSchedules(Integer... scheduleId) throws Exception;

    public void deleteSchedule(Integer scheduleId) throws Exception
    {
        SystemCrudUtils.saveLog(load(Schedule.class, scheduleId), LogAction.delete,null,null);
        delete(Schedule.class, scheduleId);
    }

    /**
     * 查询某个用户在某个时间内的日程
     *
     * @param userId    用户ID
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 日程列表
     * @throws Exception 数据库异常
     */
    @OQL("select s from Schedule s where (creator=:1 and tag=0 or :1 in (select userId from participants) and tag=1 and state<>3)"
            + " and endTime>=?2 and startTime<=?3 order by startTime")
    public abstract List<Schedule> getUserSchedules(Integer userId, Date startTime, Date endTime) throws Exception;

    /**
     * 查询某个部门在某个时间内的日程
     *
     * @param deptIds   部门ID
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 日程列表
     * @throws Exception 数据库异常
     */
    @OQL("select s from Schedule s where deptId in :1 and tag=1 and endTime>=?2 and startTime<=?3 order by startTime")
    public abstract List<Schedule> getDeptSchedules(Collection<Integer> deptIds, Date startTime, Date endTime)
            throws Exception;

    @OQL("select s,[s.participants] from Schedule s where ((exists u in participants : u.userId in :1) and tag=1"
            + " or creator in :1 and tag=0) and endTime>=?2 and startTime<=?3 order by startTime")
    public abstract List<Schedule> getUsersSchedules(Collection<Integer> userIds, Date startTime, Date endTime)
            throws Exception;

    @OQL("select s,[s.participants] from Schedule s where (exists u in participants : u.userId in :1) and tag=1"
            + " and (startTime>=?2 and startTime<?3 or endTime>?2 and endTime<=?3) order by startTime")
    public abstract List<Schedule> getUsersDeptSchedulesInTimes(Collection<Integer> userIds, Date startTime,
                                                                Date endTime) throws Exception;

    /**
     * 查询某时间要提醒的时间
     *
     * @param remindTime 提醒时间
     * @return 日程列表
     * @throws Exception 数据库异常
     */
    @OQL("select s from Schedule s where remindTime=?1")
    public abstract List<Schedule> getRemaindSchdule(Date remindTime) throws Exception;

    @OQL("select scheduleId from Schedule where linkId=:")
    public abstract Integer getScheduleIdByLink(String link) throws Exception;

    @OQLUpdate("delete Schedule where linkId=:")
    public abstract int deleteScheduleByLink(String link) throws Exception;

    @OQL("select s from Schedule s where remindTime>=?1 or remindTime1>=?1")
    public abstract List<Schedule> getShouldRemindSchedules(Date time_start) throws Exception;
}
