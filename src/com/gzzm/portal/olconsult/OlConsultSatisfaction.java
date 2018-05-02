package com.gzzm.portal.olconsult;

import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

/**
 * Created with IntelliJ IDEA.
 * User: wym
 * Date: 13-6-5
 * Time: 上午9:15
 * 在线咨询 满意度
 */
@Entity(table = "PLOLCONSULTSATISFACTION", keys = "satisfactionId")
public class OlConsultSatisfaction
{

    @Generatable(length = 6, name = "PLOLCONSULTSATISFACTIONID")
    private Integer satisfactionId;

    @Require
    @Unique(with = "typeId")
    @ColumnDescription(type = "varchar2(20)")
    private String satisfaction;

    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    private Integer typeId;

    @NotSerialized
    private OlConsultType type;

    public OlConsultSatisfaction()
    {

    }

    public Integer getSatisfactionId()
    {
        return satisfactionId;
    }

    public void setSatisfactionId(Integer satisfactionId)
    {
        this.satisfactionId = satisfactionId;
    }

    public String getSatisfaction()
    {
        return satisfaction;
    }

    public void setSatisfaction(String satisfaction)
    {
        this.satisfaction = satisfaction;
    }

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
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

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof OlConsultSatisfaction))
            return false;

        OlConsultSatisfaction that = (OlConsultSatisfaction) o;

        return satisfactionId.equals(that.satisfactionId);
    }

    @Override
    public int hashCode()
    {
        return satisfactionId.hashCode();
    }

    @Override
    public String toString()
    {
        return satisfaction;
    }
}
