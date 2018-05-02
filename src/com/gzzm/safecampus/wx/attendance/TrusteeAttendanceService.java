package com.gzzm.safecampus.wx.attendance;

import com.gzzm.safecampus.campus.attendance.AttendanceType;
import net.cyan.arachne.annotation.Service;

/**
 * 托管考勤Service
 *
 * @author yiuman
 * @date 2018/4/2
 */
@Service
public class TrusteeAttendanceService extends BaseAttendanceService {

    @Service(url = "/wx/attendance/trustee")
    public String toTrusteePage()throws Exception{
        setList(attendanceDao.getTrusteeInfoByTeacherId(wxUserOnlineInfo.getIdentifyId()));
//        setList(attendanceDao.getTrusteeInfoByTeacherId(1032));、
        setType(AttendanceType.TruAttendance);
        return "/safecampus/wx/attendance/siestaAttendance.ptl";
    }

    @Service(url = "/wx/attendance/trusteestudent")
    public String trusteeStudentPage() throws Exception{
        setType(AttendanceType.TruAttendance);
        setDataMap(AttendanceTools.getStringListMap(attendanceDao.getStudentsBytTrusteeRoomId(key)));
        return "/safecampus/wx/attendance/siestaStudent.ptl";
    }
}
