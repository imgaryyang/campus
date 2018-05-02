package com.gzzm.safecampus.campus.classes;

import com.gzzm.safecampus.campus.base.BaseBean;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.ColumnDescription;
import net.cyan.thunwind.annotation.Entity;
import net.cyan.thunwind.annotation.Generatable;
import net.cyan.thunwind.annotation.ToOne;

/**
 * 课程表
 *
 * @author yuanfang
 * @date 18-03-14 10:12
 */
@Entity(table = "SCSUBJECT", keys = "subjectId")
public class Subject extends BaseBean
{
    /**
     * 课程主键ID
     */
    @Generatable(length = 6)
    private Integer subjectId;

    /**
     * 课程名称
     */
    @ColumnDescription(type = "varchar(20)")
    private String subjectName;

    /**
     * 科目编号
     */
    @ColumnDescription(type = "varchar(20)")
    private String subjectCode;

    @ColumnDescription(type = "number(1)", nullable = false, defaultValue = "0")
    private Integer deleteTag;

    /**
     * 所属年级
     */
    private Integer gradeId;

    @NotSerialized
    @ToOne
    private Grade grade;

    public Subject()
    {
    }

    public Integer getDeleteTag()
    {
        return deleteTag;
    }

    public void setDeleteTag(Integer deleteTag)
    {
        this.deleteTag = deleteTag;
    }

    public Integer getSubjectId()
    {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId)
    {
        this.subjectId = subjectId;
    }

    public String getSubjectName()
    {
        return subjectName;
    }

    public void setSubjectName(String subjectName)
    {
        this.subjectName = subjectName;
    }

    public String getSubjectCode()
    {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode)
    {
        this.subjectCode = subjectCode;
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

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof Subject)) return false;

        Subject subject = (Subject) o;

        return subjectId != null ? subjectId.equals(subject.subjectId) : subject.subjectId == null;
    }

    @Override
    public int hashCode()
    {
        return subjectId != null ? subjectId.hashCode() : 0;
    }

    @Override
    public String toString()
    {
        return subjectName;
    }
}
