package com.gzzm.mo;

import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

/**
 * 客户端类型，由于不同的运营商同一个设备使用的客户端可能略有不通，需要区分
 *
 * @author camel
 * @date 2014/5/13
 */
@Entity(table = "MOCLIENTTYPE", keys = "typeId")
public class MoClientType
{
    @Generatable(length = 4)
    private Integer typeId;

    @Require
    @Unique
    @ColumnDescription(type = "varchar(250)")
    private String typeName;

    /**
     * 客户端类型编号，客户端中用此编号来识别应该更新哪个客户端
     */
    @Require
    @Unique
    @ColumnDescription(type = "varchar(20)")
    private String typeCode;

    @Require
    private DeviceType deviceType;

    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    public MoClientType()
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

    public String getTypeCode()
    {
        return typeCode;
    }

    public void setTypeCode(String typeCode)
    {
        this.typeCode = typeCode;
    }

    public DeviceType getDeviceType()
    {
        return deviceType;
    }

    public void setDeviceType(DeviceType deviceType)
    {
        this.deviceType = deviceType;
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

        if (!(o instanceof MoClientType))
            return false;

        MoClientType that = (MoClientType) o;

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
