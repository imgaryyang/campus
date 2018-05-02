package com.gzzm.safecampus.campus.bus;

import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.annotation.OQLUpdate;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.Date;
import java.util.List;

/**
 * @author czy
 */
public abstract class BusScheduleDao extends GeneralDao
{
    public BusScheduleDao()
    {
    }

    @OQL("select s from BusSchedule s where s.deptId=:1 and s.busId=?2 and scheduleTime>=?3 and scheduleTime<=?4 and teacher.teacherName like ?5 order by scheduleTime")
    public abstract List<BusSchedule> getBusSchedules(Integer deptId, Integer busId, java.util.Date startTime, java.util.Date endTime, String teacherName) throws Exception;

    @OQL("select s.busId,min(s.busInfo.busName) as busName,s.teacherId,min(s.teacher.teacherName) as teacherName from BusSchedule s where s.busId=?1 and s.scheduleTime>=?2 and s.scheduleTime<=?3 and s.deptId=:4 and s.teacher.teacherName like ?5 group by s.busId,s.teacherId")
    public abstract List<BusTeacher> getBusTeachers(Integer busId, java.util.Date startTime, java.util.Date endTime, Integer deptId, String teacherName) throws Exception;

    @OQL("select s.busId from BusInfo s where busName=:1 and deptId=:2")
    public abstract Integer getBusIdByBusLicense(String busLicense, Integer deptId);

    @OQL("select s.teacherId from Teacher s where teacherNo=:1 and teacherName=:2 and  deptId=:3")
    public abstract Integer getTeacherId(String teacherNo, String teacherName, Integer deptId) throws Exception;

    @OQL("select s from BusSchedule s where teacherId = ?1 and busId=?2 and scheduleTime=?3")
    public abstract BusSchedule getBusScheduleByTeaDate(Integer teacherId, Integer busId, Date scheduleTime) throws Exception;

    @OQLUpdate("delete from BusSchedule s where s.busId=:1 and s.teacherId=:2 and s.scheduleTime>=:3 and scheduleTime<=:4")
    public abstract Integer deleteSchedule(Integer busId, Integer teacherId, Date startTime, Date endTime) throws Exception;

    /**
     * 是否存在排班
     *
     * @return 是否存在
     * @throws Exception 操作异常
     */
    public boolean existsSchedule(Integer busId, Integer teacherId, Date startTime, Date endTime) throws Exception
    {
        return oqlExists(
                "exists BusSchedule where busId=:1 and teacherId =:2 and scheduleTime>=?3 and scheduleTime<=?4",
                busId, teacherId, startTime, endTime);
    }
}
