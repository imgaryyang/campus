package com.gzzm.portal.cms.information;

import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

import java.util.List;

/**
 * @author camel
 * @date 12-8-29
 */
@Entity(table = "PLINFORMATIONCATALOG", keys = "catalogId")
public class InformationCatalog
{
    @Generatable(length = 6)
    private Integer catalogId;

    @Require
    @Unique(with = "parentCatalogId")
    @ColumnDescription(type = "varchar(250)")
    private String catalogName;

    private Integer parentCatalogId;

    @NotSerialized
    @ToOne("PARENTCATALOGID")
    private InformationCatalog parentCatalog;

    @NotSerialized
    @OrderBy(column = "ORDERID")
    @OneToMany("PARENTCATALOGID")
    private List<InformationCatalog> children;

    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    public InformationCatalog()
    {
    }

    public Integer getCatalogId()
    {
        return catalogId;
    }

    public void setCatalogId(Integer catalogId)
    {
        this.catalogId = catalogId;
    }

    public String getCatalogName()
    {
        return catalogName;
    }

    public void setCatalogName(String catalogName)
    {
        this.catalogName = catalogName;
    }

    public Integer getParentCatalogId()
    {
        return parentCatalogId;
    }

    public void setParentCatalogId(Integer parentCatalogId)
    {
        this.parentCatalogId = parentCatalogId;
    }

    public InformationCatalog getParentCatalog()
    {
        return parentCatalog;
    }

    public void setParentCatalog(InformationCatalog parentCatalog)
    {
        this.parentCatalog = parentCatalog;
    }

    public List<InformationCatalog> getChildren()
    {
        return children;
    }

    public void setChildren(List<InformationCatalog> children)
    {
        this.children = children;
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

        if (!(o instanceof InformationCatalog))
            return false;

        InformationCatalog that = (InformationCatalog) o;

        return catalogId.equals(that.catalogId);
    }

    @Override
    public int hashCode()
    {
        return catalogId.hashCode();
    }

    @Override
    public String toString()
    {
        return catalogName;
    }
}
