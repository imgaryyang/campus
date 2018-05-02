package com.gzzm.safecampus.campus.pay;

import com.gzzm.safecampus.campus.account.School;
import net.cyan.thunwind.annotation.*;

import java.sql.Date;
import java.util.*;

/**
 * 存储招行给学校开通的缴费服务
 *
 * @author yuanfang
 * @date 18-03-06 16:05
 */
@Entity(table = "SCSCHOOLPAYSERVICE", keys = "spId")
public class SchoolPayService
{

    @Generatable(length = 6)
    private Integer spId;

    /**
     * 所属服务
     */
    @ToOne("SERVICEID")
    private PayService payService;

    private Integer serviceId;

    /**
     * 所属学校
     */
    @ToOne("SCHOOLID")
    private School school;

    private Integer schoolId;

    /**
     * 开通日期
     */
    private Date registerDate;

    /**
     * 开通人
     */

    private Integer registerId;

    /**
     * 开通部门
     */
    private Integer registerDeptId;

    @ColumnDescription(defaultValue = "0")
    private PayServiceStatus payServiceStatus;

    public SchoolPayService()
    {
    }

    public PayService getPayService()
    {
        return payService;
    }

    public void setPayService(PayService payService)
    {
        this.payService = payService;
    }

    public Integer getServiceId()
    {
        return serviceId;
    }

    public void setServiceId(Integer serviceId)
    {
        this.serviceId = serviceId;
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

    public PayServiceStatus getPayServiceStatus()
    {
        return payServiceStatus;
    }

    public void setPayServiceStatus(PayServiceStatus payServiceStatus)
    {
        this.payServiceStatus = payServiceStatus;
    }

    public Integer getSpId()
    {
        return spId;
    }

    public void setSpId(Integer spId)
    {
        this.spId = spId;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SchoolPayService that = (SchoolPayService) o;
        return Objects.equals(spId, that.spId);
    }

    @Override
    public int hashCode()
    {

        return Objects.hash(spId);
    }

    @Override
    public String toString()
    {
        return spId + "";
    }
}
