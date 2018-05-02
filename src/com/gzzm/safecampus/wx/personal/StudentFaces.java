package com.gzzm.safecampus.wx.personal;

import com.gzzm.safecampus.campus.classes.Student;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.util.Inputable;
import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.storage.CommonFileColumn;

/**
 * 学生人脸实体
 *
 * @author yiuman
 * @date 2018/4/9
 */
@Entity(table = "SCSTUDENTFACES", keys = "faceId")
public class StudentFaces {

    @Generatable(length = 6)
    private Integer faceId;

    /**
     * 人脸类型
     */
    @ColumnDescription(type = "number(1)")
    private FaceType faceType;

    /**
     * 关联学生
     */
    private Integer studentId;

    @ToOne("STUDENTID")
    @NotSerialized
    private Student student;

    @CommonFileColumn(pathColumn = "photoPath", target = "target", path = "studentfaces/pic")
    private Inputable photo;

    @ColumnDescription(type = "varchar2(250)")
    private String photoPath;

    /**
     * 人脸库图片主键
     */
    @ColumnDescription(type = "varchar2(36)")
    private String imageId;

    public Integer getFaceId() {
        return faceId;
    }

    public void setFaceId(Integer faceId) {
        this.faceId = faceId;
    }

    public FaceType getFaceType() {
        return faceType;
    }

    public void setFaceType(FaceType faceType) {
        this.faceType = faceType;
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

    public Inputable getPhoto() {
        return photo;
    }

    public void setPhoto(Inputable photo) {
        this.photo = photo;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StudentFaces)) return false;

        StudentFaces that = (StudentFaces) o;

        return faceId != null ? faceId.equals(that.faceId) : that.faceId == null;
    }

    @Override
    public int hashCode() {
        return faceId != null ? faceId.hashCode() : 0;
    }
}
