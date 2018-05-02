package com.gzzm.portal.org;

import com.gzzm.platform.organ.Dept;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.*;

/**
 * @author camel
 * @date 2014/8/13
 */
@Entity(table = "PLLEADERTITLE", keys = "titleId")
public class LeaderTitle
{
    @Generatable(length = 6)
    private Integer titleId;

    @Require
    @ColumnDescription(type = "varchar(50)")
    private String titleName;

    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    private Integer deptId;

    @NotSerialized
    private Dept dept;

    public LeaderTitle()
    {
    }

    public Integer getTitleId()
    {
        return titleId;
    }

    public void setTitleId(Integer titleId)
    {
        this.titleId = titleId;
    }

    public String getTitleName()
    {
        return titleName;
    }

    public void setTitleName(String titleName)
    {
        this.titleName = titleName;
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
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof LeaderTitle))
            return false;

        LeaderTitle that = (LeaderTitle) o;

        return titleId.equals(that.titleId);
    }

    @Override
    public int hashCode()
    {
        return titleId.hashCode();
    }

    @Override
    public String toString()
    {
        return titleName;
    }
}
