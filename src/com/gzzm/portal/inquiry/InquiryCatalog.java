package com.gzzm.portal.inquiry;

import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

import java.util.List;

/**
 * 可由来信人分类，也可由后台分类
 *
 * @author camel
 * @date 12-11-6
 */
@Entity(table = "PLINQUIRYCATALOG", keys = "catalogId")
public class InquiryCatalog
{
    @Generatable(length = 5)
    private Integer catalogId;

    @Require
    @Unique(with = "parentCatalogId")
    @ColumnDescription(type = "varchar(50)")
    private String catalogName;

    /**
     * 父类型
     */
    private Integer parentCatalogId;

    @NotSerialized
    @ToOne("PARENTCATALOGID")
    private InquiryCatalog parentCatalog;

    @NotSerialized
    @OneToMany("PARENTCATALOGID")
    private List<InquiryCatalog> children;

    @ColumnDescription(type = "number(3)")
    private Integer orderId;

    public InquiryCatalog()
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

    public InquiryCatalog getParentCatalog()
    {
        return parentCatalog;
    }

    public void setParentCatalog(InquiryCatalog parentCatalog)
    {
        this.parentCatalog = parentCatalog;
    }

    public List<InquiryCatalog> getChildren()
    {
        return children;
    }

    public void setChildren(List<InquiryCatalog> children)
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

        if (!(o instanceof InquiryCatalog))
            return false;

        InquiryCatalog that = (InquiryCatalog) o;

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
