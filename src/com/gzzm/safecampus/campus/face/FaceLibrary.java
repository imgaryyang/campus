package com.gzzm.safecampus.campus.face;

import com.gzzm.safecampus.campus.account.School;
import com.gzzm.safecampus.campus.base.BaseBean;
import com.gzzm.safecampus.campus.classes.Student;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.*;

import java.util.*;

/**
 * 人脸库
 * @author zy
 * @date 2018/3/28 14:11
 */
@Entity(table = "SCFACELIBRARY" , keys = "faceId")
public class FaceLibrary extends BaseBean
{
    /**
     * 人脸库主键
     */
    @ColumnDescription(type = "varchar2(36)")
    private String faceId;

    /**
     * 所属学校
     */
    private Integer schoolId;

    @ToOne("SCHOOLID")
    @NotSerialized
    private School school;

    /**
     * 根据不同用户类型存储对应的主键
     */
    @ColumnDescription(type = "number(9)")
    private Integer personId;

    /**
     * 人员类型是学生的时候关联
     */
    @ToOne("PERSONID")
    @NotSerialized
    private Student student;

    /**
     * 人员类型
     */
    private PersonType personType;

    /**
     * 添加时间
     */
    private Date createTime;

    public FaceLibrary()
    {
    }

    public String getFaceId()
    {
        return faceId;
    }

    public void setFaceId(String faceId)
    {
        this.faceId = faceId;
    }

    public Integer getSchoolId()
    {
        return schoolId;
    }

    public void setSchoolId(Integer schoolId)
    {
        this.schoolId = schoolId;
    }

    public School getSchool()
    {
        return school;
    }

    public void setSchool(School school)
    {
        this.school = school;
    }

    public Integer getPersonId()
    {
        return personId;
    }

    public void setPersonId(Integer personId)
    {
        this.personId = personId;
    }

    public PersonType getPersonType()
    {
        return personType;
    }

    public Student getStudent()
    {
        return student;
    }

    public void setStudent(Student student)
    {
        this.student = student;
    }

    public void setPersonType(PersonType personType)
    {
        this.personType = personType;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof FaceLibrary)) return false;
        FaceLibrary that = (FaceLibrary) o;
        return Objects.equals(faceId, that.faceId);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(faceId);
    }
}
