package com.gzzm.oa.activite;

import com.gzzm.platform.organ.Dept;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.util.StringUtils;
import net.cyan.commons.validate.annotation.*;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

/**
 * 年度活动预算
 *
 * @author xjz
 * @date 13-3-27
 */
@Entity(table = "OAACTBUDGET", keys = "activiteBudgetId")
public class ActiviteBudget
{
    /**
     * 主键
     */
    @Generatable(length = 8)
    private Integer activiteBudgetId;

    /**
     * 年份
     */
    @Unique
    @MinVal("1970")
    @MaxVal("2100")
    @Require
    private Integer budgetYears;

    /**
     * 活动预算经费
     */
    @ColumnDescription(type = "number(9,2)", nullable = false)
    private Double budgetAmount;

    /**
     * 活动发起部门
     */
    private Integer deptId;

    /**
     * 关联部门表
     */
    @NotSerialized
    private Dept dept;

    public ActiviteBudget()
    {
    }

    public Integer getActiviteBudgetId()
    {
        return activiteBudgetId;
    }

    public void setActiviteBudgetId(Integer activiteBudgetId)
    {
        this.activiteBudgetId = activiteBudgetId;
    }

    public Integer getBudgetYears()
    {
        return budgetYears;
    }

    public void setBudgetYears(Integer budgetYears)
    {
        this.budgetYears = budgetYears;
    }

    public Double getBudgetAmount()
    {
        return budgetAmount;
    }

    public void setBudgetAmount(Double budgetAmount)
    {
        this.budgetAmount = budgetAmount;
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
        if (this == o) return true;
        if (!(o instanceof ActiviteBudget)) return false;

        ActiviteBudget activiteBudget = (ActiviteBudget) o;

        return activiteBudgetId.equals(activiteBudget.activiteBudgetId);
    }

    @Override
    public int hashCode()
    {
        return activiteBudgetId.hashCode();
    }

    @Override
    public String toString()
    {
        return StringUtils.toString(budgetYears);
    }
}
