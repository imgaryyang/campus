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
 * 校门考勤（人脸识别）
 *
 * @author hmc
 * @date 2018/04/13
 */
@Service
public class FaceAttendanceService extends BaseAttendanceService {
    @Inject
    private ClassesDao classesDao;

    private Integer flag=4;

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    /**
     * 校门考勤首页
     *
     * @return
     * @throws Exception
     */
    @Service(url = "/wx/attendance/face")
    public String toFaceAteendancePage() throws Exception {
        List<Classes> classeses = classesDao.getclassByTeacherId(wxUserOnlineInfo.getIdentifyId());
//        List<Classes> classeses = classesDao.getclassByTeacherId(1032);
        List<Map<String, Object>> maplist = new ArrayList<>();
        for (Classes classes : classeses) {
            Map<String, Object> map = new HashMap<>();
            map.put("Id", classes.getClassesId());
            map.put("name", classes.getGrade() + classes.getClassesName());
            maplist.add(map);
        }
        setList(maplist);
        setType(AttendanceType.FaceAttendance);
        return "/safecampus/wx/attendance/siestaAttendance.ptl";
    }

    /**
     * 开始考勤
     *
     * @return
     * @throws Exception
     */
    @Service(url = "/wx/attendance/faceStudentPage")
    public String faceStudentPage() throws Exception {
        setType(AttendanceType.FaceAttendance);
        setDataMap(AttendanceTools.getStringListMap(classesDao.getStudentsByClassesId(key)));
        return "/safecampus/wx/attendance/siestaStudent.ptl";
    }
}
