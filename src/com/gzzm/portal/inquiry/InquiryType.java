package com.gzzm.portal.inquiry;

import net.cyan.commons.util.*;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

/**
 * 咨询投诉类别
 *
 * @author camel
 * @date 12-11-6
 */
@Entity(table = "PLINQUIRYTYPE", keys = "typeId")
public class InquiryType implements AdvancedEnum, Comparable<InquiryType>
{
    private static final long serialVersionUID = 8788337920839552758L;

    @Generatable(length = 3)
    private Integer typeId;

    @Require
    @Unique
    @ColumnDescription(type = "varchar(50)")
    private String typeName;

    @ColumnDescription(type = "number(3)")
    private Integer orderId;

    public InquiryType()
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

        if (!(o instanceof InquiryType))
            return false;

        InquiryType that = (InquiryType) o;

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

    public Object valueOf()
    {
        return typeId;
    }

    public int compareTo(InquiryType o)
    {
        return DataConvert.compare(orderId, o.orderId);
    }
}
