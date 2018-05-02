package com.gzzm.safecampus.campus.classes;

import com.gzzm.platform.commons.*;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.Provider;
import net.cyan.commons.validate.annotation.FieldValidator;
import net.cyan.commons.validate.annotation.Warning;
import net.cyan.nest.annotation.*;
import net.cyan.thunwind.annotation.*;

import java.sql.Date;
import java.util.*;

/**
 * 学生的监护人信息
 * 学生与家属是一对多关系
 *
 * @author yuanfang
 * @date 18-03-06 14:12
 */
@Entity(table = "SCGUARDIAN", keys = "guardianId")
public class Guardian
{
    @Inject
    private static Provider<GuardianDao> daoProvider;

    /**
     * 主键
     */
    @Generatable(length = 9)
    private Integer guardianId;

    /**
     * 监护人姓名
     */
    @ColumnDescription(type = "varchar2(30)")
    private String name;

    /**
     * 性别
     */
    private Sex sex;

    /**
     * 出生日期
     */
    private Date birthday;

    /**
     * 联系方式
     */
    @ColumnDescription(type = "varchar2(50)")
    private String phone;

    /**
     * 住址
     */
    @ColumnDescription(type = "varchar2(250)")
    private String address;

    /**
     * 省份
     */
    @ColumnDescription(type = "varchar2(50)")
    private String province;

    /**
     * 城市
     */
    @ColumnDescription(type = "varchar2(50)")
    private String city;

    /**
     * 身份证号
     */
    @ColumnDescription(type = "varchar2(50)")
    private String idCard;

    /**
     * 家属关系
     */
    @ColumnDescription(type = "varchar2(50)")
    private String relationInfo;

    /**
     * 关联学生
     */
    private Integer studentId;

    @NotSerialized
    @ToOne
    private Student student;

    @ColumnDescription(type = "NUMBER(6)")
    private Integer orderId;

    public Guardian()
    {
    }

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
    }

    public Integer getGuardianId()
    {
        return guardianId;
    }

    public void setGuardianId(Integer guardianId)
    {
        this.guardianId = guardianId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Sex getSex()
    {
        return sex;
    }

    public void setSex(Sex sex)
    {
        this.sex = sex;
    }

    public Date getBirthday()
    {
        return birthday;
    }

    public void setBirthday(Date birthday)
    {
        this.birthday = birthday;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getProvince()
    {
        return province;
    }

    public void setProvince(String province)
    {
        this.province = province;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
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

    public Student getStudent()
    {
        return student;
    }

    public void setStudent(Student student)
    {
        this.student = student;
    }

    public Integer getStudentId()
    {
        return studentId;
    }

    public void setStudentId(Integer studentId)
    {
        this.studentId = studentId;
    }

    public String getRelationInfo()
    {
        return relationInfo;
    }

    public void setRelationInfo(String relationInfo)
    {
        this.relationInfo = relationInfo;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Guardian guardian = (Guardian) o;
        return Objects.equals(guardianId, guardian.guardianId);
    }

    @Override
    public int hashCode()
    {

        return Objects.hash(guardianId);
    }

    @Override
    public String toString()
    {
        return name;
    }

    @FieldValidator("phone")
    @Warning("guardian.phone_exists")
    public Integer checkPhone() throws Exception
    {
        return daoProvider.get().checkGuardianPhone(getStudentId(), getPhone());
    }
}
