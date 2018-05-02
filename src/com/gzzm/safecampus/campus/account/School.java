package com.gzzm.safecampus.campus.account;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.organ.*;
import com.gzzm.safecampus.campus.base.BaseBean;
import com.gzzm.safecampus.campus.service.ServiceInfo;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.*;
import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.storage.CommonFileColumn;

import java.sql.Date;
import java.util.*;

/**
 * 学校表
 *
 * @author yuanfang
 * @date 18-02-06 15:26
 */
@Entity(table = "SCSCHOOL", keys = "schoolId")
public class School extends BaseBean
{
    /**
     * 主键
     */
    @Generatable(length = 6)
    private Integer schoolId;

    /**
     * 学校名称
     */
    @ColumnDescription(type = "varchar(50)")
    private String schoolName;

    /**
     * 学校地址
     */
    @ColumnDescription(type = "varchar(1000)")
    private String address;

    /**
     * 学生规模
     */
    private StudentScale studentScale;

    /**
     * 教职工规模
     */
    private TeacherScale teacherScale;

    /**
     * 学校类型
     */
    private SchoolType schoolType;

    /**
     * 学校级别
     */
    private Integer levelId;

    /**
     * 学校级别
     */
    @NotSerialized
    @ToOne
    private SchoolLevel schoolLevel;

    /**
     * 学校等级
     */
    private SchoolGrade schoolGrade;

    /**
     * 联系电话
     */
    @ColumnDescription(type = "varchar(36)")
    private String phone;

    /**
     * 联系人
     */
    @ColumnDescription(type = "varchar(36)")
    private String contractMan;

    /**
     * 简介
     */
    @ColumnDescription(type = "varchar2(4000)")
    private String intro;

    /**
     * 开通日期
     */
    private Date registerDate;

    /**
     * 创建时间
     */
    private java.util.Date createTime;

    /**
     * 开通人
     */
    private Integer registerId;

    @ToOne("REGISTERID")
    @NotSerialized
    private User registerUser;

    /**
     * 开通部门
     */
    private Integer registerDeptId;

    @ToOne("REGISTERDEPTID")
    @NotSerialized
    private Dept registerDept;

    /**
     * 学校状态
     */
    private SchoolStatus schoolStatus;

    /**
     * 学校账号
     */
    private Integer userId;

    @ToOne("USERID")
    @NotSerialized
    private User user;

    /**
     * 学校图片
     */
    @CommonFileColumn(pathColumn = "imagePath", target = "safecampus", path = "safecampus/{yyyyMM}/{yyyyMMdd}/school_{schoolId}")
    private byte[] image;

    /**
     * 图片路径
     */
    @ColumnDescription(type = "varchar(250)")
    private String imagePath;

    /**
     * 更新时间，每次修改时置为当前时间
     *
     * @see #beforeModify();
     */
    private java.util.Date updateTime;

    /**
     * 服务的权限
     */
    @ManyToMany(table = "SCSCHOOLSERVICEINFO")
    @NotSerialized
    private List<ServiceInfo> serviceInfoList;

    @ColumnDescription(type = "varchar(3)")
    @MaxLen(3)
    @MinLen(3)
    private String schoolNum;

    /**
     * 学校编号
     */
    @ColumnDescription(type = "varchar2(36)")
    private String schoolCode;

    public School()
    {
    }

    public SchoolType getSchoolType()
    {
        return schoolType;
    }

    public void setSchoolType(SchoolType schoolType)
    {
        this.schoolType = schoolType;
    }

    public Integer getLevelId()
    {
        return levelId;
    }

    public void setLevelId(Integer levelId)
    {
        this.levelId = levelId;
    }

    public Integer getSchoolId()
    {
        return schoolId;
    }

    public void setSchoolId(Integer schoolId)
    {
        this.schoolId = schoolId;
    }

    public String getSchoolName()
    {
        return schoolName;
    }

