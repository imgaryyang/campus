package com.gzzm.portal.olconsult;

import com.gzzm.platform.organ.User;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

/**
 * User: wym
 * Date: 13-5-31
 * Time: 上午10:57
 * 坐席管理
 */
@Entity(table = "PLOLCONSULTSEAT", keys = "seatId")
public class OlConsultSeat
{
    @Generatable(length = 6)
    private Integer seatId;

    private Integer typeId;

    private OlConsultType type;

    @Unique(with = "typeId")
    @Require
    @ColumnDescription(type = "varchar2(50)")
    private String seatName;

    @Unique(with = "typeId")
    private Integer userId;

    @NotSerialized
    private User user;

    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    public OlConsultSeat()
    {
    }

    public Integer getSeatId()
    {
        return seatId;
    }

    public void setSeatId(Integer seatId)
    {
        this.seatId = seatId;
    }

    public Integer getTypeId()
    {
        return typeId;
    }

    public void setTypeId(Integer typeId)
    {
        this.typeId = typeId;
    }

    public OlConsultType getType()
    {
        return type;
    }

    public void setType(OlConsultType type)
    {
        this.type = type;
    }

    public String getSeatName()
    {
        return seatName;
    }

    public void setSeatName(String seatName)
    {
        this.seatName = seatName;
    }

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
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
        if (!(o instanceof OlConsultSeat))
            return false;

        OlConsultSeat that = (OlConsultSeat) o;

        return seatId.equals(that.seatId);
    }

    @Override
    public int hashCode()
    {
        return seatId.hashCode();
    }

    @Override
    public String toString()
    {
        return seatName;
    }
}