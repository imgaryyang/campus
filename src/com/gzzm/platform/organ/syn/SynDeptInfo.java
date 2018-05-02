package com.gzzm.platform.organ.syn;

import java.io.Serializable;

/**
 * 要同步的部门信息
 *
 * @author camel
 * @date 2011-4-19
 */
public class SynDeptInfo implements Serializable
{
    private static final long serialVersionUID = -6690426392855785976L;

    /**
     * 部门id，指原系统中的部门ID
     */
    private String deptId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 部门级别,由本系统定义
     */
    private byte level;

    /**
     * 上级部门id，指原系统的id
     */
    private String parentDeptId;

    /**
     * 部门编号
     */
    private String deptCode;

    /**
     * 部门简称
     */
    private String shortName;

    /**
     * 部门简码
     */
    private String shortCode;

    /**
     * 部门排序，在同一级部门中的序号
     */
    private Integer deptSort;

    /**
     * 是否已经删除
     */
    private boolean deleted;

    public SynDeptInfo()
    {
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

    public byte getLevel()
    {
        return level;
    }

    public void setLevel(byte level)
    {
        this.level = level;
    }

    public String getParentDeptId()
    {
        return parentDeptId;
    }

    public void setParentDeptId(String parentDeptId)
    {
        this.parentDeptId = parentDeptId;
    }

    public String getDeptCode()
    {
        return deptCode;
    }

    public void setDeptCode(String deptCode)
    {
        this.deptCode = deptCode;
    }

    public String getShortName()
    {
        return shortName;
    }

    public void setShortName(String shortName)
    {
        this.shortName = shortName;
    }

    public String getShortCode()
    {
        return shortCode;
    }

    public void setShortCode(String shortCode)
    {
        this.shortCode = shortCode;
    }

    public Integer getDeptSort()
    {
        return deptSort;
    }

    public void setDeptSort(Integer deptSort)
    {
        this.deptSort = deptSort;
    }

    public boolean isDeleted()
    {
        return deleted;
    }

    public void setDeleted(boolean deleted)
    {
        this.deleted = deleted;
    }
}
