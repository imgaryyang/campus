package com.gzzm.safecampus.campus.score;

import com.gzzm.safecampus.campus.account.SchoolYear;
import com.gzzm.safecampus.campus.classes.Student;
import com.gzzm.safecampus.campus.classes.Subject;
import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.annotation.OQLUpdate;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.List;

/**
 * @author yiuman
 * @date 2018/3/15
 */
public abstract class ScoreDao extends GeneralDao
{

    @OQL("select s from SchoolYear s where s.status =1")
    public abstract SchoolYear getSchoolYear();

    @OQL("select c from Subject c where c.gradeId = ?1 and c.deleteTag=0 order by c.orderId")
    public abstract List<Subject> getSubjectListByGradeId(Integer gradeId);

    @OQL("select distinct c.gradeId from Exam c where c.examId = ?1")
    public abstract Integer getGradeIdByExamId(Integer examId);

    @OQL("select e from Exam e where e.gradeId = ?1 and e.examName = ?2")
    public abstract Exam getExamByGradeId(Integer gId, String name);

    /**
     * 获取某个学生某场考试的成绩
     * @param studentId 学生Id
     * @param examId 考试Id
     * @return 各科目的成绩
     */
    @OQL("select c from SubjectScore c where c.studentId =?1 and c.examId =?2 order by c.subject.orderId")
    public abstract List<SubjectScore> getSubjectScoresByExamAndStudentId(Integer studentId, Integer examId);

    /**
     * 根据学生姓名学号，获取学生信息
     *
     * @param studentName 姓名
     * @param studentNo   学号
     * @param deptId      学校
     * @return 学生信息
     * @throws Exception
     */
    @OQL("select s from com.gzzm.safecampus.campus.classes.Student s where s.studentName=:1 and s.studentNo=:2 and s.deptId=:3 order by s.studentId desc")
    public abstract Student getStudentByName(String studentName, String studentNo, Integer deptId) throws Exception;

    /**
     * 获取学生某场考试的科目成绩
     *
     * @param studentId 学生
     * @param subjectId 科目
     * @param examId    考试
     * @return 成绩
     * @throws Exception
     */
    @OQL("select s from com.gzzm.safecampus.campus.score.SubjectScore s where s.studentId=:1 and s.subjectId=:2 and s.examId=:3 limit 1")
    public abstract SubjectScore getStudentSubjectScore(Integer studentId, Integer subjectId, Integer examId) throws Exception;

    @OQLUpdate("delete from SubjectScore s where s.studentId in ?1 and examId = ?2")
    public abstract void deleteSubjectScores(Integer[] keys, Integer examId);

    @OQLUpdate("update Exam set messageStatus=1 where examId = :1")
    public abstract void updateExamMessageStatus(Integer examId);
}