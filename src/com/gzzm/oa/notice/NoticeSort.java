package com.gzzm.oa.notice;

import com.gzzm.platform.organ.Dept;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.*;

/**
 * @author camel
 * @date 12-9-13
 */
@Entity(table = "OANOTICESORT", keys = "sortId")
public class NoticeSort
{
    @Generatable(length = 6)
    private Integer sortId;

    @Require
    @ColumnDescription(type = "varchar(50)")
    private String sortName;


    /**
     * 部门ID
     */
    @ColumnDescription(type = "number(7)", nullable = false)
    private Integer deptId;

    /**
     * 部门
     */
    private Dept dept;

    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    public NoticeSort()
    {
    }

    public Integer getSortId()
    {
        return sortId;
    }

    public void setSortId(Integer sortId)
    {
        this.sortId = sortId;
    }

    public String getSortName()
    {
        return sortName;
    }

    public void setSortName(String sortName)
    {
        this.sortName = sortName;
    }

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
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

    @Override
    public String toString()
    {
        return sortName;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof NoticeSort))
            return false;

        NoticeSort that = (NoticeSort) o;

        return sortId.equals(that.sortId);
    }

    @Override
    public int hashCode()
    {
        return sortId.hashCode();
    }
}
