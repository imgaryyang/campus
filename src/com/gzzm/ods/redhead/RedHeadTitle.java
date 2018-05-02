package com.gzzm.ods.redhead;

import com.gzzm.platform.organ.Dept;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

/**
 * 红头标题
 *
 * @author camel
 * @date 2010-6-28
 */
@Entity(table = "ODREDHEADTITLE", keys = "titleId")
public class RedHeadTitle
{
    /**
     * 红头标题ID，由序列号生成
     */
    @Generatable(length = 8)
    private Integer titleId;

    @Require
    @Unique(with = "deptId")
    @ColumnDescription(type = "varchar(250)")
    private String title;

    /**
     * 红头模板所属部门
     */
    private Integer deptId;

    /**
     * 关联Dept对象
     */
    private Dept dept;

    /**
     * 排序ID，排序范围为部门内
     */
    private Integer orderId;

    public RedHeadTitle()
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

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
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

        if (!(o instanceof RedHeadTitle))
            return false;

        RedHeadTitle that = (RedHeadTitle) o;

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
        return title;
    }
}
