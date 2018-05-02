package com.gzzm.safecampus.wx.personal;

import com.gzzm.safecampus.campus.account.School;
import com.gzzm.safecampus.campus.classes.*;
import com.gzzm.safecampus.campus.device.DeviceCard;
import com.gzzm.safecampus.campus.score.Exam;
import com.gzzm.safecampus.campus.score.SubjectScore;
import com.gzzm.safecampus.wx.user.WxUser;
import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.*;

import java.util.*;

/**
 * 微信认证DAO
 *
 * @author yiuman
 * @date 2018/3/22
 */
public abstract class WxAuthDao extends GeneralDao {

    /**
     * 根据微信openid和状态查询微信用户
     *
     * @param openId
     * @param status
     * @return
     */
    @OQL("select c from WxUser c where c.openId =?1 and c.phone =?2 and c.status=?3")
    public abstract WxUser getWxUserByOpenIdAndPhone(String openId, String phone, Integer status);

    @OQL("select c from Guardian c where c.phone =?1")
    public abstract Guardian getGuardianByPhone(String phone);

    @GetByField({"studentId", "phone"})
    public abstract Guardian getGuardian(Integer studentId, String phone) throws Exception;

    /**
     * 根据 学生ID 查询考试实体
     *
     * @param studentId
     * @return
     */
    @OQL("select c from Exam c where c.examId in (select s.examId from SubjectScore s where s.studentId = ?1) ")
    public abstract List<Exam> getExamByStudentId(Integer studentId);

    /**
     * 根据 考试ID 学生ID 查询对应的学生成绩
     *
     * @param examId
     * @param studentId
     * @return
     */
    @OQL("select s from SubjectScore s where s.examId = ?1 and s.studentId =?2")
    public abstract List<SubjectScore> getSubjectScoreByExamIdAndStuId(Integer examId, Integer studentId);

    /**
     * 根据 考试ID 科目ID 学ID 查询对应的成绩
     *
     * @param examId
     * @param subjectId
     * @param studentId
     * @return
     */
    @OQL("select s from SubjectScore s where s.score is not null and s.examId = ?1 and s.subjectId = ?2 and s.studentId in ?3")
    public abstract List<SubjectScore> getSubjectScoreByExamIdAndSubjectId(Integer examId, Integer subjectId, List<Integer> studentId);

    @OQL("select c from Grade c where c.schoolId = ?1")
    public abstract List<Grade> getGradeListBySchoolId(Integer schoolId);

    /**
     * 获取某个年级下的班级列表
     *
     * @param gradeId 年级Id
     * @return 班级列表
     */
    @OQL("select c from Classes c where c.gradeId = ?1 and (c.deleteTag=0 or c.deleteTag is null)")
    public abstract List<Classes> getClassesByGradeId(Integer gradeId) throws Exception;

    /**
     * 根据 电话 老师姓名 身份证 查询老师实体
     *
     * @param phone
     * @param teacherName
     * @param idCard
     * @return
     */
    @OQL("select c from Teacher c where c.phone =?1 and c.teacherName = ?2 and c.idCard =?3")
    public abstract Teacher getTeacherByPhoneAndNameAndIdCard(String phone, String teacherName, String idCard);

    /**
     * 根据 姓名 电话  查询家属实体
     *
     * @param userName
     * @param phone
     * @return
     */
    @OQL("select c from Guardian c where c.name =:1  and c.phone =:2")
    public abstract Guardian getGuardianByNameAndIdCardAndPhone(String userName, String phone);

    /**
     * 根据 班级ID 学生名字 查询学生
     *
     * @param classesId
     * @param studentName
     * @return
     */
    @OQL("select c from Student c where c.classesId=:1 and c.studentName=:2 and (c.deleteTag=0 or c.deleteTag is null)")
    public abstract Student queryStudentBySidGidCidAndName(Integer classesId, String studentName);

    /**
     * 根据电话 身份证 学校ID 学生名字 查询注册信息
     *
     * @param phone       电话
     * @param idCard      身份证
     * @param schoolId    学校ID
     * @param studentName 学生名字
     * @return
     */
    @OQL("select c from WxRegister c where c.phone =:1 and c.idCard = :2 and c.schoolId =:3 and c.studentName=:4")
    public abstract WxRegister getWxRegisterByPhoneAndIdCard(String phone, String idCard, Integer schoolId, String studentName);

