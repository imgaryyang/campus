package com.gzzm.ods.exchange;

import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

/**
 * @author camel
 * @date 2015/11/19
 */
@Entity(table = "ODRECEIVEATTRIBUTE", keys = "attributeId")
public class ReceiveAttribute
{
    @Generatable(length = 5, name = "ODRECEIVEATTRIBUTEID")
    private Integer attributeId;

    @Require
    @Unique
    @ColumnDescription(type = "varchar(30)", nullable = false)
    private String attributeName;

    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    public ReceiveAttribute()
    {
    }

    public Integer getAttributeId()
    {
        return attributeId;
    }

    public void setAttributeId(Integer attributeId)
    {
        this.attributeId = attributeId;
    }

    public String getAttributeName()
    {
        return attributeName;
    }

    public void setAttributeName(String attributeName)
    {
        this.attributeName = attributeName;
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

        if (!(o instanceof ReceiveAttribute))
            return false;

        ReceiveAttribute that = (ReceiveAttribute) o;

        return attributeId.equals(that.attributeId);
    }

    @Override
    public int hashCode()
    {
        return attributeId.hashCode();
    }

    @Override
    public String toString()
    {
        return attributeName;
    }
}
