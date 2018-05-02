package com.gzzm.safecampus.campus.bus;

import com.gzzm.safecampus.campus.base.BaseBean;
import com.gzzm.safecampus.campus.base.TimeUtils;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.MaxVal;
import net.cyan.thunwind.annotation.ColumnDescription;
import net.cyan.thunwind.annotation.Entity;
import net.cyan.thunwind.annotation.Generatable;

import java.math.BigDecimal;

/**
 * @author czy
 *         校巴途经地点
 */
@Entity(table = "SCBUSSITE", keys = "siteId")
public class BusSite extends BaseBean
{
    /**
     * 途经地点ID
     */
    @Generatable(length = 6)
    private Integer siteId;

    /**
     * 途经地点名称
     */
    @ColumnDescription(type = "varchar2(30)")
    private String siteName;

    /**
     * 早班途经时间时分
     */
    @MaxVal("23")
    @ColumnDescription(type = "number(2)")
    private Integer morningViaHour;

    /**
     * 早班途经时间分钟
     */
    @MaxVal("59")
    @ColumnDescription(type = "number(2)")
    private Integer morningViaMinute;

    /**
     * 晚班途经时间时分
     */
    @MaxVal("23")
    @ColumnDescription(type = "number(2)")
    private Integer nightViaHour;

    /**
     * 晚班途经时间分钟
     */
    @MaxVal("59")
    @ColumnDescription(type = "number(2)")
    private Integer nightViaMinute;

    /**
     * 经度
     */
    @ColumnDescription(type = "decimal(18,2)")
    private BigDecimal longitude;

    /**
     * 纬度
     */
    @ColumnDescription(type = "decimal(18,2)")
    private BigDecimal latitude;

    /**
     * 路线ID
     */
    @ColumnDescription(type = "number(6)")
    private Integer routeId;

    @NotSerialized
    private BusRoute route;

    public BusSite()
    {
    }

    public BigDecimal getLatitude()
    {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude)
    {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude()
    {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude)
    {
        this.longitude = longitude;
    }

    public Integer getMorningViaHour()
    {
        return morningViaHour;
    }

    public void setMorningViaHour(Integer morningViaHour)
    {
        this.morningViaHour = morningViaHour;
    }

    public Integer getMorningViaMinute()
    {
        return morningViaMinute;
    }

    public void setMorningViaMinute(Integer morningViaMinute)
    {
        this.morningViaMinute = morningViaMinute;
    }

    public Integer getNightViaHour()
    {
        return nightViaHour;
    }

    public void setNightViaHour(Integer nightViaHour)
    {
        this.nightViaHour = nightViaHour;
    }

    public Integer getNightViaMinute()
    {
        return nightViaMinute;
    }

    public void setNightViaMinute(Integer nightViaMinute)
    {
        this.nightViaMinute = nightViaMinute;
    }

    public BusRoute getRoute()
    {
        return route;
    }

    public void setRoute(BusRoute route)
    {
        this.route = route;
    }

    public Integer getRouteId()
    {
        return routeId;
    }

    public void setRouteId(Integer routeId)
    {
        this.routeId = routeId;
    }

    public Integer getSiteId()
    {
        return siteId;
    }

    public void setSiteId(Integer siteId)
    {
        this.siteId = siteId;
    }

    public String getSiteName()
    {
        return siteName;
    }

    public void setSiteName(String siteName)
    {
        this.siteName = siteName;
    }

    public String getMorningTime()
    {
        return TimeUtils.hourMinuteFormat(morningViaHour, morningViaMinute);
    }

    public String getNightTime()
    {
        return TimeUtils.hourMinuteFormat(nightViaHour, nightViaMinute);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BusSite busSite = (BusSite) o;

        return siteId.equals(busSite.siteId);

    }

    @Override
    public int hashCode()
    {
        return siteId.hashCode();
    }

    @Override
    public String toString()
    {
        return siteName;
    }
}