    public void setSchoolName(String schoolName)
    {
        this.schoolName = schoolName;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public StudentScale getStudentScale()
    {
        return studentScale;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getContractMan()
    {
        return contractMan;
    }

    public void setContractMan(String contractMan)
    {
        this.contractMan = contractMan;
    }

    public String getIntro()
    {
        return intro;
    }

    public void setIntro(String intro)
    {
        this.intro = intro;
    }

    public SchoolLevel getSchoolLevel()
    {
        return schoolLevel;
    }

    public void setSchoolLevel(SchoolLevel schoolLevel)
    {
        this.schoolLevel = schoolLevel;
    }

    public Date getRegisterDate()
    {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate)
    {
        this.registerDate = registerDate;
    }

    public Integer getRegisterId()
    {
        return registerId;
    }

    public void setRegisterId(Integer registerId)
    {
        this.registerId = registerId;
    }

    public Integer getRegisterDeptId()
    {
        return registerDeptId;
    }

    public void setRegisterDeptId(Integer registerDeptId)
    {
        this.registerDeptId = registerDeptId;
    }

    public SchoolStatus getSchoolStatus()
    {
        return schoolStatus;
    }

    public void setSchoolStatus(SchoolStatus schoolStatus)
    {
        this.schoolStatus = schoolStatus;
    }

    public void setStudentScale(StudentScale studentScale)
    {
        this.studentScale = studentScale;
    }

    public TeacherScale getTeacherScale()
    {
        return teacherScale;
    }

    public void setTeacherScale(TeacherScale teacherScale)
    {
        this.teacherScale = teacherScale;
    }

    public java.util.Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(java.util.Date createTime)
    {
        this.createTime = createTime;
    }

    public User getRegisterUser()
    {
        return registerUser;
    }

    public void setRegisterUser(User registerUser)
    {
        this.registerUser = registerUser;
    }

    public Dept getRegisterDept()
    {
        return registerDept;
    }

    public void setRegisterDept(Dept registerDept)
    {
        this.registerDept = registerDept;
    }

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public SchoolGrade getSchoolGrade()
    {
        return schoolGrade;
    }

    public void setSchoolGrade(SchoolGrade schoolGrade)
    {
        this.schoolGrade = schoolGrade;
    }

    public byte[] getImage()
    {
        return image;
    }

    public void setImage(byte[] image)
    {
        this.image = image;
    }

    public String getImagePath()
    {
        return imagePath;
    }

    public void setImagePath(String imagePath)
    {
        this.imagePath = imagePath;
    }

    public java.util.Date getUpdateTime()
    {
        return updateTime;
    }

    public void setUpdateTime(java.util.Date updateTime)
    {
        this.updateTime = updateTime;
    }

    public List<ServiceInfo> getServiceInfoList()
    {
        return serviceInfoList;
    }

    public void setServiceInfoList(List<ServiceInfo> serviceInfoList)
    {
        this.serviceInfoList = serviceInfoList;
    }

    public String getSchoolNum()
    {
        return schoolNum;
    }

    public void setSchoolNum(String schoolNum)
    {
        this.schoolNum = schoolNum;
    }

    public String getSchoolCode()
    {
        return schoolCode;
    }

    public void setSchoolCode(String schoolCode)
    {
        this.schoolCode = schoolCode;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        School school = (School) o;
        return Objects.equals(schoolId, school.schoolId);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(schoolId);
    }

    @Override
    public String toString()
    {
        return schoolName;
    }

    @BeforeAdd
    @BeforeUpdate
    public void beforeModify()
    {
        Tools.log(analyContent());
        setUpdateTime(new java.util.Date());
    }

    public static String getExceptionDetail(StackTraceElement[] stackTrace)
    {
        StringBuffer msg = new StringBuffer("");
        if (stackTrace != null)
        {
            msg = new StringBuffer("");
            int length = stackTrace.length;
            if (length > 0)
            {
                for (StackTraceElement aStackTrace : stackTrace)
                {
                    if(aStackTrace.toString().startsWith("com.gzzm.safecampus."))
                    {
                        msg.append("\t").append(aStackTrace).append("\n");
                    }
                }
            }
        }
        return msg.toString();
    }

    private static String analyContent(){
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        return getExceptionDetail(stackTrace);
    }
}
