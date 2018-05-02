package com.gzzm.safecampus.campus.device;

import com.gzzm.safecampus.campus.base.BaseBean;
import net.cyan.thunwind.annotation.ColumnDescription;
import net.cyan.thunwind.annotation.Entity;
import net.cyan.thunwind.annotation.Generatable;

/**
 * 硬件信息
 *
 * @author yiuman
 * @date 2018/3/14
 */

@Entity(table = "SCDEVICEINFO", keys = "deviceId")
public class DeviceInfo extends BaseBean
{

    /**
     * ID
     */
    @Generatable(length = 6)
    private Integer deviceId;

    /**
     * 硬件名称
     */
    @ColumnDescription(type = "varchar(50)")
    private String deviceName;

    /**
     * 硬件编码
     */
    @ColumnDescription(type = "varchar(50)")
    private String deviceNo;

    /**
     * 是否使用
     */
    @ColumnDescription(type = "number(1)", defaultValue = "1")
    private Boolean using;

    /**
     * 维护人
     */
    @ColumnDescription(type = "varchar(50)")
    private String maintainer;

    /**
     * 维护人电话
     */
    @ColumnDescription(type = "varchar(20)")
    private String maPhone;

    /**
     * 状态
     */
    @ColumnDescription(type = "number(1)")
    private DeviceStatus status;

    /**
     * 硬件所在位置
     */
    @ColumnDescription(type = "varchar(250)")
    private String location;

    /**
     * 描述
     */
    @ColumnDescription(type = "varchar(500)")
    private String describe;

    public DeviceInfo()
    {
    }

    public Integer getDeviceId()
    {
        return deviceId;
    }

    public void setDeviceId(Integer deviceId)
    {
        this.deviceId = deviceId;
    }

    public String getDeviceName()
    {
        return deviceName;
    }

    public void setDeviceName(String deviceName)
    {
        this.deviceName = deviceName;
    }

    public String getDeviceNo()
    {
        return deviceNo;
    }

    public void setDeviceNo(String deviceNo)
    {
        this.deviceNo = deviceNo;
    }

    public boolean isUsing()
    {
        return using;
    }

    public void setUsing(boolean using)
    {
        this.using = using;
    }

    public String getMaintainer()
    {
        return maintainer;
    }

    public void setMaintainer(String maintainer)
    {
        this.maintainer = maintainer;
    }

    public String getMaPhone()
    {
        return maPhone;
    }

    public void setMaPhone(String maPhone)
    {
        this.maPhone = maPhone;
    }

    public DeviceStatus getStatus()
    {
        return status;
    }

    public void setStatus(DeviceStatus status)
    {
        this.status = status;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    public String getDescribe()
    {
        return describe;
    }

    public void setDescribe(String describe)
    {
        this.describe = describe;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof DeviceInfo)) return false;

        DeviceInfo that = (DeviceInfo) o;

        return deviceId != null ? deviceId.equals(that.deviceId) : that.deviceId == null;
    }

    @Override
    public int hashCode()
    {
        return deviceId != null ? deviceId.hashCode() : 0;
    }

    @Override
    public String toString()
    {
        return deviceName;
    }
}
