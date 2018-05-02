package com.gzzm.ods.exchange.back;

import com.gzzm.platform.organ.Dept;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.*;

/**
 * 退文理由
 *
 * @author ldp
 * @date 2017/11/24
 */
@Entity(table = "ODBACKREASON", keys = "reasonId")
public class BackReason
{
    @Generatable(length = 9)
    private Integer reasonId;

    @Index
    private Integer deptId;

    @NotSerialized
    private Dept dept;

    @Require
    @ColumnDescription(type = "varchar(2000)")
    private String reason;

    /**
     * 所属类型
     */
    @Require
    private Integer typeId;

    @NotSerialized
    private BackReasonType type;

    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    public BackReason()
    {
    }

    public Integer getReasonId()
    {
        return reasonId;
    }

    public void setReasonId(Integer reasonId)
    {
        this.reasonId = reasonId;
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

    public String getReason()
    {
        return reason;
    }

    public void setReason(String reason)
    {
        this.reason = reason;
    }

    public Integer getTypeId()
    {
        return typeId;
    }

    public void setTypeId(Integer typeId)
    {
        this.typeId = typeId;
    }

    public BackReasonType getType()
    {
        return type;
    }

    public void setType(BackReasonType type)
    {
        this.type = type;
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

        if (!(o instanceof BackReason))
            return false;

        BackReason that = (BackReason) o;

        return reasonId.equals(that.reasonId);
    }

    @Override
    public int hashCode()
    {
        return reasonId.hashCode();
    }

    @Override
    public String toString()
    {
        return reason;
    }
}
