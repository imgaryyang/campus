package com.gzzm.safecampus.wx.attendance;

import com.gzzm.safecampus.campus.attendance.AttendanceType;
import net.cyan.arachne.annotation.Service;

/**
 * 午休考勤
 *
 * @author yiuman
 * @date 2018/3/30
 */
@Service
public class SiestaAttendanceService extends BaseAttendanceService{


    @Service(url = "/wx/attendance/siesta")
    public String toSiestaPage() throws Exception{
        setList(attendanceDao.getSiertaInfoByTeacherId(wxUserOnlineInfo.getIdentifyId()));//TODO
//        setList(attendanceDao.getSiertaInfoByTeacherId(84));
        setType(AttendanceType.SiestaAttendance);
        return "/safecampus/wx/attendance/siestaAttendance.ptl";
    }

    @Service(url = "/wx/attendance/siestastudent")
    public String siestaStudentPage() throws Exception{
        setType(AttendanceType.SiestaAttendance);
        setDataMap(AttendanceTools.getStringListMap(attendanceDao.getStudentsBySiestaRoomId(key)));
        return "/safecampus/wx/attendance/siestaStudent.ptl";
    }

}
