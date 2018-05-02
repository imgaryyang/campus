package com.gzzm.safecampus.campus.bus;

import com.gzzm.safecampus.campus.base.BaseBean;
import com.gzzm.safecampus.campus.base.TimeUtils;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.ColumnDescription;
import net.cyan.thunwind.annotation.Entity;
import net.cyan.thunwind.annotation.Generatable;
import net.cyan.thunwind.annotation.OneToMany;

import java.util.List;

/**
 * 校巴路线
 *
 * @author czy
 */
@Entity(table = "SCBUSROUTE", keys = "routeId")
public class BusRoute extends BaseBean
{
    /**
     * 路线ID
     */
    @Generatable(length = 6)
    private Integer routeId;

    /**
     * 路线名称
     */
    @ColumnDescription(type = "varchar2(30)")
    private String routeName;

    /**
     * 早班发车时间时分
     */
    @ColumnDescription(type = "number(2)")
    private Integer morningBusHour;

    /**
     * 早班发车时间分钟
     */
    @ColumnDescription(type = "number(2)")
    private Integer morningBusMinute;

    /**
     * 晚班发车时间时分
     */
    @ColumnDescription(type = "number(2)")
    private Integer nightBusHour;

    /**
     * 晚班发车时间分钟
     */
    @ColumnDescription(type = "number(2)")
    private Integer nightBusMinute;

    /**
     * 预计路线行程时间
     */
    @ColumnDescription(type = "varchar2(30)")
    private String routeTravelTime;

    @OneToMany
    @NotSerialized
    private List<BusSite> busSites;

    @ColumnDescription(type = "NUMBER(1)", defaultValue = "0")
    private Integer deleteTag;

    public BusRoute()
    {
    }

    public Integer getMorningBusHour()
    {
        return morningBusHour;
    }

    public void setMorningBusHour(Integer morningBusHour)
    {
        this.morningBusHour = morningBusHour;
    }

    public Integer getMorningBusMinute()
    {
        return morningBusMinute;
    }

    public void setMorningBusMinute(Integer morningBusMinute)
    {
        this.morningBusMinute = morningBusMinute;
    }

    public Integer getNightBusHour()
    {
        return nightBusHour;
    }

    public void setNightBusHour(Integer nightBusHour)
    {
        this.nightBusHour = nightBusHour;
    }

    public Integer getNightBusMinute()
    {
        return nightBusMinute;
    }

    public void setNightBusMinute(Integer nightBusMinute)
    {
        this.nightBusMinute = nightBusMinute;
    }

    public Integer getRouteId()
    {
        return routeId;
    }

    public void setRouteId(Integer routeId)
    {
        this.routeId = routeId;
    }

    public String getRouteName()
    {
        return routeName;
    }

    public void setRouteName(String routeName)
    {
        this.routeName = routeName;
    }

    public String getRouteTravelTime()
    {
        return routeTravelTime;
    }

    public void setRouteTravelTime(String routeTravelTime)
    {
        this.routeTravelTime = routeTravelTime;
    }

    public List<BusSite> getBusSites()
    {
        return busSites;
    }

    public void setBusSites(List<BusSite> busSites)
    {
        this.busSites = busSites;
    }

    public String getMorningTime()
    {
        return TimeUtils.hourMinuteFormat(morningBusHour, morningBusMinute);
    }

    public String getNightTime()
    {
        return TimeUtils.hourMinuteFormat(nightBusHour, nightBusMinute);
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

        BusRoute busRoute = (BusRoute) o;

        return routeId.equals(busRoute.routeId);

    }

    @Override
    public int hashCode()
    {
        return routeId.hashCode();
    }

    @Override
    public String toString()
    {
        return routeName;
    }
}