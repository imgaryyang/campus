package com.gzzm.safecampus.wx.attendance;

import com.gzzm.safecampus.campus.attendance.AttendanceType;
import com.gzzm.safecampus.campus.bus.BusInfo;
import com.gzzm.safecampus.campus.bus.BusRoute;
import com.gzzm.safecampus.campus.bus.BusRouteDao;
import com.gzzm.safecampus.campus.classes.Classes;
import com.gzzm.safecampus.campus.classes.Student;
import net.cyan.arachne.annotation.Service;
import net.cyan.nest.annotation.Inject;

import java.util.List;
import java.util.Map;

/**
 * 校巴考勤
 *
 * @author hmc
 * @date 2018/04/03
 */
@Service
public class BusAttendanceService extends BaseAttendanceService {

    @Inject
    private BusRouteDao busRouteDao;


    @Service(url = "/wx/attendance/busroute")
    public String toClassAteendancePage() throws Exception {
        List<Map<String,Object>>  busInfos = busRouteDao.getBusInfos(wxUserOnlineInfo.getDeptId());
//        List<Map<String,Object>>  busInfos = busRouteDao.getBusInfos(184);
        setList(busInfos);
        setType(AttendanceType.BusAttendance);
        return "/safecampus/wx/attendance/siestaAttendance.ptl";
    }

    @Service(url = "/wx/attendance/busStudentPage")
    public String trusteeStudentPage() throws Exception {
        setType(AttendanceType.BusAttendance);
        setDataMap(AttendanceTools.getStringListMap(classesDao.getStudentsByBus(key)));
        return "/safecampus/wx/attendance/siestaStudent.ptl";
    }


}
