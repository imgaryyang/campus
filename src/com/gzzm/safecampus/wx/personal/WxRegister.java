package com.gzzm.safecampus.wx.personal;

import com.gzzm.safecampus.campus.account.School;
import com.gzzm.safecampus.campus.base.BaseBean;
import com.gzzm.safecampus.campus.classes.Classes;
import com.gzzm.safecampus.campus.classes.Grade;
import net.cyan.thunwind.annotation.ColumnDescription;
import net.cyan.thunwind.annotation.Entity;
import net.cyan.thunwind.annotation.Generatable;
import net.cyan.thunwind.annotation.ToOne;

import java.util.Date;

/**
 * 微信用户注册表
 *
 * @author yiuman
 * @date 2018/3/29
 */
@Entity(table = "SCWXREGISTER", keys = "registerId")
public class WxRegister extends BaseBean {

    /**
     * 主键
     */
    @Generatable(length = 6)
    private Integer registerId;

    @ColumnDescription(type = "varchar(100)")
    private String openId;

    /**
     * 学校
     */
    private Integer schoolId;

    @ToOne("SCHOOLID")
    private School school;

    /**
     * 年级
     */
    private Integer gradeId;

    @ToOne("GRADEID")
    private Grade grade;

    /**
     * 班级
     */
    private Integer classesId;

    @ToOne("CLASSESID")
    private Classes classes;

    /**
     * 注册用户名
     */
    @ColumnDescription(type = "varchar2(25)")
    private String userName;

    /**
     * 身份证
     */
    @ColumnDescription(type = "varchar2(50)")
    private String idCard;

    /**
     * 手机
     */
    @ColumnDescription(type = "varchar2(25)")
    private String phone;

    /**
     * 学生姓名
     */
    @ColumnDescription(type = "varchar2(25)")
    private String studentName;

    /**
     * 状态 0未审核  1通过 2不通过
     */
    @ColumnDescription(type = "number(1)",defaultValue = "0")
    private Integer state;

    /**
     * 注册时间
     */
    private Date registerTime;

    /**
     * 审核时间
     */
    private Date validateTime;


    public Integer getRegisterId() {
        return registerId;
    }

    public void setRegisterId(Integer registerId) {
        this.registerId = registerId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public Integer getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(Integer schoolId) {
        this.schoolId = schoolId;
    }

    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
    }

    public Integer getGradeId() {
        return gradeId;
    }

    public void setGradeId(Integer gradeId) {
        this.gradeId = gradeId;
    }

    public Grade getGrade() {
        return grade;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
    }

    public Integer getClassesId() {
        return classesId;
    }

    public void setClassesId(Integer classesId) {
        this.classesId = classesId;
    }

    public Classes getClasses() {
        return classes;
    }

    public void setClasses(Classes classes) {
        this.classes = classes;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Date getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(Date registerTime) {
        this.registerTime = registerTime;
    }

    public Date getValidateTime() {
        return validateTime;
    }

    public void setValidateTime(Date validateTime) {
        this.validateTime = validateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WxRegister)) return false;

        WxRegister that = (WxRegister) o;

        return registerId != null ? registerId.equals(that.registerId) : that.registerId == null;
    }

    @Override
    public int hashCode() {
        return registerId != null ? registerId.hashCode() : 0;
    }


}
