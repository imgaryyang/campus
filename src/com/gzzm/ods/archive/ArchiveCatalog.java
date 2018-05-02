package com.gzzm.ods.archive;

import com.gzzm.platform.organ.Dept;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

/**
 * 案件目录
 *
 * @author camel
 * @date 2016/5/11
 */
@Entity(table = "ODARCHIVECATALOG", keys = "catalogId")
@Indexes({@Index(columns = {"year", "deptId"})})
public class ArchiveCatalog
{
    @Generatable(length = 9)
    private Integer catalogId;

    @Require
    @Unique(with = {"year", "deptId"})
    @ColumnDescription(type = "varchar(250)")
    private String catalogName;

    private Integer deptId;

    @NotSerialized
    private Dept dept;

    @ColumnDescription(type = "number(4)")
    private Integer year;

    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    public ArchiveCatalog()
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

    public Integer getYear()
    {
        return year;
    }

    public void setYear(Integer year)
    {
        this.year = year;
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

        if (!(o instanceof ArchiveCatalog))
            return false;

        ArchiveCatalog that = (ArchiveCatalog) o;

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
