package com.gzzm.ods.dict;

import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

/**
 * 紧急程度实体
 *
 * @author db
 * @date 11-12-31
 */
@Entity(table = "ODPRIORITY", keys = "priorityId")
public class Priority
{
    /**
     * 主键
     */
    @Generatable(length = 7)
    private Integer priorityId;

    /**
     * 紧急程度名称
     */
    @Require
    @Unique
    @ColumnDescription(type = "varchar(250)")
    private String priorityName;

    /**
     * 排序
     */
    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    public Priority()
    {
    }

    public Integer getPriorityId()
    {
        return priorityId;
    }

    public void setPriorityId(Integer priorityId)
    {
        this.priorityId = priorityId;
    }

    public String getPriorityName()
    {
        return priorityName;
    }

    public void setPriorityName(String priorityName)
    {
        this.priorityName = priorityName;
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
        
        if (!(o instanceof Priority))
            return false;

        Priority priority = (Priority) o;

        return priorityId.equals(priority.priorityId);
    }

    @Override
    public int hashCode()
    {
        return priorityId.hashCode();
    }

    @Override
    public String toString()
    {
        return priorityName;
    }
}
