package com.gzzm.portal.olconsult;

import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.*;

/**
 * 在线咨询类型
 *
 * @author camel
 * @date 13-5-30
 */
@Entity(table = "PLOLCONSULTTYPE", keys = "typeId")
public class OlConsultType
{
    @Generatable(length = 5)
    private Integer typeId;

    @Require
    @ColumnDescription(type = "varchar(50)")
    private String typeName;

    public OlConsultType()
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

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;

        if (!(o instanceof OlConsultType))
            return false;

        OlConsultType that = (OlConsultType) o;

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
}
