package com.gzzm.safecampus.wx.attendance;

import com.gzzm.platform.commons.Tools;
import com.gzzm.safecampus.campus.attendance.*;
import com.gzzm.safecampus.campus.bus.BusDao;
import com.gzzm.safecampus.campus.bus.BusStudent;
import com.gzzm.safecampus.campus.bus.BusTeacher;
import com.gzzm.safecampus.campus.bus.ScheduleType;
import com.gzzm.safecampus.campus.classes.ClassesDao;
import com.gzzm.safecampus.campus.classes.Student;
import com.gzzm.safecampus.campus.classes.StudentDao;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.transaction.Transactional;
import net.cyan.nest.annotation.Inject;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 打卡考勤
 *
 * @author hmc
 * @date 2018/03/29
 */
@Service
public class BusCardService {

    @Inject
    private StudentDao studentDao;

    @Inject
    private ClassesDao classesDao;

    @Inject
    private AttendanceDao attendanceDao;

    @Inject
    private AttendanceService attendanceService;

    @Inject
    private BusDao busDao;

    private static String mstart = "morning_start";

    private static String mend = "morning_end";

    private static String aend = "afternoon_end";

    private static String astart = "afternoon_start";

    @Transactional
    public void saveAttendance(Integer studentId, Date date) throws Exception {
        BusStudent busStudent = studentDao.getByStuId(studentId);//获取校巴ID
        Student student = classesDao.load(Student.class, studentId);
        if (busStudent == null || student == null) {
            return;
        }
        //根据时间和记录判断是什么考勤
        java.sql.Date date1 = new java.sql.Date(new Date().getTime());//获取当前时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);
        Integer hour = calendar.get(Calendar.HOUR_OF_DAY);
        //判断是不是上午的打卡
        if (hour <= Integer.valueOf(Tools.getMessage(mend)) && hour >= Integer.valueOf(Tools.getMessage(mstart))) {
            List<AttendanceRecord> attendanceList = getAttdentList(date1, busStudent.getDeptId(), Integer.valueOf(Tools.getMessage(mend)), Integer.valueOf(Tools.getMessage(mstart)));
            //上午尚未有校巴考勤
            if (attendanceList == null || attendanceList.size() == 0) {
                AttendanceStatus attendanceStatus = AttendanceStatus.MorningGetOn;
                String batch = "上午上车";
                toInsertAttendance(studentId, date, busStudent, batch, ScheduleType.MORNING, attendanceStatus);//新增考勤
            } else if (attendanceList.size() == 1) {
                AttendanceStatus status = AttendanceStatus.MorningGetOn;
                ScheduleType type = ScheduleType.MORNING;
                updateOrInsertAttendance(studentId, date, busStudent, attendanceList, status, type);
            } else if (attendanceList.size() == 2) {
                AttendanceStatus status = AttendanceStatus.MorningGetOff;
                updateAttendace(studentId, date, attendanceList, status);
            }
        }
        //判断是不是下午的打卡
        else if (hour >= Integer.valueOf(Tools.getMessage(astart)) && hour <= Integer.valueOf(Tools.getMessage(aend))) {
            List<AttendanceRecord> attendanceList = getAttdentList(date1, busStudent.getDeptId(), Integer.valueOf(Tools.getMessage(aend)), Integer.valueOf(Tools.getMessage(astart)));
            if (attendanceList == null || attendanceList.size() == 0) {
                AttendanceStatus attendanceStatus = AttendanceStatus.AfternoonGetOn;
                String batch = "下午上车";
                toInsertAttendance(studentId, date, busStudent, batch, ScheduleType.NIGHT, attendanceStatus);
            } else if (attendanceList.size() == 1) {
                AttendanceStatus status = AttendanceStatus.AfternoonGetOn;
                ScheduleType type = ScheduleType.NIGHT;
                updateOrInsertAttendance(studentId, date, busStudent, attendanceList, status, type);
            } else if (attendanceList.size() == 2) {
                AttendanceStatus status = AttendanceStatus.AfternoonGetOff;
                updateAttendace(studentId, date, attendanceList, status);
            }
        }

    }

