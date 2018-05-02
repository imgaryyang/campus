package com.gzzm.platform.log;

import net.cyan.thunwind.annotation.*;

/**
 * 记录所有使用过的日志类型
 *
 * @author camel
 * @date 2010-7-6
 */
@Entity(table = "PFOPERATIONLOGTYPE", keys = "type")
public class OperationLogType
{
    @ColumnDescription(type = "varchar(200)")
    private String type;

    /**
     * 顺序ID
     */
   @ColumnDescription(type = "number(4)")
    private Integer orderId;

    public OperationLogType()
    {
    }

    public OperationLogType(String type)
    {
        this.type = type;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
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

        if (!(o instanceof OperationLogType))
            return false;

        OperationLogType that = (OperationLogType) o;

        return type.equals(that.type);

    }

    @Override
    public int hashCode()
    {
        return type.hashCode();
    }

    @Override
    public String toString()
    {
        return type;
    }
}
