package com.gzzm.ods.dict;

import com.gzzm.platform.organ.Dept;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

/**
 * 用章实体
 *
 * @author db
 * @date 11-12-31
 */
@Entity(table = "ODSEAL", keys = "sealId")
public class Seal
{
    /**
     * 主键
     */
    @Generatable(length = 7)
    private Integer sealId;

    /**
     * 用章名称
     */
    @Require
    @Unique(with = "deptId")
    @ColumnDescription(type = "varchar(250)")
    private String sealName;

    /**
     * 排序
     */
    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    /**
     * 所属部门
     * 关联部门实体
     */
    private Integer deptId;

    @NotSerialized
    private Dept dept;

    public Seal()
    {
    }

    public Integer getSealId()
    {
        return sealId;
    }

    public void setSealId(Integer sealId)
    {
        this.sealId = sealId;
    }

    public String getSealName()
    {
        return sealName;
    }

    public void setSealName(String sealName)
    {
        this.sealName = sealName;
    }

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
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

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof Seal))
            return false;

        Seal seal = (Seal) o;

        return sealId.equals(seal.sealId);
    }

    @Override
    public int hashCode()
    {
        return sealId.hashCode();
    }

    @Override
    public String toString()
    {
        return sealName;
    }
}
