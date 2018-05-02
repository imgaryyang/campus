package com.gzzm.safecampus.campus.service;

import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.*;

import java.util.*;

/**
 * 服务类型
 * @author zy
 * @date 2018/3/12 15:22
 */
@Entity(table = "SCSERVICETYPE" , keys = "typeId")
public class ServiceType
{
    /**
     * 主键
     */
    @Generatable(length = 6)
    private Integer typeId;

    /**
     * 类型名称
     */
    @ColumnDescription(type = "varchar2(128)")
    @Require
    private String typeName;

    /**
     * 排序
     */
    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    /**
     * 分类下包含的服务
     */
    @OneToMany
    @OrderBy(column = "orderId")
    @NotSerialized
    private List<ServiceInfo> serviceInfoList;

    public ServiceType()
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

    public List<ServiceInfo> getServiceInfoList()
    {
        return serviceInfoList;
    }

    public void setServiceInfoList(List<ServiceInfo> serviceInfoList)
    {
        this.serviceInfoList = serviceInfoList;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof ServiceType)) return false;
        ServiceType that = (ServiceType) o;
        return Objects.equals(typeId, that.typeId);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(typeId);
    }

    @Override
    public String toString()
    {
        return typeName;
    }
}
