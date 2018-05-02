package com.gzzm.ods.archive;

import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

/**
 * 归档文件保管期限管理
 *
 * @author camel
 * @date 2016/8/28
 */
@Entity(table = "ODARCHIVETIMELIMIT", keys = "timeLimitId")
public class ArchiveTimeLimit
{
    @Generatable(length = 2)
    private Integer timeLimitId;

    @Require
    @Unique
    @ColumnDescription(type = "varchar(250)")
    private String timeLimit;

    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    public ArchiveTimeLimit()
    {
    }

    public Integer getTimeLimitId()
    {
        return timeLimitId;
    }

    public void setTimeLimitId(Integer timeLimitId)
    {
        this.timeLimitId = timeLimitId;
    }

    public String getTimeLimit()
    {
        return timeLimit;
    }

    public void setTimeLimit(String timeLimit)
    {
        this.timeLimit = timeLimit;
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

        if (!(o instanceof ArchiveTimeLimit))
            return false;

        ArchiveTimeLimit that = (ArchiveTimeLimit) o;

        return timeLimitId.equals(that.timeLimitId);
    }

    @Override
    public int hashCode()
    {
        return timeLimitId.hashCode();
    }

    @Override
    public String toString()
    {
        return timeLimit;
    }
}
