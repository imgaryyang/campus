package com.gzzm.portal.org;

import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

import java.util.List;

/**
 * @author lk
 * @date 13-9-26
 */
@Entity(table = "PLORGTYPE", keys = "typeId")
public class OrgType
{
    @Generatable(length = 7)
    private Integer typeId;

    @Require
    @Unique
    @ColumnDescription(type = "varchar(50)", nullable = false)
    private String typeName;

    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    private Integer parentTypeId;

    @NotSerialized
    @ToOne("PARENTTYPEID")
    private OrgType parentType;

    @NotSerialized
    @OneToMany("PARENTTYPEID")
    private List<OrgType> children;

    public OrgType()
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

    public Integer getParentTypeId()
    {
        return parentTypeId;
    }

    public void setParentTypeId(Integer parentTypeId)
    {
        this.parentTypeId = parentTypeId;
    }

    public OrgType getParentType()
    {
        return parentType;
    }

    public void setParentType(OrgType parentType)
    {
        this.parentType = parentType;
    }

    public List<OrgType> getChildren()
    {
        return children;
    }

    public void setChildren(List<OrgType> children)
    {
        this.children = children;
    }

    @Override
    public String toString()
    {
        return typeName;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof OrgType))
            return false;

        OrgType orgType = (OrgType) o;

        return typeId.equals(orgType.typeId);
    }

    @Override
    public int hashCode()
    {
        return typeId.hashCode();
    }
}
