package com.gzzm.oa.activite;

import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

/**
 * 活动类型
 *
 * @author fwj
 * @date 11-9-29
 */
@Entity(table = "OAACTIVITETYPE", keys = "typeId")
public class ActiviteType
{
    /**
     * 主键
     */
    @Generatable(length = 8)
    private Integer typeId;

    /**
     * 活动类型名称
     */
    @Unique
    @Require
    @ColumnDescription(type = "varchar(200)", nullable = false)
    private String typeName;

    /**
     * 排序ID
     */
    @ColumnDescription(type = "number(4)")
    private Integer orderId;

    public ActiviteType()
    {
    }

    public Integer getTypeId()
    {
        return typeId;
    }

    public void setTypeId(Integer typeId)
    {
        this.typeId = typeId;
    }

    public String getTypeName()
    {
        return typeName;
    }

    public void setTypeName(String typeName)
    {
        this.typeName = typeName;
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
    public int hashCode()
    {
        return typeId.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;

        if (!(obj instanceof ActiviteType))
            return false;

        ActiviteType activiteType = (ActiviteType) obj;

        return typeId.equals(activiteType.typeId);
    }

    @Override
    public String toString()
    {
        return typeName;
    }
}
