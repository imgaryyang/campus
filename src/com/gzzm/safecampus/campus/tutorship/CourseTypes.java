package com.gzzm.safecampus.campus.tutorship;

import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.Entity;
import net.cyan.thunwind.annotation.ToOne;

/**
 * 课程与课程分类关系表
 * @author yiuman
 * @date 2018/3/13
 */
@Entity(table = "SCCOURSETYPES", keys = {"courseId", "typeItemId"})
public class CourseTypes {

    /**
     * 课程
     */
    private Integer courseId;

    @NotSerialized
    @ToOne("courseId")
    private TutorCourse course;

    /**
     * 分类
     */
    private Integer typeItemId;

    @NotSerialized
    @ToOne("typeItemId")
    private TutorSubjectTypeItem subjectTypeItem;

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

    public Integer getTypeItemId() {
        return typeItemId;
    }

    public void setTypeItemId(Integer typeItemId) {
        this.typeItemId = typeItemId;
    }

    public TutorSubjectTypeItem getSubjectTypeItem() {
        return subjectTypeItem;
    }

    public void setSubjectTypeItem(TutorSubjectTypeItem subjectTypeItem) {
        this.subjectTypeItem = subjectTypeItem;
    }
}