    /**
     * 更新打卡详情并发送消息
     *
     * @param studentId      打卡学生id
     * @param date           考勤时间
     * @param attendanceList 考勤总记录集合
     * @param status         考勤情况
     * @throws Exception
     */
    private void updateAttendace(Integer studentId, Date date, List<AttendanceRecord> attendanceList, AttendanceStatus status) throws Exception {
        Integer aoId=0;
        for (AttendanceRecord record : attendanceList){
            if (record.getAttendanceBatch().contains("下车")){
                aoId=record.getAoId();
            }
        }
        Attendance attendance = attendanceDao.getByStudentIdAndAoIdAndType(studentId, aoId, AttendanceType.BusAttendance);
        if (attendance == null) {
            return;
        }
        //判断该学生是否已经发送过考勤消息
        if (attendance.getSendState() == SendState.Sended) {
            return;
        } else {
            updateAndSendMsg(date, status, attendance);
        }
    }

    /**
     * 根据打卡记录新增或更新考勤记录
     *
     * @param studentId      打卡学生id
     * @param date           考勤时间
     * @param busStudent     校巴学生
     * @param attendanceList 打卡总记录集合
     * @param status         考勤状态
     * @param type           老师排班类型
     * @throws Exception
     */
    private void updateOrInsertAttendance(Integer studentId, Date date, BusStudent busStudent, List<AttendanceRecord> attendanceList, AttendanceStatus status, ScheduleType type) throws Exception {
        Attendance attendance = attendanceDao.getByStudentIdAndAoIdAndType(studentId, attendanceList.get(0).getAoId(), AttendanceType.BusAttendance);
        //判断该学生是否已经发送过考勤消息
        if (attendance == null) {
            return;
        }
        String batch = "";
        //如果该学生已打卡
        if (attendance.getSendState() == SendState.Sended) {
            long min = (date.getTime() - attendance.getAttendanceTime().getTime()) / 60000;
            //之前打的卡如果跟这次打的卡时间间隔小于三分钟，则不做处理
            if (min < Integer.valueOf(Tools.getMessage("min_interval"))) {
                return;
            }
            //第二次打卡比第一次打卡间隔大于三分钟，那么，记录新的打卡总记录，并记录打卡详情
            else {
                //如果第一次打卡是上午上车
                if (status == AttendanceStatus.MorningGetOn) {
                    status = AttendanceStatus.MorningGetOff;//第二次打卡状态更改为上午下车，下面同理
                    batch = "上午下车";
                } else if (status == AttendanceStatus.MorningGetOff) {
                    batch = "上午下车";
                } else if (status == AttendanceStatus.AfternoonGetOn) {
                    status = AttendanceStatus.AfternoonGetOff;
                    batch = "下午下车";
                } else if (status == AttendanceStatus.AfternoonGetOff) {
                    batch = "下午下车";
                }
                toInsertAttendance(studentId, date, busStudent, batch, type, status);
            }
        }
        //如果该学生未打卡
        else {
            //更新学生打卡记录，并发送消息
            updateAndSendMsg(date, status, attendance);
        }
    }

    /**
     * 更新打卡详情，并发送消息
     *
     * @param date       考勤时间
     * @param status     考勤情况
     * @param attendance 需要更新的考勤详情
     * @throws Exception
     */
    private void updateAndSendMsg(Date date, AttendanceStatus status, Attendance attendance) throws Exception {
        attendance.setAttendanceTime(date);
        attendance.setAttendanceStatus(status);
        attendance.setSendState(SendState.Sended);
        attendanceDao.update(attendance);
        //发送考勤消息
        attendanceService.sendSingleMsg(attendance.getAttId(), status);
    }

    /**
     * 构建考勤总记录实体
     *
     * @param date       考勤时间
     * @param busStudent 校巴学生
     * @return 返回考勤总表记录实体
     */
    private AttendanceRecord getAttendanceRecord(Date date, BusStudent busStudent) {
        AttendanceRecord attRecord = new AttendanceRecord();
        attRecord.setRelationId(busStudent.getBusId());//校巴id（关联id）
        attRecord.setDate(date);//考勤时间
        attRecord.setType(AttendanceType.BusAttendance);//考勤类型
        attRecord.setDeptId(busStudent.getDeptId());//部门id
        return attRecord;
    }

