package com.gzzm.platform.timeout;

import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.*;

/**
 * 超时级别，例如黄牌，红牌等
 *
 * @author camel
 * @date 12-8-16
 */
@Entity(table = "PFTIMEOUTLEVEL", keys = "levelId")
public class TimeoutLevel implements Comparable<TimeoutLevel>
{
    @Generatable(length = 2)
    private Integer levelId;

    @Require
    @ColumnDescription(type = "varchar(250)")
    private String levelName;

    /**
     * 颜色
     */
    private String color;

    /**
     * 警告图标
     */
    private byte[] icon;

    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    public TimeoutLevel()
    {
    }

    public Integer getLevelId()
    {
        return levelId;
    }

    public void setLevelId(Integer levelId)
    {
        this.levelId = levelId;
    }

    public String getLevelName()
    {
        return levelName;
    }

    public void setLevelName(String levelName)
    {
        this.levelName = levelName;
    }

    public String getColor()
    {
        return color;
    }

    public void setColor(String color)
    {
        this.color = color;
    }

    public byte[] getIcon()
    {
        return icon;
    }

    public void setIcon(byte[] icon)
    {
        this.icon = icon;
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

        if (!(o instanceof TimeoutLevel))
            return false;

        TimeoutLevel that = (TimeoutLevel) o;

        return levelId.equals(that.levelId);
    }

    @Override
    public int hashCode()
    {
        return levelId.hashCode();
    }

    @Override
    public String toString()
    {
        return levelName;
    }

    @Override
    public int compareTo(TimeoutLevel o)
    {
        if (o.orderId == null)
            return -1;

        if (orderId == null)
            return 1;

        return orderId.compareTo(o.orderId);
    }
}
