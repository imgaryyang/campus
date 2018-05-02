package com.gzzm.platform.organ;

import com.gzzm.platform.commons.*;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.util.*;
import net.cyan.commons.validate.annotation.*;
import net.cyan.nest.annotation.Inject;
import net.cyan.thunwind.annotation.*;

import java.sql.Date;
import java.util.*;

/**
 * 用户实体对象，对应数据库的用户表
 *
 * @author camel
 * @date 2009-7-18
 */
@Entity(table = "PFUSER", keys = "userId")
@Indexes({
        @Index(columns = {"USERNAME", "PHONE"})
})
@AutoAdd(false)
public class User
{
    @Inject
    private static Provider<OrganDao> daoProvider;

    /**
     * 用户id，长度为9,前2位为系统id，后7位为序列号
     */
    @Generatable(name = "PFUSER_USERID", length = 9)
    private Integer userId;

    @Require
    @ColumnDescription(type = "varchar(250)", nullable = false)
    private String userName;

    /**
     * 登录名称
     */
    @Require
    @ColumnDescription(type = "varchar(250)")
    @Index
    private String loginName;

    /**
     * 密码
     */
    @ColumnDescription(type = "varchar(250)", nullable = false)
    private String password;

    /**
     * 证书id，一个用户可以有多个证书，多个证书的id之间用空格分开
     */
    @ColumnDescription(type = "varchar(600)")
    private String certId;

    /**
     * 证书类型，可以同时使用多个证书验证系统
     */
    @ColumnDescription(type = "varchar(20)")
    private String certType;

    /**
     * 拼音
     */
    @ColumnDescription(type = "varchar(250)")
    private String spell;

    /**
     * 姓名简拼
     */
    @ColumnDescription(type = "varchar(250)")
    private String simpleSpell;

    /**
     * 登录方式
     */
    @Require
    @ColumnDescription(nullable = false, defaultValue = "0")
    private LoginType loginType;

    /**
     * 状态,0为正常，1为冻结，2为删除
     */
    @ColumnDescription(type = "tinyint(1)", nullable = false, defaultValue = "0")
    private Byte state;

    /**
     * 性别
     */
    @Require
    private Sex sex;

    /**
     * 电话
     */
    @ColumnDescription(type = "varchar(50)")
    @Pattern(Patterns.MOIBLE_PHONE)
    private String phone;

    /**
     * 办公电话
     */
    @ColumnDescription(type = "varchar(50)")
    @Pattern(Patterns.TELEPHONE)
    private String officePhone;

    /**
     * 附加属性
     */
    @ValueMap(table = "PFUSERATTRIBUTE", keyColumn = "ATTRIBUTENAME", valueColumn = "ATTRIBUTEVALUE",
            clearForUpdate = false)
    @NotSerialized
    private Map<String, String> attributes;

    /**
     * 是否为管理员
     */
    @ColumnDescription(nullable = false, defaultValue = "0")
    private Boolean adminUser;

    /**
     * 用户类型，0为内网用户，1为外网用户
     */
    @ColumnDescription(defaultValue = "0")
    private UserType type;

    @NotSerialized
    @ManyToMany(table = "PFUSERDEPT")
    @OrderBy(column = "USERORDER")
    private List<Dept> depts;

    /**
     * 生日
     */
    private Date birthday;

    /**
     * 工作时间
     */
    private Date workday;

    /**
     * 工号
     */
    @ColumnDescription(type = "varchar(50)")
    private String workno;

    /**
     * 部门数据查看类型，0表示只查看登录部门的数据，1表示查看全部部门数据
     */
    @ColumnDescription(nullable = false, defaultValue = "0")
    private Integer deptDataType;

    /**
     * 岗位
     */
    @OneToMany
    @NotSerialized
    private List<UserStation> stations;

    /**
     * 岗位
     */
    @OneToMany
    @NotSerialized
    private List<UserRole> roles;

    /**
     * 源Id，当做系统接口时，从其他系统同步用户数据到系统中，标识用户在源系统中的id
     * 如果数据不是从其他系统同步过来，则此字段为空
     */
    @ColumnDescription(type = "varchar(50)")
    @Index
    private String sourceId;

    /**
     * 源系统的邮件地址，从其他系统同步用户数据到系统中时，发送邮件可以通过此地址直接发到源系统中
     */
    @ColumnDescription(type = "varchar(50)")
    private String sourceMail;

