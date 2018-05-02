package com.gzzm.safecampus.wx.attendance;

import com.gzzm.safecampus.campus.attendance.*;
import com.gzzm.safecampus.campus.bus.BusInfo;
import com.gzzm.safecampus.campus.bus.BusRoute;
import com.gzzm.safecampus.campus.classes.*;
import com.gzzm.safecampus.campus.siesta.SiestaDao;
import com.gzzm.safecampus.campus.siesta.SiestaRoom;
import com.gzzm.safecampus.campus.trusteeship.TrusteeshipRoom;
import com.gzzm.safecampus.wx.user.WxUserOnlineInfo;
import net.cyan.arachne.annotation.Service;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 班级考勤:
 *
 * @author hmc
 * @date 2018/03/21
 */
@Service
public class ClassAttendanceCrud {

    @Inject
    private GuardianService guardianService;

    private Integer aoId;

    private List<Attendance> attendances = new ArrayList<Attendance>();

    /**
     * 状态标识
     */
    private Integer state;

    /**
     * 迟到人数
     */
    private Integer lateCount = 0;

    /**
     * 请假人数
     */
    private Integer leaveCount = 0;

    /**
     * 已到人数
     */
    private Integer arrvedCount = 0;

    /**
     * 未发送人数
     */
    private Integer unsendedCount = 0;

    /**
     * 班级集合
     */
    private List<Classes> classeses;

    /**
     * 关系id
     */
    private Integer relationId;

    /**
     * 考勤详情id
     */
    private Integer attId;

    /**
     * 考勤情况
     */
    private AttendanceStatus attendanceStatus;

    private List<Attendance> lateStudents = new ArrayList<Attendance>();

    private List<Attendance> leaveStudents = new ArrayList<Attendance>();

    private List<Attendance> unsendedStudents = new ArrayList<Attendance>();

    private String currentDate;

    private List<Student> studentsUnsended;

    @Inject
    private AttendanceDao attendanceDao;

    @Inject
    private ClassesDao classesDao;

    @Inject
    private WxUserOnlineInfo wxUserOnlineInfo;

    @Inject
    private AttendanceService attendanceService;

    @Inject
    private SiestaDao siestaDao;

    private String name;

    private Integer finishState;

    private Integer type;

    public List<Student> getStudentsUnsended() {
        return studentsUnsended;
    }

