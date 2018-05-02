package com.gzzm.portal.inquiry;

import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

/**
 * 提交方式，政府信箱，或者其他
 *
 * @author camel
 * @date 12-11-6
 */
@Entity(table = "PLINQUIRYWAY", keys = "wayId")
public class InquiryWay
{
    @Generatable(length = 3)
    private Integer wayId;

    @Require
    @Unique
    @ColumnDescription(type = "varchar(50)")
    private String wayName;

    @ColumnDescription(type = "number(3)")
    private Integer orderId;

    public InquiryWay()
    {
    }

    public Integer getWayId()
    {
        return wayId;
    }

    public void setWayId(Integer wayId)
    {
        this.wayId = wayId;
    }

    public String getWayName()
    {
        return wayName;
    }

    public void setWayName(String wayName)
    {
        this.wayName = wayName;
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

        if (!(o instanceof InquiryWay))
            return false;

        InquiryWay that = (InquiryWay) o;

        return wayId.equals(that.wayId);
    }

    @Override
    public int hashCode()
    {
        return wayId.hashCode();
    }

    @Override
    public String toString()
    {
        return wayName;
    }
}
