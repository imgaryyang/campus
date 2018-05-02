package com.gzzm.safecampus.wx.attendance;

import com.gzzm.platform.commons.Tools;
import com.gzzm.safecampus.campus.attendance.*;
import com.gzzm.safecampus.campus.bus.BusInfo;
import com.gzzm.safecampus.campus.classes.ClassesDao;
import com.gzzm.safecampus.campus.classes.GuardianService;
import com.gzzm.safecampus.campus.classes.Student;
import com.gzzm.safecampus.campus.classes.Teacher;
import com.gzzm.safecampus.wx.user.WxUserOnlineInfo;
import net.cyan.arachne.annotation.Service;
import net.cyan.nest.annotation.Inject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 校巴考勤
 *
 * @author hmc
 * @date 2018/3/27
 */
@Service
public class BusAttendanceCrud {

    @Inject
    private GuardianService guardianService;

    private Integer aoId;

    private List<Attendance> attendances = new ArrayList<Attendance>();

    private Integer relationId;

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
     * 考勤记录详情id
     */
    private Integer attId;

    /**
     * 考勤情况
     */
    private AttendanceStatus attendanceStatus;

    /**
     * 考勤标识
     */
    private Integer state;

    private List<Attendance> lateStudents = new ArrayList<Attendance>();

    private List<Attendance> leaveStudents = new ArrayList<Attendance>();

    private List<Attendance> unsendedStudents = new ArrayList<Attendance>();

    /**
     * 当前日期
     */
    private String currentDate;

    @Inject
    private AttendanceDao attendanceDao;

    @Inject
    private AttendanceService attendanceService;

    @Inject
    private ClassesDao classesDao;

    @Inject
    private WxUserOnlineInfo wxUserOnlineInfo;

    private String name;

    List<AttendanceStatus> status;

    private Integer attstatus;

    private List<Student> studentsUnsended;

    private Integer finishState;

    public BusAttendanceCrud() {
    }

    public Integer getFinishState() {
        return finishState;
    }

    public void setFinishState(Integer finishState) {
        this.finishState = finishState;
    }

    public List<Student> getStudentsUnsended() {
        return studentsUnsended;
    }

    public void setStudentsUnsended(List<Student> studentsUnsended) {
        this.studentsUnsended = studentsUnsended;
    }

    public Integer getAttstatus() {
        return attstatus;
    }

    public void setAttstatus(Integer attstatus) {
        this.attstatus = attstatus;
    }

    public List<AttendanceStatus> getStatus() {
        return status;
    }

    public void setStatus(List<AttendanceStatus> status) {
        this.status = status;
    }

    public Integer getAoId() {
        return aoId;
    }

    public void setAoId(Integer aoId) {
        this.aoId = aoId;
    }

    public Integer getRelationId() {
        return relationId;
    }

    public void setRelationId(Integer relationId) {
        this.relationId = relationId;
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

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
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

    /**
     * 校巴考勤详情
     *
     * @return
     * @throws Exception
     */
    @Service(url = "/wx/attendance/busattendanceDetail")
    public String showDetail() throws Exception {
        //根据aoId获取该次考勤详情
        attendances = attendanceDao.getListByAoId(getAoId());
        for (Attendance attendance : attendances) {
            //获取该次考勤的不同考勤情况的人数统计
            if (attendance.getAttendanceStatus() == AttendanceStatus.Arrived ||attendance.getAttendanceStatus().ordinal()>4) {
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
        return "/safecampus/wx/attendance/attendanceDetail.ptl";
    }

    /**
     * 进入更改校巴考勤状态页面
     *
     * @return
     * @throws Exception
     */
    @Service(url = "/wx/attendance/buschangestate")
    public String changebusState() throws Exception {
        //获取未发送状态的考勤记录
        Tools.log(getAoId()+"=========================");
        unsendedStudents = attendanceDao.getUnsendedAttdance(getAoId(), SendState.Notsended);
        //获取这些学生家长的联系电话
        for (Attendance attendance : unsendedStudents) {
            String phone = guardianService.getGuardianPhone(attendance.getStudent().getStudentId());
            if (phone != null) {
                attendance.getStudent().setPhone(phone);
            }
        }
        //获取未发送消息的学生的数量
        unsendedCount = unsendedStudents.size();
        //标识这是校巴考勤
        state = 1;
        return "/safecampus/wx/attendance/unsendedAttendanceStudent.ptl";
    }

    @Service
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
     * 校巴完成页面
     *
     * @return
     * @throws Exception
     */
    @Service(url = "/wx/attendance/busfinish")
    public String finishAtt() throws Exception {
        DateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        currentDate = sf.format(new java.util.Date());
        //获取老师信息
        Teacher teacher = classesDao.get(Teacher.class, wxUserOnlineInfo.getIdentifyId());
//        Teacher teacher = classesDao.get(Teacher.class, 1032);
        //获取考勤批次
        AttendanceRecord overview = attendanceDao.getBusAttByAoId(getAoId());
        //获取某次考勤的全部考勤详情
        List<Attendance> attendanceList = attendanceDao.getListByAoId(getAoId());
        //统计考勤详情的某些所需信息（正常到达数量、迟到人数、请假人数等）
        studentsUnsended = new ArrayList<>();
        for (Attendance att : attendanceList) {
            if (att.getAttendanceStatus() == AttendanceStatus.AfternoonGetOff || att.getAttendanceStatus() == AttendanceStatus.AfternoonGetOn || att.getAttendanceStatus() == AttendanceStatus.MorningGetOff || att.getAttendanceStatus() == AttendanceStatus.MorningGetOn) {
                arrvedCount++;
            } else if (att.getAttendanceStatus() == AttendanceStatus.Late) {
                lateCount++;
            } else if (att.getAttendanceStatus() == AttendanceStatus.Leave) {
                leaveCount++;
            }
            if (att.getSendState() == SendState.Notsended) {
                unsendedCount++;
                String phone = guardianService.getGuardianPhone(att.getStudent().getStudentId());
                if (phone != null) {
                    att.getStudent().setPhone(phone);
                }
                studentsUnsended.add(att.getStudent());
            }
        }
        //获取校巴信息
        BusInfo busInfo = classesDao.get(BusInfo.class, overview.getRelationId());
        relationId = busInfo.getBusId();
        //标志这是校巴考勤
        state = 1;
        //获取老师的名字和当前的班级
        name = teacher.getTeacherName() + "(" + busInfo.getBusName() + ")";
        //获取要打卡考勤的学生
        List<Student> students = classesDao.getStudentsByBus(overview.getRelationId());
        return "/safecampus/wx/attendance/finishAttendance.ptl";
    }

}
