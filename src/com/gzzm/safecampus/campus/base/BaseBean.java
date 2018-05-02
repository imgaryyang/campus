package com.gzzm.safecampus.campus.base;

import com.gzzm.platform.organ.Dept;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.ColumnDescription;
import net.cyan.thunwind.annotation.Index;
import net.cyan.thunwind.annotation.ToOne;

/**
 * @author Neo
 * @date 2018/3/26 10:58
 */
public class BaseBean
{
    /**
     * 部门ID
     */
    @Index
    protected Integer deptId;

    @NotSerialized
    @ToOne
    protected Dept dept;

    @ColumnDescription(type = "number(6)")
    protected Integer orderId;

    public BaseBean()
    {
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
}
