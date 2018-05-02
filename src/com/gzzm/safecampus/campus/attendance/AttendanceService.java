package com.gzzm.safecampus.campus.attendance;

import com.gzzm.safecampus.campus.wx.message.AttendanceMsg;
import com.gzzm.safecampus.campus.wx.message.WxTemplateMessage;
import net.cyan.commons.util.DataConvert;
import net.cyan.commons.util.DateUtils;
import net.cyan.nest.annotation.Inject;

import java.util.List;

/**
 * 考勤服务处理类
 *
 * @author Neo
 * @date 2018/4/2 18:28
 */
public class AttendanceService
{
    @Inject
    private AttendanceDao attendanceDao;

    public AttendanceService()
    {
    }

    /**
     * 班级考勤发送考勤消息(仅发送已到，也就是AttendanceStatus为0的记录)
     *
     * @param recordId 考勤记录Id
     * @throws Exception 操作异常
     */
    public void sendAttendanceMsg(Integer recordId, AttendanceStatus status) throws Exception
    {
        AttendanceRecord attendanceRecord = attendanceDao.load(AttendanceRecord.class, recordId);
        List<Attendance> attendances = attendanceDao.getAttendanceByRecordId(recordId, status);
        for (Attendance attendance : attendances)
        {
            AttendanceMsg attendanceMsg = new AttendanceMsg();
            attendanceMsg.setStudentId(attendance.getStudentId());
            attendanceMsg.setStudentName(attendance.getStudent().getStudentName());
            attendanceMsg.setAttendanceStatus(DataConvert.toString(status));
            attendanceMsg.setAttendanceType(DataConvert.toString(attendance.getType()));
            attendanceMsg.setAttendanceTime(DateUtils.toString(attendance.getAttendanceTime()));
            WxTemplateMessage.sendAttendanceMsg(attendanceRecord.getTeacherId(), attendanceMsg);
        }
    }

    /**
     * 考勤消息单发
     *
     * @param attId  考勤详情ID
     * @param status 考勤情况
     */
    public void sendSingleMsg(Integer attId, AttendanceStatus status) throws Exception
    {
        Attendance attendance = attendanceDao.load(Attendance.class, attId);
        AttendanceMsg attendanceMsg = new AttendanceMsg();
        attendanceMsg.setStudentId(attendance.getStudentId());
        attendanceMsg.setStudentName(attendance.getStudent().getStudentName());
        attendanceMsg.setAttendanceStatus(DataConvert.toString(status));
        attendanceMsg.setAttendanceType(DataConvert.toString(attendance.getType()));
        attendanceMsg.setAttendanceTime(DateUtils.toString(attendance.getAttendanceTime()));
        WxTemplateMessage.sendAttendanceMsg(attendance.getTeacherId(), attendanceMsg);
    }
}
