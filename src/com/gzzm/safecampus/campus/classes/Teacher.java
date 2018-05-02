package com.gzzm.safecampus.campus.classes;

import com.gzzm.platform.addivision.AdDivision;
import com.gzzm.platform.commons.*;
import com.gzzm.safecampus.campus.account.School;
import com.gzzm.safecampus.campus.base.BaseBean;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.Inputable;
import net.cyan.commons.util.Provider;
import net.cyan.commons.validate.annotation.FieldValidator;
import net.cyan.commons.validate.annotation.Warning;
import net.cyan.nest.annotation.*;
import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.storage.CommonFileColumn;

import java.sql.Date;
import java.util.*;

/**
 * 教师信息表
 * @author yuanfang
 * @date 18-02-05 17:54
 */
@Entity(table = "SCTEACHER", keys = "teacherId")
public class Teacher extends BaseBean
{
    @Inject
    private static Provider<TeacherDao> daoProvider;

    @Generatable(length = 6)
    private Integer teacherId;

    /**
     * 给老师和学生生成的序列号
     */
    @ColumnDescription(type = "char(11)")
    private String campusSerialNo;

    /**
     * 姓名
     */
    @ColumnDescription(type = "varchar(20)")
    private String teacherName;

    /**
     * 性别
     */
    private Sex sex;

    /**
     * 年龄
     */
    @ColumnDescription(type = "number(2)")
    private Integer age;

    /**
     * 联系方式
     */
    @ColumnDescription(type = "varchar2(20)")
    private String phone;

    /**
     * 工号
     */
    @ColumnDescription(type = "varchar2(20)")
    private String teacherNo;

    /**
     * 职位/职务
     */
    @ColumnDescription(type = "varchar2(20)")
    private String job;

    /**
     * 住址
     */
    @ColumnDescription(type = "varchar2(200)")
    private String address;

    /**
     * 出生日期
     */
    private Date birthday;

    /**
     * 入职时间
     */
    private Date entryTime;

    /**
     * 省份
     */
    private Integer provinceId;

    @NotSerialized
    @ToOne("PROVINCEID")
    private AdDivision province;

    /**
     * 城市
     */
    private Integer cityId;

    @NotSerialized
    @ToOne("CITYID")
    private AdDivision city;

    /**
     * ID Card number
     */
    @ColumnDescription(type = "varchar2(20)")
    private String idCard;

    @NotSerialized
    @ToOne("GRADEID")
    private Grade grade;

    /**
     * 年级ID
     */
    private Integer gradeId;

    @NotSerialized
    @ToOne("SCHOOLID")
    private School school;

    /**
     * 学校ID
     */
    private Integer schoolId;

    /**
     * 说明
     */
    @ColumnDescription(type = "varchar2(200)")
    private String desc;

    /**
     * 插图
     */
    @CommonFileColumn(pathColumn = "picPath", target = "target", path = "safecampus/teacherPhoto")
    private Inputable picture;

    @ColumnDescription(type = "varchar(250)")
    private String picPath;

    /**
     * 删除标识
     */
    @ColumnDescription(type = "number(1)", nullable = false, defaultValue = "0")
    private Integer deleteTag;

    /**
     * 老师教授的科目
     * 过滤已删除的班级
     */
    @NotSerialized
    @ComputeColumn("select s from ClassTeacher s where s.teacherId=this.teacherId and s.subjectId is not null and (s.classes.deleteTag=0 or s.classes.deleteTag is null)")
    private List<ClassTeacher> classTeachers;

    public Teacher()
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

    public String getCampusSerialNo()
    {
        return campusSerialNo;
    }

    public void setCampusSerialNo(String campusSerialNo)
    {
        this.campusSerialNo = campusSerialNo;
    }

    public String getDesc()
    {
        return desc;
    }

    public void setDesc(String desc)
    {
        this.desc = desc;
    }

