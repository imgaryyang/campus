package com.gzzm.safecampus.device.card.dto;

/**
 * @author liyabin
 * @date 2018/3/22
 */
public class DeptDto
{
    private String deptId;
    private String deptName;
    private String parentDeptId;
    public DeptDto()
    {
    }

    public DeptDto(String deptId, String deptName, String parentDeptId)
    {
        this.deptId = deptId;
        this.deptName = deptName;
        this.parentDeptId = parentDeptId;
    }

    public String getDeptId()
    {
        return deptId;
    }

    public void setDeptId(String deptId)
    {
        this.deptId = deptId;
    }

    public String getDeptName()
    {
        return deptName;
    }

    public void setDeptName(String deptName)
    {
        this.deptName = deptName;
    }

    public String getParentDeptId()
    {
        return parentDeptId;
    }

    public void setParentDeptId(String parentDeptId)
    {
        this.parentDeptId = parentDeptId;
    }
}
