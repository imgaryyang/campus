package com.gzzm.ods.dict;

import com.gzzm.platform.organ.Dept;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

/**
 * 定义一些公文的标签，标签里可以通过json定义一些列条件，根据这些条件过滤公文
 *
 * @author camel
 * @date 13-11-13
 * @see com.gzzm.ods.flow.AbstractOdWorkSheetItemList#label
 */
@Entity(table = "ODLABEL", keys = "labelId")
public class Label
{
    @Generatable(length = 5)
    private Integer labelId;

    private Integer deptId;

    @NotSerialized
    private Dept dept;

    @Require
    @ColumnDescription(type = "varchar(50)")
    @Unique(with = "deptId")
    private String labelName;

    @ColumnDescription(type = "varchar(800)")
    private String condition;

    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    public Label()
    {
    }

    public Integer getLabelId()
    {
        return labelId;
    }

    public void setLabelId(Integer labelId)
    {
        this.labelId = labelId;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public String getLabelName()
    {
        return labelName;
    }

    public void setLabelName(String labelName)
    {
        this.labelName = labelName;
    }

    public String getCondition()
    {
        return condition;
    }

    public void setCondition(String condition)
    {
        this.condition = condition;
    }

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
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

        if (!(o instanceof Label))
            return false;

        Label that = (Label) o;

        return labelId.equals(that.labelId);
    }

    @Override
    public int hashCode()
    {
        return labelId.hashCode();
    }

    @Override
    public String toString()
    {
        return labelName;
    }
}
