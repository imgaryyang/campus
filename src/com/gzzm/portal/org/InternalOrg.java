package com.gzzm.portal.org;

import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.*;

/**
 * 机构简介--内设机构
 *
 * @author zyd
 * @date 15-5-25
 */
@Entity(table = "PLINTERNALORG", keys = "internalId")
public class InternalOrg
{
    @Generatable(length = 7)
    private Integer internalId;

    /**
     * 机构简介主键
     */
    private Integer orgId;

    @NotSerialized
    private OrgInfo orgInfo;

    /**
     * 部门名称
     */
    @Require
    @ColumnDescription(type = "varchar(200)")
    private String deptName;

    @ColumnDescription(type = "varchar(100)")
    private String phone;

    @ColumnDescription(type = "varchar(100)")
    private String fax;

    @ColumnDescription(type = "varchar(100)")
    private String email;

    private char[] duty;

    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    public InternalOrg()
    {
    }

    public Integer getInternalId()
    {
        return internalId;
    }

    public void setInternalId(Integer internalId)
    {
        this.internalId = internalId;
    }

    public Integer getOrgId()
    {
        return orgId;
    }

    public void setOrgId(Integer orgId)
    {
        this.orgId = orgId;
    }

    public OrgInfo getOrgInfo()
    {
        return orgInfo;
    }

    public void setOrgInfo(OrgInfo orgInfo)
    {
        this.orgInfo = orgInfo;
    }

    public String getDeptName()
    {
        return deptName;
    }

    public void setDeptName(String deptName)
    {
        this.deptName = deptName;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getFax()
    {
        return fax;
    }

    public void setFax(String fax)
    {
        this.fax = fax;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public char[] getDuty()
    {
        return duty;
    }

    public void setDuty(char[] duty)
    {
        this.duty = duty;
    }

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof InternalOrg))
            return false;

        InternalOrg that = (InternalOrg) o;

        return internalId.equals(that.internalId);
    }

    @Override
    public int hashCode()
    {
        return internalId.hashCode();
    }
}
