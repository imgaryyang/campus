package com.gzzm.safecampus.campus.score;

import com.gzzm.safecampus.campus.account.SchoolYear;
import com.gzzm.safecampus.campus.base.BaseBean;
import com.gzzm.safecampus.campus.classes.Grade;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.ColumnDescription;
import net.cyan.thunwind.annotation.Entity;
import net.cyan.thunwind.annotation.Generatable;
import net.cyan.thunwind.annotation.ToOne;

import java.sql.Date;

/**
 * 考试表
 *
 * @author yiuman
 * @date 2018/3/15
 */
@Entity(table = "SCEXAM", keys = "examId")
public class Exam extends BaseBean
{

    @Generatable(length = 6)
    private Integer examId;

    @ColumnDescription(type = "varchar(50)")
    private String examName;

    private Date examTime;

    private Integer yearId;

    @ToOne("YEARID")
    @NotSerialized
    private SchoolYear year;

    private Integer gradeId;

    @ToOne("GRADEID")
    @NotSerialized
    private Grade grade;

    /**
     * 信息状态
     */
    @ColumnDescription(type = "number(1)",defaultValue = "0")
    private Integer messageStatus;

    public Exam()
    {
    }

    public Integer getExamId()
    {
        return examId;
    }

    public void setExamId(Integer examId)
    {
        this.examId = examId;
    }

    public String getExamName()
    {
        return examName;
    }

    public void setExamName(String examName)
    {
        this.examName = examName;
    }

    public Date getExamTime()
    {
        return examTime;
    }

    public void setExamTime(Date examTime)
    {
        this.examTime = examTime;
    }

    public Integer getYearId()
    {
        return yearId;
    }

    public void setYearId(Integer yearId)
    {
        this.yearId = yearId;
    }

    public SchoolYear getYear()
    {
        return year;
    }

    public void setYear(SchoolYear year)
    {
        this.year = year;
    }

    public Integer getGradeId()
    {
        return gradeId;
    }

    public void setGradeId(Integer gradeId)
    {
        this.gradeId = gradeId;
    }

    public Grade getGrade()
    {
        return grade;
    }

    public void setGrade(Grade grade)
    {
        this.grade = grade;
    }

    public Integer getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(Integer messageStatus) {
        this.messageStatus = messageStatus;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof Exam)) return false;

        Exam exam = (Exam) o;

        return examId != null ? examId.equals(exam.examId) : exam.examId == null;
    }

    @Override
    public int hashCode()
    {
        return examId != null ? examId.hashCode() : 0;
    }

    @Override
    public String toString()
    {
        return examName;
    }
}

