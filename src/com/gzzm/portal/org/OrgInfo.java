package com.gzzm.portal.org;

import com.gzzm.platform.organ.Dept;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.util.StringUtils;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

import java.util.List;

/**
 * @author lk
 * @date 13-9-26
 */
@Entity(table = "PLORGINFO", keys = "orgId")
public class OrgInfo
{
    @Generatable(length = 7)
    private Integer orgId;

    @Require
    @ColumnDescription(type = "varchar(200)", nullable = false)
    private String orgName;

    @Require
    @Unique
    private Integer deptId;

    private Dept dept;

    private Integer typeId;

    @NotSerialized
    private OrgType orgType;

    @ColumnDescription(type = "varchar(250)")
    private String address;

    @ColumnDescription(type = "varchar(250)")
    private String mainPage;

    @ColumnDescription(type = "varchar(20)")
    private String postCode;

    @ColumnDescription(type = "varchar(50)")
    private String phone;

    @ColumnDescription(type = "varchar(50)")
    private String email;

    @NotSerialized
    private char[] responsibility;

    @NotSerialized
    private char[] contactInfo;

    @NotSerialized
    private char[] department;

    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    @NotSerialized
    @ManyToMany(table = "PLORGTYPES")
    private List<OrgType> types;

    public OrgInfo()
    {
    }

    public Integer getOrgId()
    {
        return orgId;
    }

    public void setOrgId(Integer orgId)
    {
        this.orgId = orgId;
    }

    public String getOrgName()
    {
        return orgName;
    }

    public void setOrgName(String orgName)
    {
        this.orgName = orgName;
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

    public Integer getTypeId()
    {
        return typeId;
    }

    public void setTypeId(Integer typeId)
    {
        this.typeId = typeId;
    }

    public OrgType getOrgType()
    {
        return orgType;
    }

    public void setOrgType(OrgType orgType)
    {
        this.orgType = orgType;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getMainPage()
    {
        return mainPage;
    }

    public void setMainPage(String mainPage)
    {
        this.mainPage = mainPage;
    }

    public String getPostCode()
    {
        return postCode;
    }

    public void setPostCode(String postCode)
    {
        this.postCode = postCode;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public char[] getResponsibility()
    {
        return responsibility;
    }

    public void setResponsibility(char[] responsibility)
    {
        this.responsibility = responsibility;
    }

    public char[] getContactInfo()
    {
        return contactInfo;
    }

    public void setContactInfo(char[] contactInfo)
    {
        this.contactInfo = contactInfo;
    }

    public char[] getDepartment()
    {
        return department;
    }

    public void setDepartment(char[] department)
    {
        this.department = department;
    }

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
    }

    public List<OrgType> getTypes()
    {
        return types;
    }

    public void setTypes(List<OrgType> types)
    {
        this.types = types;
    }

    @NotSerialized
    public String getTypeName()
    {
        if (getTypes() == null || getTypes().size() == 0)
        {
            if (getOrgType() != null)
                return getOrgType().getTypeName();
            else
                return null;
        }
        else
        {
            return StringUtils.concat(getTypes(), ",");
        }
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof OrgInfo))
            return false;

        OrgInfo orgInfo = (OrgInfo) o;

        return orgId.equals(orgInfo.orgId);
    }

    @Override
    public int hashCode()
    {
        return orgId.hashCode();
    }

    @Override
    public String toString()
    {
        return orgName;
    }
}
