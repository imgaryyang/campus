package com.gzzm.safecampus.campus.classes;

import com.gzzm.platform.addivision.AdDivision;
import com.gzzm.platform.commons.Sex;
import com.gzzm.safecampus.campus.base.BaseClasses;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.util.Inputable;
import net.cyan.commons.util.Provider;
import net.cyan.commons.validate.annotation.FieldValidator;
import net.cyan.commons.validate.annotation.Warning;
import net.cyan.nest.annotation.Inject;
import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.storage.CommonFileColumn;

import java.sql.Date;
import java.util.List;
import java.util.Objects;

/**
 * 学生表
 *
 * @author yuanfang
 * @date 18-02-06 18:17
 */
@Entity(table = "SCSTUDENT", keys = "studentId")
public class Student extends BaseClasses
{
    @Inject
    private static Provider<StudentDao> daoProvider;

    /**
     * 学生ID
     */
    @Generatable(length = 9)
    private Integer studentId;

    /**
     * 给老师和学生生成的序列号
     */
    @ColumnDescription(type = "char(11)")
    private String campusSerialNo;

    /**
     * 学生姓名
     */
    @ColumnDescription(type = "varchar2(20)")
    private String studentName;

    /**
     * 学生家属
     */
    @NotSerialized
    @OneToMany
    private List<Guardian> guardians;

    /**
     * 出生日期
     */
    private Date birthday;

    /**
     * 入学时间
     */
    private Date entryTime;

    /**
     * 性别
     */
    private Sex sex;

    /**
     * 住址
     */
    @ColumnDescription(type = "varchar2(200)")
    private String address;

    /**
     * 学号
     */
    @ColumnDescription(type = "varchar2(16)")
    private String studentNo;

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

    /**
     * 说明
     */
    @ColumnDescription(type = "varchar2(200)")
    private String desc;

    /**
     * 学生的联系方式
     */
    @ColumnDescription(type = "varchar2(20)")
    private String phone;

    /**
     * 插图
     */
    @CommonFileColumn(pathColumn = "picPath", target = "target", path = "safecampus/studentPhoto")
    private Inputable picture;

    @ColumnDescription(type = "varchar(250)")
    private String picPath;

    @ColumnDescription(type = "number(1)", nullable = false, defaultValue = "0")
    private Integer deleteTag;

    public Student()
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

    public List<Guardian> getGuardians()
    {
        return guardians;
    }

    public void setGuardians(List<Guardian> guardians)
    {
        this.guardians = guardians;
    }

    public String getCampusSerialNo()
    {
        return campusSerialNo;
    }

    public void setCampusSerialNo(String campusSerialNo)
    {
        this.campusSerialNo = campusSerialNo;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public Date getEntryTime()
    {
        return entryTime;
    }

    public void setEntryTime(Date entryTime)
    {
        this.entryTime = entryTime;
    }

    public String getDesc()
    {
        return desc;
    }

    public void setDesc(String desc)
    {
        this.desc = desc;
    }

    public Integer getStudentId()
    {
        return studentId;
    }

    public void setStudentId(Integer studentId)
    {
        this.studentId = studentId;
    }

    public String getStudentName()
    {
        return studentName;
    }

    public void setStudentName(String studentName)
    {
        this.studentName = studentName;
    }

    public Date getBirthday()
    {
        return birthday;
    }

    public void setBirthday(Date birthday)
    {
        this.birthday = birthday;
    }

    public Sex getSex()
    {
        return sex;
    }

    public void setSex(Sex sex)
    {
        this.sex = sex;
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

    public String getIdCard()
    {
        return idCard;
    }

    public void setIdCard(String idCard)
    {
        this.idCard = idCard;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getStudentNo()
    {
        return studentNo;
    }

    public void setStudentNo(String studentNo)
    {
        this.studentNo = studentNo;
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

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(studentId, student.studentId);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(studentId);
    }


    @Override
    public String toString()
    {
        return studentName;
    }

    @FieldValidator("studentNo")
    @Warning("student.studentNo_exists")
    public Integer checkNo() throws Exception
    {
        return daoProvider.get().checkStudentNo(getStudentId(), getStudentNo(), getDeptId());
    }

    @FieldValidator("phone")
    @Warning("student.phone_exists")
    public Integer checkPhone() throws Exception
    {
        return daoProvider.get().checkStudentPhone(getStudentId(), getPhone(), getDeptId());
    }

    @FieldValidator("idCard")
    @Warning("student.idCard_exists")
    public Integer checkIdCard() throws Exception
    {

        return daoProvider.get().checkStudentIdCard(getStudentId(), getIdCard(), getDeptId());
    }


}