    private IdCardType idCardType;

    /**
     * 证件号码，身份证或者军官证
     */
    @ColumnDescription(type = "varchar(50)")
    private String idCardNo;

    /**
     * 全局排序ID
     */
    @NotSerialized
    @ComputeColumn("nvl(min(select ud.dept.leftValue*1000000+nvl(ud.orderId,0) from UserDept ud " +
            "where ud.userId=this.userId and ud.dept.state=0),999000000+userId)")
    private Integer sortId;

    /**
     * 用户级别
     */
    private Integer levelId;

    @NotSerialized
    private UserLevel userLevel;

    @ColumnDescription(type = "varchar(200)")
    private String duty;

    public User()
    {
    }

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getLoginName()
    {
        return loginName;
    }

    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getCertId()
    {
        return certId;
    }

    public void setCertId(String certId)
    {
        this.certId = certId;
    }

    public String getSpell()
    {
        return spell;
    }

    public void setSpell(String spell)
    {
        this.spell = spell;
    }

    public String getSimpleSpell()
    {
        return simpleSpell;
    }

    public void setSimpleSpell(String simpleSpell)
    {
        this.simpleSpell = simpleSpell;
    }

    public LoginType getLoginType()
    {
        return loginType;
    }

    public void setLoginType(LoginType loginType)
    {
        this.loginType = loginType;
    }

    public Byte getState()
    {
        return state;
    }

    public void setState(Byte state)
    {
        this.state = state;
    }

    public Sex getSex()
    {
        return sex;
    }

