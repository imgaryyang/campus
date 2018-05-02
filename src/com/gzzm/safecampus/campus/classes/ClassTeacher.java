package com.gzzm.safecampus.campus.classes;

import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.Entity;
import net.cyan.thunwind.annotation.Generatable;
import net.cyan.thunwind.annotation.ToOne;

import java.util.Objects;

/**
 * *
 * 班级的所有科目的科任老师（包括生活老师）和班主任
 *
 * @author yuanfang
 * @date 18-03-08 10:18
 */
@Entity(table = "SCCLASSTEACHER", keys = "ctId")
public class ClassTeacher
{
    @Generatable(length = 6)
    private Integer ctId;

    /**
     * 所属班级
     */
    private Integer classesId;

    @NotSerialized
    @ToOne
    private Classes classes;

    /**
     * 老师Id
     */
    private Integer teacherId;

    @NotSerialized
    @ToOne
    private Teacher teacher;

    /**
     * 关联的科目
     */
    private Integer subjectId;

    @NotSerialized
    @ToOne
    private Subject subject;

    /**
     * 老师的角色
     */
    private TeacherRole teacherRole;

    public ClassTeacher()
    {
    }

    public Integer getCtId()
    {
        return ctId;
    }

    public void setCtId(Integer ctId)
    {
        this.ctId = ctId;
    }

    public Classes getClasses()
    {
        return classes;
    }

    public void setClasses(Classes classes)
    {
        this.classes = classes;
    }

    public Integer getClassesId()
    {
        return classesId;
    }

    public void setClassesId(Integer classesId)
    {
        this.classesId = classesId;
    }

    public Teacher getTeacher()
    {
        return teacher;
    }

    public void setTeacher(Teacher teacher)
    {
        this.teacher = teacher;
    }

    public Integer getTeacherId()
    {
        return teacherId;
    }

    public void setTeacherId(Integer teacherId)
    {
        this.teacherId = teacherId;
    }

    public Subject getSubject()
    {
        return subject;
    }

    public void setSubject(Subject subject)
    {
        this.subject = subject;
    }

    public Integer getSubjectId()
    {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId)
    {
        this.subjectId = subjectId;
    }

    public TeacherRole getTeacherRole()
    {
        return teacherRole;
    }

    public void setTeacherRole(TeacherRole teacherRole)
    {
        this.teacherRole = teacherRole;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassTeacher that = (ClassTeacher) o;
        return Objects.equals(ctId, that.ctId);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(ctId);
    }

    @Override
    public String toString()
    {
        return teacher == null ? "" : teacher.toString();
    }

}

