package com.gzzm.safecampus.wx.attendance;

import com.gzzm.safecampus.campus.attendance.AttendanceType;
import com.gzzm.safecampus.campus.classes.Classes;
import com.gzzm.safecampus.campus.classes.ClassesDao;
import net.cyan.arachne.annotation.Service;
import net.cyan.nest.annotation.Inject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 班级考勤
 *
 * @author hmc
 * @date 2018/04/03
 */
@Service
public class ClassAttendanceService extends BaseAttendanceService {

    @Inject
    private ClassesDao classesDao;

    /**
     * 考勤首页
     *
     * @return
     * @throws Exception
     */
    @Service(url = "/wx/attendance/classes")
    public String toClassAteendancePage() throws Exception {
        List<Classes> classeses = classesDao.getclassByTeacherId(wxUserOnlineInfo.getIdentifyId());//TODO
//        List<Classes> classeses = classesDao.getclassByTeacherId(1032);
        List<Map<String, Object>> maplist = new ArrayList<>();
        for (Classes classes : classeses) {
            Map<String, Object> map = new HashMap<>();
            map.put("Id", classes.getClassesId());
            map.put("name", classes.getGrade() + classes.getClassesName());
            maplist.add(map);
        }
        setList(maplist);
        setType(AttendanceType.ClassAttendance);
        return "/safecampus/wx/attendance/siestaAttendance.ptl";
    }

    /**
     * 开始考勤
     *
     * @return
     * @throws Exception
     */
    @Service(url = "/wx/attendance/classesStudentPage")
    public String trusteeStudentPage() throws Exception {
        setType(AttendanceType.ClassAttendance);
        setDataMap(AttendanceTools.getStringListMap(classesDao.getStudentsByClassesId(key)));
        return "/safecampus/wx/attendance/siestaStudent.ptl";
    }
}