    /**
     * 获取学校某个时间段内的校巴考勤总记录
     *
     * @param currDate
     * @param deptId
     * @param hour1
     * @param hour2
     * @return
     */
    private List<AttendanceRecord> getAttdentList(java.sql.Date currDate, Integer deptId, Integer hour1, Integer hour2) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currDate);//设置当前时间
        calendar.set(Calendar.HOUR_OF_DAY, hour1);//设置小时
        calendar.set(Calendar.MINUTE, 0);//设置分钟
        calendar.set(Calendar.SECOND, 0);//设置秒
        java.util.Date date1 = calendar.getTime();//获取设置后的时间
        calendar.set(Calendar.HOUR_OF_DAY, hour2);//重新设置小时
        java.util.Date date2 = calendar.getTime();//获取重置后的时间
        return attendanceDao.getByDateAndStuId(date1, date2, deptId);
    }

    /**
     * 获取考勤详情实体
     *
     * @param date       考勤时间
     * @param busStudent 校巴学生
     * @param type       老师排班类型
     * @param aoId       考勤总记录id（批次id）
     * @return 返回考勤详情实体
     */
    private Attendance getAttendance(Date date, BusStudent busStudent, ScheduleType type, Integer aoId) {
        Attendance attendance = new Attendance();
        attendance.setType(AttendanceType.BusAttendance);//类型为校巴考勤
        attendance.setAttendanceTime(date);//打卡时间
        attendance.setStudentId(busStudent.getStudentId());//记录学生ID
        attendance.setDeptId(busStudent.getDeptId());//记录部门ID
        java.sql.Date date1 = new java.sql.Date(date.getTime());
        Integer teacherId = busDao.getTeacherIdByBusId(busStudent.getBusId(), date1, type);//获取考勤老师d
        if (teacherId == null) {
            teacherId = busDao.getTeacherIdByBusId(busStudent.getBusId(), date1, ScheduleType.ALLDAY);
        }
        attendance.setTeacherId(teacherId);//记录考勤老师id
        attendance.setAoId(aoId);//记录总记录id
        attendance.setSiteId(busStudent.getSiteId());//记录考勤地点
        attendance.setRelationId(busStudent.getBusId());//记录校巴id（关联id）
        return attendance;
    }

    /**
     * 新增考勤
     *
     * @param studentId  学生id
     * @param date       考勤时间
     * @param busStudent 校巴学生记录
     * @param batch      考勤批次标识
     * @param type       老师排班类型
     * @param status     考勤情况
     * @throws Exception
     */
    @Transactional
    private void toInsertAttendance(Integer studentId, Date date, BusStudent busStudent, String batch, ScheduleType type, AttendanceStatus status) throws Exception {
        Integer attId = 0;
        AttendanceRecord attRecord = getAttendanceRecord(date, busStudent);//获取考勤总记录
        attRecord.setAttendanceBatch(batch);//记录考勤批次标识
        attendanceDao.save(attRecord);//保存总记录
        List<BusStudent> busStudents = studentDao.getStudentIdsByBusId(busStudent.getBusId());//获取该校巴的全部学生
        for (BusStudent bs : busStudents) {
            Attendance attendance = getAttendance(date, bs, type, attRecord.getAoId());//获取考勤详情
            //判断校巴中的该学生是否是本次打卡的学生
            if (studentId.toString().equals(bs.getStudentId().toString())) {
                Tools.log("============"+studentId+"==============="+bs.getStudentId());
                attendance.setSendState(SendState.Sended);//状态为已发送
                attendance.setAttendanceStatus(status);//记录考勤情况
                attendanceDao.save(attendance);//保存考勤详情
                attId = attendance.getAttId();//获取考勤详情id
            } else {
                //非打卡学生，记录详情，状态为未到，发送状态为未发送
                attendance.setSendState(SendState.Notsended);//状态为未发送
                attendance.setAttendanceStatus(AttendanceStatus.Notarrived);//考勤情况标志为未到
                attendanceDao.save(attendance);//保存考勤详情
            }
        }
        attendanceService.sendSingleMsg(attId, status);//发送考勤消息
    }
}
