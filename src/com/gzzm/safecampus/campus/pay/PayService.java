package com.gzzm.safecampus.campus.pay;

import net.cyan.thunwind.annotation.*;

import java.util.*;

/**
 * @author yuanfang
 * @date 18-03-06 16:28
 */

@Entity(table = "SCPAYSERVICE", keys = "serviceId")
public class PayService
{

    @Generatable(length = 6)
    private Integer serviceId;

    /**
     * 服务名称
     */
    @ColumnDescription(type = "varchar(30)")
    private String serviceName;

    /**
     * 服务图标
     */
    //private Icon icon;

    /**
     * 服务说明
     */
    @ColumnDescription(type = "varchar(100)")
    private String note;

    public PayService()
    {
    }

    public Integer getServiceId()
    {
        return serviceId;
    }

    public void setServiceId(Integer serviceId)
    {
        this.serviceId = serviceId;
    }

    public String getServiceName()
    {
        return serviceName;
    }

    public void setServiceName(String serviceName)
    {
        this.serviceName = serviceName;
    }

    public String getNote()
    {
        return note;
    }

    public void setNote(String note)
    {
        this.note = note;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PayService that = (PayService) o;
        return Objects.equals(serviceId, that.serviceId);
    }

    @Override
    public int hashCode()
    {

        return Objects.hash(serviceId);
    }

    @Override
    public String toString()
    {
        return  serviceName;
    }
}