    public Date getBirthday()
    {
        return birthday;
    }

    public void setBirthday(Date birthday)
    {
        this.birthday = birthday;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getTeacherNo()
    {
        return teacherNo;
    }

    public void setTeacherNo(String teacherNo)
    {
        this.teacherNo = teacherNo;
    }

    public Inputable getPicture()
    {
        return picture;
    }

    public void setPicture(Inputable picture)
    {
        this.picture = picture;
    }

    public String getPicPath()
    {
        return picPath;
    }

    public void setPicPath(String picPath)
    {
        this.picPath = picPath;
    }

    public Integer getTeacherId()
    {
        return teacherId;
    }

    public void setTeacherId(Integer teacherId)
    {
        this.teacherId = teacherId;
    }

    public String getTeacherName()
    {
        return teacherName;
    }

    public void setTeacherName(String teacherName)
    {
        this.teacherName = teacherName;
    }

    public Sex getSex()
    {
        return sex;
    }

    public void setSex(Sex sex)
    {
        this.sex = sex;
    }

    public Integer getAge()
    {
        return age;
    }

    public void setAge(Integer age)
    {
        this.age = age;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getJob()
    {
        return job;
    }

    public void setJob(String job)
    {
        this.job = job;
    }

    public Date getEntryTime()
    {
        return entryTime;
    }

    public void setEntryTime(Date entryTime)
    {
        this.entryTime = entryTime;
    }

    public Integer getCityId()
    {
        return cityId;
    }

    public void setCityId(Integer cityId)
    {
        this.cityId = cityId;
    }

    public AdDivision getCity()
    {
        return city;
    }

    public void setCity(AdDivision city)
    {
        this.city = city;
    }

    public Integer getProvinceId()
    {
        return provinceId;
    }

    public void setProvinceId(Integer provinceId)
    {
        this.provinceId = provinceId;
    }

    public AdDivision getProvince()
    {
        return province;
    }

    public void setProvince(AdDivision province)
    {
        this.province = province;
    }

    public String getIdCard()
    {
        return idCard;
    }

    public void setIdCard(String idCard)
    {
        this.idCard = idCard;
    }

    public Grade getGrade()
    {
        return grade;
    }

    public void setGrade(Grade grade)
    {
        this.grade = grade;
    }

    public Integer getGradeId()
    {
        return gradeId;
    }

    public void setGradeId(Integer gradeId)
    {
        this.gradeId = gradeId;
    }

    public School getSchool()
    {
        return school;
    }

    public void setSchool(School school)
    {
        this.school = school;
    }

    public Integer getSchoolId()
    {
        return schoolId;
    }

    public void setSchoolId(Integer schoolId)
    {
        this.schoolId = schoolId;
    }

    public List<ClassTeacher> getClassTeachers()
    {
        return classTeachers;
    }

    public void setClassTeachers(List<ClassTeacher> classTeachers)
    {
        this.classTeachers = classTeachers;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Teacher teacher = (Teacher) o;
        return Objects.equals(teacherId, teacher.teacherId);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(teacherId);
    }

    @Override
    public String toString()
    {
        return teacherName == null ? "" : teacherName;
    }

    @FieldValidator("teacherNo")
    @Warning("teacher.teacherNo_exists")
    public Integer checkNo() throws Exception
    {
        return daoProvider.get().checkTeacherNo(getTeacherNo(), getDeptId(),getTeacherId());
    }

    @FieldValidator("phone")
    @Warning("teacher.phone_exists")
    public Integer checkPhone() throws Exception
    {
        return daoProvider.get().checkTeacherPhone(getPhone(), getDeptId(),getTeacherId());
    }

    @FieldValidator("idCard")
    @Warning("teacher.idCard_exists")
    public Integer checkIdCard() throws Exception
    {
        return daoProvider.get().checkTeacherIdCard(getIdCard(), getDeptId(),getTeacherId());
    }


}
