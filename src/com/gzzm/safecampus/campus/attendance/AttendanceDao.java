package com.gzzm.safecampus.campus.attendance;

import com.gzzm.safecampus.campus.classes.Student;
import net.cyan.thunwind.annotation.GetByField;
import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 考勤dao
 *
 * @author Huangmincong
 * @date 2018/03/16
 */
public abstract class AttendanceDao extends GeneralDao {
    public AttendanceDao() {
    }

    /**
     * 根据记录id获取打卡站点
     *
     * @param attId 考勤记录ID
     * @return
     * @throws Exception
     */
    @OQL("select a.siteName from AttendanceBusSite a  where a.attId = :1")
    public abstract String getSiteName(Integer attId) throws Exception;

    /**
     * 根据关系id和考勤类型获取两个日期之间的考勤批次
     *
     * @param relationId 关系ID
     * @param date1      开始日期
     * @param date2      结束日期
     * @param type       考勤类型
     * @return
     */
    @OQL("select a from AttendanceRecord a where a.type=?4 and a.relationId=?1 and a.date>?2 and a.date<?3")
    public abstract List<AttendanceRecord> getAttList(Integer relationId, java.sql.Date date1, java.sql.Date date2, AttendanceType type) throws Exception;

    /**
     * 根据部门id获取两个日期之间的校巴考勤批次
     *
     * @param date1
     * @param date2
     * @param deptId
     * @return
     */
    @OQL("select a from AttendanceRecord a where a.type=1 and a.date<?1 and a.date>?2 and  a.deptId=?3")
    public abstract List<AttendanceRecord> getByDateAndStuId(Date date1, Date date2, Integer deptId) ;
    /**
     * 根据部门id获取两个日期之间的校门考勤批次
     *
     * @param date1
     * @param date2
     * @param deptId
     * @return
     */
    @OQL("select a from AttendanceRecord a where a.type=4 and a.date<?1 and a.date>?2 and  a.deptId=?3 and a.relationId=?4")
    public abstract List<AttendanceRecord> getFaceByDateAndStuId(Date date1, Date date2, Integer deptId,Integer classesId) ;

    /**
     * 根据班级id获取班级考勤记录
     *
     * @param classesId
     * @return
     */
    @OQL("select a from Attendance a where a.type=0 and a.relationId=?1 order by a.attendanceTime desc")
    public abstract List<Attendance> getAttListById(Integer classesId) throws Exception;

    /**
     * 根据校巴id获取校巴考勤记录
     *
     * @param busId 校巴id
     * @return
     */
    @OQL("select a from Attendance a where a.type=1 and a.relationId=?1 order by a.attendanceTime desc")
    public abstract List<Attendance> getBusAttListById(Integer busId) throws Exception;

    /**
     * 根据学生id获取考勤记录
     *
     * @param studentId
     * @return
     */
    @OQL("select a from Attendance a where  a.studentId=?1 and a.attendanceTime>?2 and a.attendanceTime<?3 order by a.attendanceTime desc")
    public abstract List<Attendance> getStudentAttendance(Integer studentId, java.sql.Date today, java.sql.Date tomorrow) throws Exception;

    /**
     * 根据考勤批次id获取考勤记录
     *
     * @param aoId
     * @return
     */
    @OQL("select a from Attendance a where  a.aoId=?1 order by a.attendanceTime desc")
    public abstract List<Attendance> getListByAoId(Integer aoId) throws Exception;

    /**
     * 根据考勤批次id获取相应发送状态的考勤记录
     *
     * @param aoId
     * @param state
     * @return
     */
    @OQL("select a from Attendance a where  a.aoId=?1 and a.sendState=?2 order by a.attendanceTime desc")
    public abstract List<Attendance> getUnsendedAttdance(Integer aoId, SendState state) throws Exception;

    /**
     * 根据考勤批次id获取考勤批次
     *
     * @param aoId
     * @return
     */
    @OQL("select a from AttendanceRecord a where a.aoId=:1")
    public abstract AttendanceRecord getByAoId(Integer aoId) throws Exception;

    /**
     * 根据考勤批次id获取考勤批次
     *
     * @param aoId
     * @return
     */
    @OQL("select a from AttendanceRecord a where a.aoId=:1")
    public abstract AttendanceRecord getRecord(Integer aoId) throws Exception;

    /**
     * 根据考勤批次id获取校巴考勤的批次
     *
     * @param aoId
     * @return
     */
    @OQL("select a from AttendanceRecord a where a.aoId=:1 and a.type=1")
    public abstract AttendanceRecord getBusAttByAoId(Integer aoId) throws Exception;

