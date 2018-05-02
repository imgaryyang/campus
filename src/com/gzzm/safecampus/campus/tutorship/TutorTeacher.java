package com.gzzm.safecampus.campus.tutorship;

import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.util.Inputable;
import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.storage.CommonFileColumn;

import java.util.List;

/**
 * 课外辅导--名师资源
 *
 * @author yiuman
 * @date 2018/3/12
 */

@Entity(table = "SCTUTORTEACHER", keys = "teacherId")
public class TutorTeacher extends TutorBase {

    /**
     * 名师主键ID
     */
    @Generatable(length = 6)
    private Integer teacherId;

    /**
     * 名师名称
     */
    @ColumnDescription(type = "varchar(20)")
    private String teacherName;

    /**
     * 所属机构
     */
    private Integer institutionId;

    @ToOne("INSTITUTIONID")
    @NotSerialized
    private TutorInstitution institution;

    @CommonFileColumn(pathColumn = "filePath", target = "target", path = "teacher/{yyyyMM}/{yyyyMMdd}/{teacherId}")
    private byte[] teacherDescribe;

    /**
     * 插图
     */
    @CommonFileColumn(pathColumn = "picPath", target = "target", path = "teacher/pic")
    private Inputable picture;

    /**
     * 名师与课程
     */
    @NotSerialized
    @ComputeColumn("select m from TeacherCourse m where m.teacherId=this.teacherId")
    private List<TeacherCourse> teacherCourses;

    @ColumnDescription(type = "varchar(250)")
    private String filePath;

    @ColumnDescription(type = "varchar(250)")
    private String picPath;

    public Integer getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Integer teacherId) {
        this.teacherId = teacherId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
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

    public byte[] getTeacherDescribe() {
        return teacherDescribe;
    }

    public void setTeacherDescribe(byte[] teacherDescribe) {
        this.teacherDescribe = teacherDescribe;
    }

    public Inputable getPicture() {
        return picture;
    }

    public void setPicture(Inputable picture) {
        this.picture = picture;
    }

    public List<TeacherCourse> getTeacherCourses() {
        return teacherCourses;
    }

    public void setTeacherCourses(List<TeacherCourse> teacherCourses) {
        this.teacherCourses = teacherCourses;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getPicPath() {
        return picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TutorTeacher)) return false;

        TutorTeacher that = (TutorTeacher) o;

        return teacherId != null ? teacherId.equals(that.teacherId) : that.teacherId == null;
    }

    @Override
    public int hashCode() {
        return teacherId != null ? teacherId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return teacherName;
    }
}
