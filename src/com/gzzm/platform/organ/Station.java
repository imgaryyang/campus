package com.gzzm.platform.organ;

import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

/**
 * 岗位
 *
 * @author camel
 * @date 2009-7-18
 */
@Entity(table = "PFSTATION", keys = "stationId")
public class Station
{
    /**
     * 岗位id，长度为8,前2位为系统id，后6位为序列号
     */
    @Generatable(length = 8)
    private Integer stationId;

    /**
     * 岗位名称
     */
    @Require
    @Unique(with = "deptId")
    @ColumnDescription(type = "varchar(250)", nullable = false)
    private String stationName;

    /**
     * 岗位所属的部门的id
     */
    @Index
    private Integer deptId;

    /**
     * 岗位所属的部门
     */
    @NotSerialized
    private Dept dept;

    /**
     * 岗位说明
     */
    private String remark;

    /**
     * 排序号，用于展示时将岗位排序，只对属于同一个部门的岗位排序
     */
    private Integer orderId;

    /**
     * 能否被子部门使用
     */
    @Require
    @ColumnDescription(nullable = false, defaultValue = "0")
    private Boolean inheritable;

    public Station()
    {
    }

    public Integer getStationId()
    {
        return stationId;
    }

    public void setStationId(Integer stationId)
    {
        this.stationId = stationId;
    }

    public String getStationName()
    {
        return stationName;
    }

    public void setStationName(String stationName)
    {
        this.stationName = stationName;
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

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public Boolean getInheritable()
    {
        return inheritable;
    }

    public void setInheritable(Boolean inheritable)
    {
        this.inheritable = inheritable;
    }

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
    }

    public boolean equals(Object o)
    {
        return this == o || o instanceof Station && stationId.equals(((Station) o).stationId);
    }

    public int hashCode()
    {
        return stationId.hashCode();
    }

    public String toString()
    {
        return stationName;
    }
}
