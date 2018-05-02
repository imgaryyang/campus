package com.gzzm.platform.organ.syn;

import com.gzzm.platform.commons.*;

import java.io.Serializable;
import java.sql.Date;
import java.util.*;

/**
 * 同步的用户信息
 *
 * @author camel
 * @date 2011-4-19
 */
public class SynUserInfo implements Serializable
{
    private static final long serialVersionUID = -1666387666451422092L;

    /**
     * 用户ID，原部门的用户Id
     */
    private String userId;

    /**
     * 用户登录名
     */
    private String loginName;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码
     */
    private String password;

    /**
     * 性别
     */
    private Sex sex;

    /**
     * 手机号码
     */
    private String phone;

    private String officePhone;

    /**
     * 证书id
     */
    private String certId;

    /**
     * 证书类型
     */
    private String certType;

    /**
     * 是否已经被删除
     */
    private int state;

    private String mail;

    private IdCardType idCardType;

    /**
     * 证件号码，身份证或者军官证
     */
    private String idCardNo;

    /**
     * 工作时间
     */
    private java.sql.Date workday;

    /**
     * 此用户所属的部门信息，包括所属部门的id和用户在此部门的排序
     */
    private List<SynUserDeptInfo> depts;

    public SynUserInfo()
    {
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public String getLoginName()
    {
        return loginName;
    }

    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
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

    public String getCertId()
    {
        return certId;
    }

    public void setCertId(String certId)
    {
        this.certId = certId;
    }

    public String getCertType()
    {
        return certType;
    }

    public void setCertType(String certType)
    {
        this.certType = certType;
    }

    public int getState()
    {
        return state;
    }

    public void setState(int state)
    {
        this.state = state;
    }

    public String getMail()
    {
        return mail;
    }

    public void setMail(String mail)
    {
        this.mail = mail;
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

    public Date getWorkday()
    {
        return workday;
    }

    public void setWorkday(Date workday)
    {
        this.workday = workday;
    }

    public List<SynUserDeptInfo> getDepts()
    {
        return depts;
    }

    public void setDepts(List<SynUserDeptInfo> depts)
    {
        this.depts = depts;
    }

    public SynUserDeptInfo addDept(String deptId, int sort)
    {
        if (depts == null)
        {
            depts = new ArrayList<SynUserDeptInfo>();
        }
        else
        {
            for (SynUserDeptInfo dept : depts)
            {
                if (dept.getDeptId().equals(deptId))
                    return dept;
            }
        }

        SynUserDeptInfo dept = new SynUserDeptInfo(deptId, sort);
        depts.add(dept);

        return dept;
    }

    public SynUserDeptInfo getDept(String deptId)
    {
        if (depts != null)
        {
            for (SynUserDeptInfo dept : depts)
            {
                if (dept.getDeptId().equals(deptId))
                    return dept;
            }
        }

        return null;
    }
}