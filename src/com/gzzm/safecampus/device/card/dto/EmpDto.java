package com.gzzm.safecampus.device.card.dto;

import com.gzzm.safecampus.device.card.annotation.ReflectionField;

/**
 * @author liyabin
 * @date 2018/3/22
 */
public class EmpDto
{
    @ReflectionField({"studentNo"})
    private String empId;
    @ReflectionField({"studentName"})
    private String empName;
    private String sex;
    public EmpDto()
    {
    }
    public EmpDto(String empId, String empName, String sex)
    {
        this.empId = empId;
        this.empName = empName;
        this.sex = sex;
    }

    public String getEmpId()
    {
        return empId;
    }

    public void setEmpId(String empId)
    {
        this.empId = empId;
    }

    public String getEmpName()
    {
        return empName;
    }

    public void setEmpName(String empName)
    {
        this.empName = empName;
    }

    public String getSex()
    {
        return sex;
    }

    public void setSex(String sex)
    {
        this.sex = sex;
    }
}