    /**
     * 根据 微信用户ID  学生ID 查询对应的微信用户与学生关系
     *
     * @param wxUserId
     * @param studentId
     * @return
     */
    @OQL("select c from WxStudent c where c.wxUserId =?1 and c.studentId = ?2")
    public abstract WxStudent getWxUserByUserIdAndStudenId(Integer wxUserId, Integer studentId);

    /**
     * 根据 家属电话 查询对应的学生
     *
     * @param phone
     * @return
     */
    @OQL("select c from Student c where c.studentId in(select g.studentId from Guardian g where g.phone = ?1) and (c.deleteTag=0 or c.deleteTag is null)")
    public abstract List<Student> queryStudentByGuardianPhone(String phone);

    @OQL("select c.studentId from WxStudent c where c.wxUserId=?1")
    public abstract List<Integer> getWxStudentsByWxuserId(Integer wxUserId);

    @OQL("select c from WxUser c where c.phone =:1  and c.status=0")
    public abstract List<WxUser> getWxUserByPhone(String phone) throws Exception;


    @OQL("select c from WxUser c where c.phone =:1 and name=:2 and c.status=0")
      public abstract List<WxUser> getWxUserByPhoneAndName(String phone,String name) throws Exception;

    /**
     * 根据微信用户ID查询微信用户关联的学生
     *
     * @param userId
     * @return
     */
    @OQL("select c.student from WxStudent c where c.wxUserId=:1 and (c.student.deleteTag=0 or c.student.deleteTag is null)")
    public abstract List<Student> queryStudentByWxUserId(Integer userId);

    /**
     * 根据学生ID查询校园卡
     *
     * @param studentId
     * @return
     */
    @OQL("select d from DeviceCard d where d.targetId = ?1 and d.status=1")
    public abstract DeviceCard getDeviceCardByStutdentId(Integer studentId);

    @OQL("select c from ClassTeacher c where c.classesId = ?1 and c.teacherRole =1")
    public abstract ClassTeacher getMasterByClassId(Integer studentId);

    /**
     * 根据学生ID 及 人脸类型 查询学生人脸对象
     *
     * @param studentId
     * @param faceType
     * @returnN
     */
    @OQL("select f from StudentFaces f where f.studentId = ?1 and f.faceType = ?2")
    public abstract StudentFaces getFaceByStudentIdAndType(Integer studentId, FaceType faceType);

    @OQL("select c.deptId from School c where c.schoolId  =?1")
    public abstract Integer getDeptIdBySchoolId(Integer schoolId);

    @OQL("select c from WxUser c where c.openId =?1 and (c.status = 0 or c.status is null)")
    public abstract WxUser getWxUserByOpenId(String openId);

    @OQL("select * from WxUser s where s.phone=:1 ")
    public abstract List<WxUser> getWxUsers(String phone);

    @OQLUpdate("delete WxStudent w where w.studentId =:1 and w.wxUserId =:2")
    public abstract Integer deleteUsersStudent(Integer studentId,Integer wxUserId);

    /**
     * 修改家属电话信息
     *
     * @param phone
     * @param phone1
     * @param userName
     */
    @OQLUpdate("update Guardian set phone = ?2 where phone = ?1 and name =?3")
    public abstract void updateGuardianPhone(String phone, String phone1, String userName);

    @OQL("select c.studentId from Student c where c.classesId =?1")
    public abstract List<Integer> getStudentIdsByClsId(Integer classesId);

    @OQL("select c from School c where c.deptId in(select s.deptId from Teacher s where s.teacherId = ?1)")
    public abstract School getSchoolByDeptId(Integer identifyId);


    @OQL("select c.student.deptId from WxStudent c " +
            "where c.wxUserId=:1 and (c.student.deleteTag=0 or c.student.deleteTag is null)")
    public abstract Integer getDeptIdByWxUserId(Integer userId);

    @OQL("select c from Guardian c where c.phone=:1 and c.name=:2 ")
    public abstract Guardian getGuardianByPhoneAndName(String phone, String name);

    @OQL("select c from School c where c.schoolCode is not null")
    public abstract List<School> getSchools();

    @OQLUpdate("update Teacher set phone = ?2 where phone = ?1 and teacherName =?3")
    public abstract void updateTeacherPhone(String phone, String phone1, String userName);
}
