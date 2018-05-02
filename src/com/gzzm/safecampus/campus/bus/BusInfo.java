package com.gzzm.safecampus.campus.bus;

import com.gzzm.platform.commons.Patterns;
import com.gzzm.safecampus.campus.base.BaseBean;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.MaxLen;
import net.cyan.commons.validate.annotation.Pattern;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.*;

/**
 * @author czy
 * 校巴信息
 */
@Entity(table = "SCBUSINFO", keys = "busId")
public class BusInfo extends BaseBean
{
    /**
     * 校巴ID
     */
    @Generatable(length = 6)
    private Integer busId;

    /**
     * 校巴名称
     */
    @Require
    @ColumnDescription(type = "varchar2(30)")
    @MaxLen(30)
    private String busName;

    /**
     * 校巴车牌
     */
    @Require
    @ColumnDescription(type = "varchar2(30)")
    @MaxLen(30)
    private String busLicense;

    /**
     * 路线ID
     */
    private Integer routeId;

    @NotSerialized
    @ToOne
    private BusRoute busRoute;

    /**
     * 司机名称
     */
    @Require
    @ColumnDescription(type = "varchar2(30)")
    @MaxLen(30)
    private String driverName;

    /**
     * 司机电话
     */
    @Pattern(Patterns.PHONE0)
    @Require
    @ColumnDescription(type = "varchar2(30)")
    @MaxLen(30)
    private String driverPhone;

    /**
     * 校巴站点Id，用于作为校巴站点树的主键，不持久化到数据库
     */
    @Transient
    private String busSiteId;

    /**
     * 删除标识
     */
    @ColumnDescription(type = "NUMBER(1)")
    private Integer deleteTag;

    public BusInfo()
    {
    }

    public BusInfo(Integer busId)
    {
        this.busId = busId;
    }

    public BusInfo(Integer busId, String busName)
    {
        this.busId = busId;
        this.busName = busName;
    }

    public BusInfo(String busSiteId, String busName)
    {
        this.busSiteId = busSiteId;
        this.busName = busName;
    }

    public Integer getBusId()
    {
        return busId;
    }

    public void setBusId(Integer busId)
    {
        this.busId = busId;
    }

    public String getBusLicense()
    {
        return busLicense;
    }

    public void setBusLicense(String busLicense)
    {
        this.busLicense = busLicense;
    }

    public String getBusName()
    {
        return busName;
    }

    public void setBusName(String busName)
    {
        this.busName = busName;
    }

    public BusRoute getBusRoute()
    {
        return busRoute;
    }

    public void setBusRoute(BusRoute busRoute)
    {
        this.busRoute = busRoute;
    }

    public String getDriverName()
    {
        return driverName;
    }

    public void setDriverName(String driverName)
    {
        this.driverName = driverName;
    }

    public String getDriverPhone()
    {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone)
    {
        this.driverPhone = driverPhone;
    }

    public Integer getRouteId()
    {
        return routeId;
    }

    public void setRouteId(Integer routeId)
    {
        this.routeId = routeId;
    }

    public String getBusSiteId()
    {
        return busSiteId;
    }

    public void setBusSiteId(String busSiteId)
    {
        this.busSiteId = busSiteId;
    }

    public Integer getDeleteTag()
    {
        return deleteTag;
    }

    public void setDeleteTag(Integer deleteTag)
    {
        this.deleteTag = deleteTag;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BusInfo busInfo = (BusInfo) o;

        return busId.equals(busInfo.busId);

    }

    @Override
    public int hashCode()
    {
        return busId.hashCode();
    }

    @Override
    public String toString()
    {
        return busName;
    }
}