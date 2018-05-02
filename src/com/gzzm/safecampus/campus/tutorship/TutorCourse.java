package com.gzzm.safecampus.campus.tutorship;

import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.storage.CommonFileColumn;

import java.util.List;

/**
 * 课外辅导--辅导课程
 *
 * @author yiuman
 * @date 2018/3/12
 */

@Entity(table = "SCTUTORCOURSE", keys = "courseId")
public class TutorCourse extends TutorBase {

    /**
     * 课程主键
     */
    @Generatable(length = 6)
    private Integer courseId;

    /*
    课程名称
     */
    @ColumnDescription(type = "varchar(100)")
    private String courseName;


    /**
     * 所属机构
     */
    private Integer institutionId;

    @ToOne("INSTITUTIONID")
    @NotSerialized
    private TutorInstitution institution;

    /**
     * 课程介绍
     */
    @CommonFileColumn(pathColumn = "filePath", target="course", path = "course/{yyyyMM}/{yyyyMMdd}/{courseId}")
    private byte[] courseDescribe;

    @NotSerialized
    @ComputeColumn("select m from CourseTypes m where m.courseId=this.courseId")
    private List<CourseTypes> courseTypes;

    @ColumnDescription(type = "varchar(250)")
    private String filePath;

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }


    public Integer getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(Integer institutionId) {
        this.institutionId = institutionId;
    }

    public TutorInstitution getInstitution() {
        return institution;
    }

    public void setInstitution(TutorInstitution institution) {
        this.institution = institution;
    }

    public byte[] getCourseDescribe() {
        return courseDescribe;
    }

    public void setCourseDescribe(byte[] courseDescribe) {
        this.courseDescribe = courseDescribe;
    }

    public List<CourseTypes> getCourseTypes() {
        return courseTypes;
    }

    public void setCourseTypes(List<CourseTypes> courseTypes) {
        this.courseTypes = courseTypes;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TutorCourse)) return false;

        TutorCourse that = (TutorCourse) o;

        return courseId != null ? courseId.equals(that.courseId) : that.courseId == null;
    }

    @Override
    public int hashCode() {
        return courseId != null ? courseId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return courseName;
    }
}
