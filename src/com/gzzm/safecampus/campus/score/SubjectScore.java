package com.gzzm.safecampus.campus.score;

import com.gzzm.safecampus.campus.base.BaseBean;
import com.gzzm.safecampus.campus.classes.Student;
import com.gzzm.safecampus.campus.classes.Subject;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.*;

/**
 * 科目成绩表
 *
 * @author yiuman
 * @date 2018/3/15
 */
@Entity(table = "SCSUBJECTSCORE", keys = "scoreId")
public class SubjectScore extends BaseBean{

    @Generatable(length = 6)
    private Integer scoreId;

    @ColumnDescription(type = "varchar(10)")
    private String score;

    private Integer subjectId;

    @ToOne("SUBJECTID")
    @NotSerialized
    private Subject subject;

    private Integer studentId;

    @ToOne("STUDENTID")
    @NotSerialized
    private Student student;

    private Integer examId;

    @Index
    @ToOne("EXAMID")
    @NotSerialized
    private Exam exam;

    public SubjectScore() {
    }

    public Integer getScoreId() {
        return scoreId;
    }

    public void setScoreId(Integer scoreId) {
        this.scoreId = scoreId;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Integer getExamId() {
        return examId;
    }

    public void setExamId(Integer examId) {
        this.examId = examId;
    }

    public Exam getExam() {
        return exam;
    }

    public void setExam(Exam exam) {
        this.exam = exam;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubjectScore)) return false;

        SubjectScore that = (SubjectScore) o;

        return scoreId != null ? scoreId.equals(that.scoreId) : that.scoreId == null;
    }

    @Override
    public int hashCode() {
        return scoreId != null ? scoreId.hashCode() : 0;
    }



}
