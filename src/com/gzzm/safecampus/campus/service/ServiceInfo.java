package com.gzzm.safecampus.campus.service;

import com.gzzm.platform.organ.Role;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.*;

import java.util.*;

/**
 * 服务信息
 * @author zy
 * @date 2018/3/12 15:23
 */
@Entity(table = "SCSERVICEINFO" , keys = "serviceId")
public class ServiceInfo
{
    /**
     * 主键
     */
    @Generatable(length = 9)
    private Integer serviceId;

    private Integer typeId;

    /**
     * 服务类型
     */
    @ToOne("TYPEID")
    @NotSerialized
    private ServiceType type;

    /**
     * 服务名称
     */
    @ColumnDescription(type = "varchar2(128)")
    private String serviceName;

    /**
     * 包含权限
     */
    @ManyToMany(table = "SCSERVICEINFOROLE")
    @NotSerialized
    private List<Role> roles;

    /**
     * 说明
     */
    @ColumnDescription(type = "varchar2(1000)")
    private String remark;

    /**
     * 排序
     */
    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    @Transient
    private boolean checked;

    public ServiceInfo()
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

    public Integer getTypeId()
    {
        return typeId;
    }

    public void setTypeId(Integer typeId)
    {
        this.typeId = typeId;
    }

    public ServiceType getType()
    {
        return type;
    }

    public void setType(ServiceType type)
    {
        this.type = type;
    }

    public String getServiceName()
    {
        return serviceName;
    }

    public void setServiceName(String serviceName)
    {
        this.serviceName = serviceName;
    }

    public List<Role> getRoles()
    {
        return roles;
    }

    public void setRoles(List<Role> roles)
    {
        this.roles = roles;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
    }

    public boolean isChecked()
    {
        return checked;
    }

    public void setChecked(boolean checked)
    {
        this.checked = checked;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof ServiceInfo)) return false;
        ServiceInfo that = (ServiceInfo) o;
        return Objects.equals(serviceId, that.serviceId);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(serviceId);
    }
}
