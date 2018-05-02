package com.gzzm.platform.workday;

import com.gzzm.platform.commons.UpdateTimeService;
import net.cyan.commons.util.DateUtils;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

import java.sql.Date;

/**
 * 工作日配置
 *
 * @author camel
 * @date 2010-5-26
 */
@Entity(table = "PFWORKDAY", keys = "workDayId")
public class WorkDay
{
    /**
     * 工作日ID，主键
     */
    @Generatable(length = 6)
    private Integer workDayId;

    /**
     * 工作日对应的时间
     */
    @Require
    @ColumnDescription(nullable = false)
    @Unique
    private Date workDayDate;

    /**
     * 工作日类型
     */
    @Require
    @ColumnDescription(nullable = false)
    private WorkDayType type;

    /**
     * 说明
     */
    @ColumnDescription(type = "varchar(1000)")
    private String remark;

    public WorkDay()
    {
    }

    public Integer getWorkDayId()
    {
        return workDayId;
    }

    public void setWorkDayId(Integer workDayId)
    {
        this.workDayId = workDayId;
    }

    public Date getWorkDayDate()
    {
        return workDayDate;
    }

    public void setWorkDayDate(Date workDayDate)
    {
        this.workDayDate = workDayDate;
    }

    public WorkDayType getType()
    {
        return type;
    }

    public void setType(WorkDayType type)
    {
        this.type = type;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof WorkDay))
            return false;

        WorkDay workDay = (WorkDay) o;

        return workDayId.equals(workDay.workDayId);
    }

    @Override
    public int hashCode()
    {
        return workDayId.hashCode();
    }

    @Override
    public String toString()
    {
        if (workDayDate == null)
            return "";

        return DateUtils.toString(workDayDate);
    }

    public static void setUpdated() throws Exception
    {
        //数据被修改之后更新菜单更新时间
        UpdateTimeService.updateLastTime("WorkDay", new java.util.Date());
    }
}