    public void setSex(Sex sex)
    {
        this.sex = sex;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getOfficePhone()
    {
        return officePhone;
    }

    public void setOfficePhone(String officePhone)
    {
        this.officePhone = officePhone;
    }

    public String getCertType()
    {
        return certType;
    }

    public void setCertType(String certType)
    {
        this.certType = certType;
    }

    public UserType getType()
    {
        return type;
    }

    public void setType(UserType type)
    {
        this.type = type;
    }

    public Map<String, String> getAttributes()
    {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes)
    {
        this.attributes = attributes;
    }

    public Boolean getAdminUser()
    {
        return adminUser;
    }

    public void setAdminUser(Boolean adminUser)
    {
        this.adminUser = adminUser;
    }

    public List<Dept> getDepts()
    {
        return depts;
    }

    public void setDepts(List<Dept> depts)
    {
        this.depts = depts;
    }

    public Date getBirthday()
    {
        return birthday;
    }

    public void setBirthday(Date birthday)
    {
        this.birthday = birthday;
    }

    public Date getWorkday()
    {
        return workday;
    }

    public void setWorkday(Date workday)
    {
        this.workday = workday;
    }

    public String getWorkno()
    {
        return workno;
    }

    public void setWorkno(String workno)
    {
        this.workno = workno;
    }

    public Integer getWorkingAge(int year)
    {
        if (workday == null)
            return null;

        return year - DateUtils.getYear(workday);
    }

    @NotSerialized
    public Integer getWorkingAge()
    {
        return getWorkingAge(DateUtils.getYear(new java.util.Date()));
    }

    public Integer getWorkingMonth(int year, int month)
    {
        if (workday == null)
            return null;

        int year0 = DateUtils.getYear(workday);
        int month0 = DateUtils.getMonth(workday);

        return (year - year0) * 12 + (month - month0);
    }

    public Integer getWorkingMonth()
    {
        java.util.Date date = new java.util.Date();
        return getWorkingMonth(DateUtils.getYear(date), DateUtils.getMonth(date));
    }

    public Integer getDeptDataType()
    {
        return deptDataType;
    }

    public void setDeptDataType(Integer deptDataType)
    {
        this.deptDataType = deptDataType;
    }

    public List<UserStation> getStations()
    {
        return stations;
    }

    public void setStations(List<UserStation> stations)
    {
        this.stations = stations;
    }

    public List<UserRole> getRoles()
    {
        return roles;
    }

    public void setRoles(List<UserRole> roles)
    {
        this.roles = roles;
    }

    public String getSourceId()
    {
        return sourceId;
    }

    public void setSourceId(String sourceId)
    {
        this.sourceId = sourceId;
    }

    public String getSourceMail()
    {
        return sourceMail;
    }

    public void setSourceMail(String sourceMail)
    {
        this.sourceMail = sourceMail;
    }

    public IdCardType getIdCardType()
    {
        return idCardType;
    }

    public void setIdCardType(IdCardType idCardType)
    {
        this.idCardType = idCardType;
    }

    public String getIdCardNo()
    {
        return idCardNo;
    }

    public void setIdCardNo(String idCardNo)
    {
        this.idCardNo = idCardNo;
    }

    public Integer getSortId()
    {
        return sortId;
    }

    public void setSortId(Integer sortId)
    {
        this.sortId = sortId;
    }

    public Integer getLevelId()
    {
        return levelId;
    }

    public void setLevelId(Integer levelId)
    {
        this.levelId = levelId;
    }

    public UserLevel getUserLevel()
    {
        return userLevel;
    }

    public void setUserLevel(UserLevel userLevel)
    {
        this.userLevel = userLevel;
    }

    public String getDuty()
    {
        return duty;
    }

    public void setDuty(String duty)
    {
        this.duty = duty;
    }

    public String allDeptName()
    {
        List<Dept> depts = getDepts();
        if (depts.size() == 1)
            return depts.get(0).getAllName(1);

        StringBuilder buffer = new StringBuilder();

        for (Dept dept : depts)
        {
            if (dept.getDeptId() > 1)
            {
                if (buffer.length() > 0)
                    buffer.append(",");
                buffer.append(dept.getAllName(1));
            }
        }

        return buffer.toString();
    }

    public String allSimpleDeptName()
    {
        List<Dept> depts = getDepts();
        if (depts.size() == 1)
            return depts.get(0).getDeptName();

        StringBuilder buffer = new StringBuilder();

        for (Dept dept : depts)
        {
            if (dept.getDeptId() > 1)
            {
                if (buffer.length() > 0)
                    buffer.append(",");
                buffer.append(dept.getDeptName());
            }
        }

        return buffer.toString();
    }

    @SuppressWarnings("LoopStatementThatDoesntLoop")
    public String firstDeptName()
    {
        for (Dept dept : getDepts())
        {
            return dept.getAllName(0);
        }

        return "";
    }

    @SuppressWarnings("LoopStatementThatDoesntLoop")
    public String firstDeptName(int level)
    {
        for (Dept dept : getDepts())
        {
            return dept.getAllName(level);
        }

        return "";
    }

    @SuppressWarnings("LoopStatementThatDoesntLoop")
    public String firstSimpleDeptName()
    {
        for (Dept dept : getDepts())
        {
            return dept.getDeptName();
        }

        return "";
    }

    @SuppressWarnings("LoopStatementThatDoesntLoop")
    public String firstSimpleDeptName(int level)
    {
        for (Dept dept : getDepts())
        {
            return dept.getParentDept(level).getDeptName();
        }

        return "";
    }

    public String allStationName()
    {
        return StringUtils.concat(getStations(), ",");
    }

    public String allRoleName()
    {
        return StringUtils.concat(getRoles(), ",");
    }

    public boolean equals(Object o)
    {
        return this == o || o instanceof User && userId.equals(((User) o).userId);
    }

    public int hashCode()
    {
        return userId.hashCode();
    }

    public String toString()
    {
        return userName;
    }

    public static String getSpell(String userName)
    {
        return Chinese.getLetters(userName);
    }

    @BeforeAdd
    @BeforeUpdate
    public void beforeModify() throws Exception
    {
        //设置简拼和全拼
        String userName = getUserName();
        if (userName != null)
        {
            setSpell(getSpell(userName));
            setSimpleSpell(Chinese.getFirstLetters(userName));
        }
    }

    @FieldValidator("loginName")
    @Warning("user.loginname_exists")
    public User checkLoginName() throws Exception
    {
        return !StringUtils.isEmpty(getLoginName()) ?
                daoProvider.get().getUserByLoginName(getLoginName(), getUserId(), type) : null;
    }

    @FieldValidator("certId")
    @Warning("user.certid_exists")
    public User checkCertId() throws Exception
    {
        if (!StringUtils.isEmpty(getCertId()))
        {
            for (String certId : getCertId().trim().split(" "))
            {
                if (certId.length() > 0)
                {
                    User user = daoProvider.get().getUserByCert(certId, getCertType(), getUserId());

                    if (user != null)
                    {
                        user.setCertId(certId);
                        return user;
                    }
                }
            }
        }

        return null;
    }
}
