package com.gzzm.ods.flow;

import com.gzzm.platform.organ.Dept;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

import java.util.List;

/**
 * 将公文流程实例归档
 *
 * @author camel
 * @date 12-4-25
 */
@Entity(table = "ODINSTANCECATALOG", keys = "catalogId")
public class OdInstanceCatalog
{
    /**
     * 目录ID，主键
     */
    @Generatable(length = 9)
    private Integer catalogId;

    /**
     * 目录名
     */
    @ColumnDescription(type = "varchar(250)")
    @Unique(with = {"parentCatalogId", "deptId"})
    @Require
    private String catalogName;

    private Integer parentCatalogId;

    @NotSerialized
    @ToOne("PARENTCATALOGID")
    private OdInstanceCatalog parentCatalog;

    @NotSerialized
    @OneToMany("PARENTCATALOGID")
    @OrderBy(column = "ORDERID")
    private List<OdInstanceCatalog> children;

    private Integer deptId;

    private Dept dept;

    /**
     * 排序id，目录在同一级目录中的顺序，越小排越前面
     */
    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    public OdInstanceCatalog()
    {
    }

    public OdInstanceCatalog(Integer catalogId, String catalogName)
    {
        this.catalogId = catalogId;
        this.catalogName = catalogName;
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

    public OdInstanceCatalog getParentCatalog()
    {
        return parentCatalog;
    }

    public void setParentCatalog(OdInstanceCatalog parentCatalog)
    {
        this.parentCatalog = parentCatalog;
    }

    public List<OdInstanceCatalog> getChildren()
    {
        return children;
    }

    public void setChildren(List<OdInstanceCatalog> children)
    {
        this.children = children;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public Dept getDept()
    {
        return dept;
    }

    public void setDept(Dept dept)
    {
        this.dept = dept;
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

        if (!(o instanceof OdInstanceCatalog))
            return false;

        OdInstanceCatalog that = (OdInstanceCatalog) o;

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