    /**
     * 根据关系id获取班级考勤的考勤批次id
     *
     * @param relationId
     * @return
     */
    @OQL("select a.aoId from AttendanceRecord a where a.type=0 and a.relationId=?1 order by date desc limit 1")
    public abstract Integer getByClassesId(Integer relationId) throws Exception;

    /**
     * 根据考勤批次id和学生id获取考勤记录
     *
     * @param studentId
     * @param aoId
     * @return
     */
    @OQL("select a from Attendance a where a.type=1 and a.studentId=?1 and a.aoId=?2  order by attendanceTime desc limit 1")
    public abstract Attendance getByStudentIdAndAoId(Integer studentId, Integer aoId) throws Exception;

    /**
     * 根据考勤批次id和学生id获取考勤记录
     *
     * @param studentId
     * @param aoId
     * @return
     */
    @OQL("select a from Attendance a where a.type=?3 and a.studentId=?1 and a.aoId=?2  order by attendanceTime desc limit 1")
    public abstract Attendance getByStudentIdAndAoIdAndType(Integer studentId, Integer aoId,AttendanceType type) throws Exception;

    /**
     * 根据老师id获取午休室列表
     *
     * @param identifyId 老师id
     * @return
     */
    @OQL("select s.sroomId as Id ,s.name as name from SiestaRoom s where s.teacherId = ?1")
    public abstract List<Map<String, Object>> getSiertaInfoByTeacherId(Integer identifyId) throws Exception;

    /**
     * 根据午休室id获取当天的考勤批次
     *
     * @param sroomId  关系id
     * @param today    开始日期
     * @param tomorrow 结束日期
     * @return
     */
    @OQL("select a from AttendanceRecord a where a.relationId = ?1 and a.date>?2 and a.date <?3 and a.type=?4 order by a.date desc")
    public abstract List<AttendanceRecord> getAttendanceSiestaToday(Integer sroomId, Date today, Date tomorrow,Integer type) ;

    /**
     * 根据批次id获取相应考勤情况的考勤记录数量
     *
     * @param aoId
     * @param leave
     * @return
     */
    @OQL("select count(c.attId) from Attendance c where c.aoId = ?1 and c.attendanceStatus =?2")
    public abstract Integer countStatusByAoId(Integer aoId, AttendanceStatus leave);

    /**
     * 根据批次id获取相应发送状态的学生
     *
     * @param aoId  批次id
     * @param state 发送状态
     * @return
     */
    @OQL("select s from Student s where s.studentId in (select c.studentId from Attendance c where c.aoId = ?1 and c.sendState =?2)")
    public abstract List<Student> getUnsendStudents(Integer aoId, Integer state);

    /**
     * 根据学生id获取家长的联系电话列表
     *
     * @param studentId 学生id
     * @return
     */
    @OQL("select c.phone as phone,c.relationInfo from Guardian c where c.studentId=?1")
    public abstract List<Map<String, Object>> getRelationsByStudenId(Integer studentId);

    /**
     * 根据午休室id获取学生列表
     *
     * @param roomId 午休室id
     * @return
     * @throws Exception
     */
    @OQL("select c from Student c where c.studentId in(select s.studentId from SiestaStudent s where s.sroomId =?1)")
    public abstract List<Student> getStudentsBySiestaRoomId(Integer roomId) throws Exception;

    /**
     * 根据老师id获取托管室列表
     *
     * @param teacherId 老师id
     * @return
     * @throws Exception
     */
    @OQL("select s.troomId as Id,s.name as name from TrusteeshipRoom s where s.teacherId = ?1")
    public abstract List<Map<String, Object>> getTrusteeInfoByTeacherId(Integer teacherId) throws Exception;

    /**
     * 根据午休室id获取午休室学生列表
     *
     * @param key 午休室id
     * @return
     * @throws Exception
     */
    @OQL("select c from Student c where c.studentId in(select s.studentId from TrusteeshipStudent s where s.troomId =?1)")
    public abstract List<Student> getStudentsBytTrusteeRoomId(Integer key) throws Exception;

    /**
     * 获取某次考勤的记录
     *
     * @param recordId 考勤记录Id
     * @return 考勤记录
     * @throws Exception 操作异常
     */
    @GetByField({"aoId", "attendanceStatus"})
    public abstract List<Attendance> getAttendanceByRecordId(Integer recordId, AttendanceStatus attendanceStatus) throws Exception;

    /**
     * 根据批次id获取考勤记录的数量
     *
     * @param aoId 批次id
     * @return
     * @throws Exception
     */
    @OQL("select count(c.attId) from Attendance c where c.aoId = ?1 and (c.attendanceStatus >=5 or c.attendanceStatus =0)")
    public abstract Integer countBusStatusByAoId(Integer aoId) ;
}
