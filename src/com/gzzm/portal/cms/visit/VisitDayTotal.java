package com.gzzm.portal.cms.visit;

import com.gzzm.platform.visit.*;
import net.cyan.thunwind.annotation.*;

import java.sql.*;

/**
 * @author sjy
 * @date 2018/3/2
 */
@Entity(table = "PFVISITDAYTOTAL", keys = "recordId")
public class VisitDayTotal
{
    @Generatable(length = 11)
    private Long recordId;

    /**
     * 统计日期
     */
    private Date visitDay;

    /**
     * @see VisitRecord#type
     */
    @ColumnDescription(type = "number(3)", nullable = false)
    private Integer type;

    /**
     * 访问的对象的ID，即相关表的主键，要求必须是整数
     */
    private Integer objectId;

    /**
     * 当天访问量
     * 同一用户每日多次访问不重复计算，只计一次
     */
    @ColumnDescription(type = "number(8)")
    private Integer visitCount;

    /**
     * 点击量
     * 同一用户对同一页面多次访问重复计算
     */
    @ColumnDescription(type = "number(8)")
    private Integer clickCount;

    public Long getRecordId()
    {
        return recordId;
    }

    public void setRecordId(Long recordId)
    {
        this.recordId = recordId;
    }

    public Date getVisitDay()
    {
        return visitDay;
    }

    public void setVisitDay(Date visitDay)
    {
        this.visitDay = visitDay;
    }

    public Integer getType()
    {
        return type;
    }

    public void setType(Integer type)
    {
        this.type = type;
    }

    public Integer getObjectId()
    {
        return objectId;
    }

    public void setObjectId(Integer objectId)
    {
        this.objectId = objectId;
    }

    public Integer getVisitCount()
    {
        return visitCount;
    }

    public void setVisitCount(Integer visitCount)
    {
        this.visitCount = visitCount;
    }

    public Integer getClickCount()
    {
        return clickCount;
    }

    public void setClickCount(Integer clickCount)
    {
        this.clickCount = clickCount;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof VisitDayTotal))
        {
            return false;
        }

        VisitDayTotal that = (VisitDayTotal) o;

        return recordId.equals(that.recordId);

    }

    @Override
    public int hashCode()
    {
        return recordId.hashCode();
    }
}
