package com.gzzm.ods.document;

import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

/**
 * 配置公文的扩展属性
 *
 * @author camel
 * @date 11-10-21
 */
@Entity(table = "ODDOCUMENTATTRIBUTE", keys = "attributeId")
public class DocumentAttribute
{
    @Generatable(length = 5, name = "ODDOCUMENTATTRIBUTEID")
    private Integer attributeId;

    @Require
    @Unique
    @ColumnDescription(type = "varchar(30)", nullable = false)
    private String attributeName;

    /**
     * 枚举值，多个值用|分开
     */
    @ColumnDescription(type = "varchar(500)")
    private String enumValues;

    /**
     * 是否查询条件
     */
    @Require
    @ColumnDescription(defaultValue = "0", nullable = false)
    private Boolean query;

    /**
     * 是否为更多查询
     */
    @Require
    private Boolean moreQuery;

    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    public DocumentAttribute()
    {
    }

    public Integer getAttributeId()
    {
        return attributeId;
    }

    public void setAttributeId(Integer attributeId)
    {
        this.attributeId = attributeId;
    }

    public String getAttributeName()
    {
        return attributeName;
    }

    public void setAttributeName(String attributeName)
    {
        this.attributeName = attributeName;
    }

    public Boolean getQuery()
    {
        return query;
    }

    public void setQuery(Boolean query)
    {
        this.query = query;
    }

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
    }

    public String getEnumValues()
    {
        return enumValues;
    }

    public void setEnumValues(String enumValues)
    {
        this.enumValues = enumValues;
    }

    public Boolean getMoreQuery()
    {
        return moreQuery;
    }

    public void setMoreQuery(Boolean moreQuery)
    {
        this.moreQuery = moreQuery;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof DocumentAttribute))
            return false;

        DocumentAttribute that = (DocumentAttribute) o;

        return attributeId.equals(that.attributeId);
    }

    @Override
    public int hashCode()
    {
        return attributeId.hashCode();
    }

    @Override
    public String toString()
    {
        return attributeName;
    }
}
