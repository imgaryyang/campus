package com.gzzm.safecampus.campus.tutorship;

import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.Entity;
import net.cyan.thunwind.annotation.ToOne;

/**
 * 名师与课程关系表
 * @author yiuman
 * @date 2018/3/13
 */
@Entity(table = "SCTEACHERCOURSE", keys = {"teacherId", "courseId"})
public class TeacherCourse {

    /**
     * 名师
     */
    private Integer teacherId;

    @NotSerialized
    @ToOne("teacherId")
    private TutorTeacher teacher;

    /**
     * 课程
     */
    private Integer courseId;

    @NotSerialized
    @ToOne("courseId")
    private TutorCourse course;

    public Integer getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Integer teacherId) {
        this.teacherId = teacherId;
    }

    public TutorTeacher getTeacher() {
        return teacher;
    }

    public void setTeacher(TutorTeacher teacher) {
        this.teacher = teacher;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public TutorCourse getCourse() {
        return course;
    }

    public void setCourse(TutorCourse course) {
        this.course = course;
    }
}
