package com.gzzm.portal.org;

import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.*;

/**
 * @author lk
 * @date 13-9-29
 */
@Entity(table = "PLLEADER", keys = "leaderId")
public class Leader
{
    @Generatable(length = 7)
    private Integer leaderId;

    @Require
    @ColumnDescription(type = "varchar(50)", nullable = false)
    private String leaderName;

    private Integer orgId;

    @NotSerialized
    private OrgInfo org;

    private Integer deptId;

    @NotSerialized
    private Dept dept;

    private Integer titleId;

    @NotSerialized
    private LeaderTitle title;

    @ColumnDescription(type = "varchar(200)")
    private String titleName;

    @ColumnDescription(type = "varchar(1000)")
    private String responsibility;

    @ColumnDescription(type = "varchar(50)")
    private String phone;

    @ColumnDescription(type = "varchar(250)")
    private String email;

    @ColumnDescription(type = "varchar(250)")
    private String workAddress;

    private char[] resume;

    private char[] introduction;

    private byte[] photo;

    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    private Integer userId;

    @NotSerialized
    private User user;

    @ColumnDescription(defaultValue = "1", nullable = false)
    private Boolean valid;

    public Leader()
    {
    }

    public Integer getLeaderId()
    {
        return leaderId;
    }

    public void setLeaderId(Integer leaderId)
    {
        this.leaderId = leaderId;
    }

    public String getLeaderName()
    {
        return leaderName;
    }

    public void setLeaderName(String leaderName)
    {
        this.leaderName = leaderName;
    }

    public Integer getOrgId()
    {
        return orgId;
    }

    public void setOrgId(Integer orgId)
    {
        this.orgId = orgId;
    }

    public OrgInfo getOrg()
    {
        return org;
    }

    public void setOrg(OrgInfo org)
    {
        this.org = org;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public Dept getDept()
    {
        return dept;
    }

    public void setDept(Dept dept)
    {
        this.dept = dept;
    }

    public Integer getTitleId()
    {
        return titleId;
    }

    public void setTitleId(Integer titleId)
    {
        this.titleId = titleId;
    }

    public LeaderTitle getTitle()
    {
        return title;
    }

    public void setTitle(LeaderTitle title)
    {
        this.title = title;
    }

    public String getTitleName()
    {
        return titleName;
    }

    public void setTitleName(String titleName)
    {
        this.titleName = titleName;
    }

    public String getResponsibility()
    {
        return responsibility;
    }

    public void setResponsibility(String responsibility)
    {
        this.responsibility = responsibility;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public char[] getResume()
    {
        return resume;
    }

    public void setResume(char[] resume)
    {
        this.resume = resume;
    }

    public char[] getIntroduction()
    {
        return introduction;
    }

    public void setIntroduction(char[] introduction)
    {
        this.introduction = introduction;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getWorkAddress()
    {
        return workAddress;
    }

    public void setWorkAddress(String workAddress)
    {
        this.workAddress = workAddress;
    }

    public byte[] getPhoto()
    {
        return photo;
    }

    public void setPhoto(byte[] photo)
    {
        this.photo = photo;
    }

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
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

    public Boolean getValid()
    {
        return valid;
    }

    public void setValid(Boolean valid)
    {
        this.valid = valid;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof Leader))
            return false;

        Leader leader = (Leader) o;

        return leaderId.equals(leader.leaderId);
    }

    @Override
    public int hashCode()
    {
        return leaderId.hashCode();
    }

    @Override
    public String toString()
    {
        return leaderName;
    }
}
