package com.gzzm.ods.redhead;

import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

/**
 * 红头模板类型，整个系统使用统一的红头类型定义
 *
 * @author camel
 * @date 2010-6-28
 */
@Entity(table = "ODREDHEADTYPE", keys = "typeId")
public class RedHeadType implements Comparable<RedHeadType>
{
    /**
     * 红头类型ID，由序列号生成
     */
    @Generatable(length = 4)
    private Integer typeId;

    /**
     * 红头类型名称
     */
    @ColumnDescription(type = "varchar(250)", nullable = false)
    @Require
    @Unique
    private String typeName;

    /**
     * 红头类型排序号
     */
    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    public RedHeadType()
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
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof RedHeadType))
            return false;

        RedHeadType that = (RedHeadType) o;

        return typeId.equals(that.typeId);
    }

    @Override
    public int hashCode()
    {
        return typeId.hashCode();
    }

    @Override
    public String toString()
    {
        return typeName;
    }

    public int compareTo(RedHeadType o)
    {
        if (o == this)
            return 0;

        if (orderId == null)
            return -1;

        if (o.orderId == null)
            return 1;

        return orderId.compareTo(o.orderId);
    }
}
