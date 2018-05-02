package com.gzzm.safecampus.campus.base;

import com.gzzm.safecampus.campus.classes.Student;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.ToOne;

/**
 * 简单的学生信息
 *
 * @author Neo
 * @date 2018/3/23 16:08
 */
public class BaseStudent extends BaseClasses
{
    /**
     * 学生ID
     */
    protected Integer studentId;

    @NotSerialized
    @ToOne
    protected Student student;

    public BaseStudent()
    {
    }

    public Integer getStudentId()
    {
        return studentId;
    }

    public void setStudentId(Integer studentId)
    {
        this.studentId = studentId;
    }

    public Student getStudent()
    {
        return student;
    }

    public void setStudent(Student student)
    {
        this.student = student;
    }
}