    public void setStudentsUnsended(List<Student> studentsUnsended) {
        this.studentsUnsended = studentsUnsended;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getFinishState() {
        return finishState;
    }

    public void setFinishState(Integer finishState) {
        this.finishState = finishState;
    }

    public Integer getAoId() {
        return aoId;
    }

    public void setAoId(Integer aoId) {
        this.aoId = aoId;
    }

    public List<Attendance> getAttendances() {
        return attendances;
    }

    public void setAttendances(List<Attendance> attendances) {
        this.attendances = attendances;
    }

    public Integer getLateCount() {
        return lateCount;
    }

    public void setLateCount(Integer lateCount) {
        this.lateCount = lateCount;
    }

    public Integer getLeaveCount() {
        return leaveCount;
    }

    public void setLeaveCount(Integer leaveCount) {
        this.leaveCount = leaveCount;
    }

    public Integer getArrvedCount() {
        return arrvedCount;
    }

    public void setArrvedCount(Integer arrvedCount) {
        this.arrvedCount = arrvedCount;
    }

    public Integer getUnsendedCount() {
        return unsendedCount;
    }

    public void setUnsendedCount(Integer unsendedCount) {
        this.unsendedCount = unsendedCount;
    }

    public List<Classes> getClasseses() {
        return classeses;
    }

    public void setClasseses(List<Classes> classeses) {
        this.classeses = classeses;
    }

    public Integer getRelationId() {
        return relationId;
    }

    public void setRelationId(Integer relationId) {
        this.relationId = relationId;
    }

    public List<Attendance> getLateStudents() {
        return lateStudents;
    }

    public void setLateStudents(List<Attendance> lateStudents) {
        this.lateStudents = lateStudents;
    }

    public List<Attendance> getLeaveStudents() {
        return leaveStudents;
    }

    public void setLeaveStudents(List<Attendance> leaveStudents) {
        this.leaveStudents = leaveStudents;
    }

    public List<Attendance> getUnsendedStudents() {
        return unsendedStudents;
    }

    public void setUnsendedStudents(List<Attendance> unsendedStudents) {
        this.unsendedStudents = unsendedStudents;
    }

    public AttendanceStatus getAttendanceStatus() {
        return attendanceStatus;
    }

    public void setAttendanceStatus(AttendanceStatus attendanceStatus) {
        this.attendanceStatus = attendanceStatus;
    }

    public Integer getAttId() {
        return attId;
    }

    public void setAttId(Integer attId) {
        this.attId = attId;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    /**
     * 考勤详情
     *
     * @return
     * @throws Exception
     */
    @Service(url = "/wx/attendance/attendanceDetail")
    public String showDetail() throws Exception {
        //根据aoid获取该次的全部考勤详情
        attendances = AttendanceTools.getSortList(attendanceDao.getListByAoId(getAoId()));
        //统计该次考勤的相关信息
        for (Attendance attendance : attendances) {
            if (attendance.getAttendanceStatus() == AttendanceStatus.Arrived || attendance.getAttendanceStatus().ordinal() > 4) {
                arrvedCount++;
            } else if (attendance.getAttendanceStatus() == AttendanceStatus.Late) {
                lateCount++;
                lateStudents.add(attendance);
            } else if (attendance.getAttendanceStatus() == AttendanceStatus.Leave) {
                leaveCount++;
                leaveStudents.add(attendance);
            }
            if (attendance.getSendState() == SendState.Notsended) {
                unsendedStudents.add(attendance);
            }
        }
        AttendanceRecord record = attendanceDao.get(AttendanceRecord.class, getAoId());
        type = record.getType().ordinal();
        return "/safecampus/wx/attendance/attendanceDetail.ptl";
    }

    /**
     * 进入更改状态页面
     *
     * @return
     * @throws Exception
     */
    @Service(url = "/wx/attendance/changestate")
    public String changeState() throws Exception {
        if (getAoId() == null) {
            aoId = attendanceDao.getByClassesId(getRelationId());
        }
        AttendanceRecord record = attendanceDao.get(AttendanceRecord.class, aoId);
        relationId = record.getRelationId();
        type = record.getType().ordinal();
        unsendedStudents = attendanceDao.getUnsendedAttdance(getAoId(), SendState.Notsended);
        unsendedCount = unsendedStudents.size();
        for (Attendance attendance : unsendedStudents) {
            attendance.getStudent().setPhone(guardianService.getGuardianPhone(attendance.getStudent().getStudentId()));
        }
        state = 0;
        return "/safecampus/wx/attendance/unsendedAttendanceStudent.ptl";
    }

    /**
     * 保存更改的考勤状态
     *
     * @param status
     * @throws Exception
     */
    @Service(url = "/wx/attendance/saveRecord")
    public void saveRecord(Integer status) throws Exception {
        Attendance attendance = attendanceDao.get(Attendance.class, getAttId());
        attendance.setAttendanceStatus(getAttendanceStatus());
        attendance.setSendState(SendState.Sended);
        attendanceDao.save(attendance);
        if (status == 1) {
            //发送消息操作
            attendanceService.sendSingleMsg(attendance.getAttId(), attendance.getAttendanceStatus());
        }
    }

    /**
     * 考勤完成
     *
     * @return
     * @throws Exception
     */
    @Service(url = "/wx/attendance/finish")
    public String finishAtt() throws Exception {
        DateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        currentDate = sf.format(new java.util.Date());
        Teacher teacher = classesDao.get(Teacher.class, wxUserOnlineInfo.getIdentifyId());
//        Teacher teacher = classesDao.get(Teacher.class, 1032);
        AttendanceRecord overview = attendanceDao.getByAoId(getAoId());
        List<Attendance> attendanceList = attendanceDao.getListByAoId(getAoId());
        studentsUnsended = new ArrayList<>();
        for (Attendance att : attendanceList) {
            if (att.getAttendanceStatus() == AttendanceStatus.Arrived || att.getAttendanceStatus().ordinal() > 4) {
                arrvedCount++;
            } else if (att.getAttendanceStatus() == AttendanceStatus.Late) {
                lateCount++;
            } else if (att.getAttendanceStatus() == AttendanceStatus.Leave) {
                leaveCount++;
            }
            if (att.getSendState() == SendState.Notsended) {
                unsendedCount++;
                att.getStudent().setPhone(guardianService.getGuardianPhone(att.getStudent().getStudentId()));
                studentsUnsended.add(att.getStudent());
            }
        }
        if (overview.getType() == AttendanceType.ClassAttendance || overview.getType() == AttendanceType.FaceAttendance) {
            Classes classes = classesDao.get(Classes.class, overview.getRelationId());
            name = teacher.getTeacherName() + "(" + classes.getGrade() + classes.getClassesName() + ")";
        } else if (overview.getType() == AttendanceType.SiestaAttendance) {
            SiestaRoom siestaRoom = siestaDao.get(SiestaRoom.class, overview.getRelationId());
            name = teacher.getTeacherName() + "(" + siestaRoom.getName() + ")";
        } else if (overview.getType() == AttendanceType.TruAttendance) {
            TrusteeshipRoom trusteeshipRoom = siestaDao.get(TrusteeshipRoom.class, overview.getRelationId());
            name = teacher.getTeacherName() + "(" + trusteeshipRoom.getName() + ")";
        } else if (overview.getType() == AttendanceType.BusAttendance) {
            BusInfo busInfo = classesDao.get(BusInfo.class, overview.getRelationId());
            BusRoute busRoute = classesDao.get(BusRoute.class, busInfo.getRouteId());
            name = teacher.getTeacherName() + "(" + busRoute.getRouteName() + ")";
        }
        relationId = overview.getRelationId();
        if (overview.getType() == AttendanceType.ClassAttendance) {
            state = 0;
        } else if (overview.getType() == AttendanceType.SiestaAttendance) {
            state = 2;
        } else if (overview.getType() == AttendanceType.TruAttendance) {
            state = 3;
        } else if (overview.getType() == AttendanceType.FaceAttendance) {
            state = 4;
        } else if (overview.getType() == AttendanceType.BusAttendance) {
            state = 1;
        }
        //获取老师的名字和当前的班级
        classeses = classesDao.getclassByTeacherId(wxUserOnlineInfo.getIdentifyId());
//        classeses = classesDao.getclassByTeacherId(1032);
        return "/safecampus/wx/attendance/finishAttendance.ptl";
    }
}
