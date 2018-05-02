package com.gzzm.safecampus.wx.attendance;

import com.gzzm.platform.commons.Tools;
import com.gzzm.safecampus.campus.attendance.*;
import com.gzzm.safecampus.campus.classes.Classes;
import com.gzzm.safecampus.campus.classes.ClassesDao;
import com.gzzm.safecampus.campus.classes.Student;
import com.gzzm.safecampus.campus.device.Camera;
import com.gzzm.safecampus.campus.device.Purpose;
import net.cyan.arachne.annotation.Service;
import net.cyan.nest.annotation.Inject;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 人脸识别
 *
 * @author hmc
 * @date 2018/04/08
 */
@Service
public class FaceRecAttendanceService {

    @Inject
    private AttendanceDao attendanceDao;

    @Inject
    private ClassesDao classesDao;

    @Inject
    private AttendanceService attendanceService;

    private static String mstart = "morning_into_school_start";

    private static String mend = "morning_into_school_end";

    private static String nInStart = "noon_into_out_school_start";

    private static String nInEnd = "noon_into_out_school_end";

    private static String aOutEnd = "afternoon_out_school_end";

    private static String aOutStart = "afternoon_out_school_start";

    /**
     * 人脸识别接口
     *
     * @param studentId 学生Id
     * @param cameraId  设备Id
     * @param imgId     图片Id
     * @param date      考勤时间
     */
    public void saveAttendance(Integer studentId, Integer cameraId, String imgId, java.util.Date date) throws Exception {
        Tools.log("进入人脸识别====================================");
        Camera camera = attendanceDao.get(Camera.class, cameraId);
        Student student = attendanceDao.load(Student.class, studentId);
        Classes classes = classesDao.getByStudentId(studentId);
        if (student == null || camera == null || classes == null) {
            return;
        }
        Tools.log("确认学生存在====================================");
        Integer deptId = student.getDeptId();
        //根据时间和记录判断是什么考勤
        java.sql.Date date1 = new java.sql.Date(date.getTime());//获取当前时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Integer hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour < Integer.valueOf(Tools.getMessage(mend)) && hour >= Integer.valueOf(Tools.getMessage(mstart))) {
            Tools.log("上午进校====================================");
            //上午进校
            if (camera.getPurpose() == Purpose.IN) {
                String batch = "上午进校";
                AttendanceStatus status= AttendanceStatus.MorningComeIn;
                List<AttendanceRecord> records = getAttdentList(date1, deptId, Integer.valueOf(Tools.getMessage(mend)), Integer.valueOf(Tools.getMessage(mstart)), classes.getClassesId());
                toInsertAttendances(studentId, cameraId, imgId, date, classes, deptId, batch, records,status);
            } else {
                Tools.log("上午出校====================================直接返回（上午没有出校识别）");
                return;
            }
        } else if (hour < Integer.valueOf(Tools.getMessage(nInEnd)) && hour >= Integer.valueOf(Tools.getMessage(nInStart))) {
            Tools.log("中午时间段====================================");
            if (camera.getPurpose() == Purpose.IN) {
                Tools.log("中午进校====================================");
                //进校
                String batch = "中午进校";
                AttendanceStatus status= AttendanceStatus.NoonComeIn;
                List<AttendanceRecord> records = getAttdentList(date1, deptId, Integer.valueOf(Tools.getMessage(nInEnd)), Integer.valueOf(Tools.getMessage(nInStart)), classes.getClassesId());
                toInsertAttendances(studentId, cameraId, imgId, date, classes, deptId, batch, records,status);
            } else if (camera.getPurpose() == Purpose.OUT) {
                Tools.log("中午出校====================================");
                //出校
                String batch = "中午出校";
                AttendanceStatus status= AttendanceStatus.NoonOut;
                List<AttendanceRecord> records = getAttdentList(date1, deptId, Integer.valueOf(Tools.getMessage(nInEnd)), Integer.valueOf(Tools.getMessage(nInStart)), classes.getClassesId());
                toInsertAttendances(studentId, cameraId, imgId, date, classes, deptId, batch, records,status);
            }
        } else if (hour <= Integer.valueOf(Tools.getMessage(aOutEnd)) && hour >= Integer.valueOf(Tools.getMessage(aOutStart))) {
            Tools.log("下午时间段====================================");
            if (camera.getPurpose() == Purpose.OUT) {
                Tools.log("下午出校====================================");
                //出校
                String batch = "下午出校";
                AttendanceStatus status= AttendanceStatus.AfterNoonOut;
                List<AttendanceRecord> records = getAttdentList(date1, deptId, Integer.valueOf(Tools.getMessage(aOutEnd)), Integer.valueOf(Tools.getMessage(aOutStart)), classes.getClassesId());
                toInsertAttendances(studentId, cameraId, imgId, date, classes, deptId, batch, records,status);
            } else {
                Tools.log("下午进校====================================直接返回，下午没有进校考勤");
                return;
            }
        }
    }

    /**
     * 对识别到的学生考勤做处理
     *
     * @param studentId 学生id
     * @param cameraId  设备id
     * @param imgId     图片id
     * @param date      考勤时间
     * @param classes   班级
     * @param deptId    部门id
     * @param batch     考勤批次标识
     * @param records   考勤批次集合
     * @throws Exception
     */
    private void toInsertAttendances(Integer studentId, Integer cameraId, String imgId, Date date, Classes classes, Integer deptId, String batch, List<AttendanceRecord> records,AttendanceStatus status) throws Exception {
        Tools.log("进入插入考勤记录方法====================================");
        if (records!=null){
            Tools.log("总记录条数共："+records.size());
        }
        Integer attId = 0;
        if (records == null || records.size() == 0) {
            Tools.log("数据库没有校门考勤记录====================================");
            AttendanceRecord attRecord = getAttendanceRecord(date, classes.getClassesId(), deptId);
            attRecord.setAttendanceBatch(batch);
            attendanceDao.save(attRecord);
            List<Student> students = classes.getStudents();
            Tools.log("查询学生====================================");
            for (Student stu : students) {
                Tools.log("学生存在====================================");
                Attendance attendance = getAttendance(cameraId, imgId, date, classes, deptId, attRecord, stu);
                if (studentId.toString().equals(stu.getStudentId().toString())) {
                    Tools.log("匹配到学生====================================");
                    attendance.setSendState(SendState.Sended);
                    attendance.setAttendanceStatus(status);
                    attendanceDao.save(attendance);
                    attId = attendance.getAttId();
                } else {
                    Tools.log("同班的同学考勤记录====================================");
                    attendance.setSendState(SendState.Notsended);
                    attendance.setAttendanceStatus(AttendanceStatus.Notarrived);
                    attendanceDao.save(attendance);
                }
            }
            Tools.log("发送消息====================================");
            attendanceService.sendSingleMsg(attId, status);
        } else if (records.size() == 1) {
            Tools.log("存在一次考勤总记录====================================");
            updateAttendance(studentId, date, records.get(0).getAoId(),status);
        } else if (records.size() == 2) {
            Tools.log("存在两次考勤总记录====================================");
            for (AttendanceRecord record : records) {
                if (record.getAttendanceBatch().equals(batch)) {
                    Tools.log("匹配到其中一次考勤记录====================================");
                    updateAttendance(studentId, date, record.getAoId(),status);
                }
            }
        }
    }

    /**
     * 更新识别的学生的校门考勤状态、考勤时间、发送状态
     *
     * @param studentId 学生id
     * @param date      考勤时间
     * @param aoId      考勤批次id（总表id）
     * @throws Exception
     */
    private void updateAttendance(Integer studentId, Date date, Integer aoId,AttendanceStatus status) throws Exception {
        Tools.log("进入更新考勤方法====================================");
        Attendance attendance = attendanceDao.getByStudentIdAndAoIdAndType(studentId, aoId, AttendanceType.FaceAttendance);
        //判断该学生是否已经发送过考勤消息
        if (attendance.getSendState() == SendState.Sended) {
            Tools.log("该学生已发送消息，直接返回====================================");
            return;
        }
        attendance.setAttendanceTime(date);
        attendance.setAttendanceStatus(status);
        attendance.setSendState(SendState.Sended);
        attendanceDao.update(attendance);
        //发送考勤消息
        attendanceService.sendSingleMsg(attendance.getAttId(), status);
    }

    /**
     * 组装考勤详情实体
     *
     * @param cameraId  设备id
     * @param imgId     图片id
     * @param date      考勤时间
     * @param classes   学生所在班级
     * @param deptId    部门id
     * @param attRecord 考勤批次
     * @param stu       学生
     * @return 返回考勤详情实体
     */
    private Attendance getAttendance(Integer cameraId, String imgId, Date date, Classes classes, Integer deptId, AttendanceRecord attRecord, Student stu) {
        Attendance attendance = new Attendance();
        attendance.setStudentId(stu.getStudentId());//学生id
        attendance.setType(AttendanceType.FaceAttendance);//类型为校门考勤
        attendance.setAttendanceTime(date);//考勤时间
        attendance.setImgId(imgId);//图片id
        attendance.setCameraId(cameraId);//设备id
        attendance.setDeptId(deptId);//记录部门ID
        Tools.log("总表id==================="+attRecord.getAoId());
        attendance.setAoId(attRecord.getAoId());//总表id（某个批次id）
        attendance.setRelationId(classes.getClassesId());//班级id（关联id）
        return attendance;
    }

    /**
     * 获取学校某个时间段内某个班级的考勤批次集合
     *
     * @param currDate  当前时间
     * @param deptId    部门id
     * @param hour1     时间段结束时间
     * @param hour2     时间段开始时间
     * @param classesId 班级id
     * @return 返回考勤批次集合
     */
    private List<AttendanceRecord> getAttdentList(java.sql.Date currDate, Integer deptId, Integer hour1, Integer hour2, Integer classesId) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currDate);
        calendar.set(Calendar.HOUR_OF_DAY, hour1);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        java.util.Date date1 = calendar.getTime();
        calendar.set(Calendar.HOUR_OF_DAY, hour2);
        java.util.Date date2 = calendar.getTime();
        return attendanceDao.getFaceByDateAndStuId(date1, date2, deptId, classesId);
    }

    /**
     * 组装考勤批次实体
     *
     * @param date      考勤时间
     * @param classesId 班级id
     * @param deptId    部门id
     * @return 返回考勤批次
     */
    private AttendanceRecord getAttendanceRecord(Date date, Integer classesId, Integer deptId) {
        AttendanceRecord attRecord = new AttendanceRecord();
        attRecord.setRelationId(classesId);
        attRecord.setDate(date);
        attRecord.setType(AttendanceType.FaceAttendance);
        attRecord.setDeptId(deptId);
        return attRecord;
    }
}
