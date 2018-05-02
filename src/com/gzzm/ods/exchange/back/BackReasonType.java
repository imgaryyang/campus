package com.gzzm.ods.exchange.back;

import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

/**
 * 退文理由分类
 *
 * @author ldp
 * @date 2018/1/9
 */
@Entity(table = "ODBACKREASONTYPE", keys = "typeId")
public class BackReasonType
{

    @Generatable(length = 5)
    private Integer typeId;

    /**
     * 类型名称
     */
    @Require
    @Unique
    @ColumnDescription(type = "varchar(250)")
    private String typeName;

    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    public BackReasonType()
    {
    }

    public Integer getTypeId()
    {
        return typeId;
    }

    public void setTypeId(Integer typeId)
    {
        this.typeId = typeId;
    }

    public String getTypeName()
    {
        return typeName;
    }

    public void setTypeName(String typeName)
    {
        this.typeName = typeName;
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

        if (!(o instanceof BackReasonType))
            return false;

        BackReasonType that = (BackReasonType) o;

        return typeId.equals(that.typeId);
    }

    @Override
    public int hashCode()
    {
        return typeId.hashCode();
    }

    @Override
    public String toString()
    {
        return typeName;
    }
}
