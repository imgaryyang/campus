package com.gzzm.ods.flow;

import com.gzzm.platform.organ.*;
import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * 公文终止，记录每个公文终止的操作
 *
 * @author camel
 * @date 13-12-5
 */
@Entity(table = "ODFLOWINSTANCESTOP", keys = "instanceId")
public class OdFlowInstanceStop
{
    @ColumnDescription(type = "number(12)")
    private Long instanceId;

    private OdFlowInstance instance;

    /**
     * 终止的用户
     */
    private Integer userId;

    private User user;

    private Date stopTime;

    /**
     * 终止的用户所属的部门，有别于instance的部门
     */
    private Integer deptId;

    private Dept dept;

    public OdFlowInstanceStop()
    {
    }

    public Long getInstanceId()
    {
        return instanceId;
    }

    public void setInstanceId(Long instanceId)
    {
        this.instanceId = instanceId;
    }

    public OdFlowInstance getInstance()
    {
        return instance;
    }

    public void setInstance(OdFlowInstance instance)
    {
        this.instance = instance;
    }

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public Date getStopTime()
    {
        return stopTime;
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

    public void setStopTime(Date stopTime)
    {
        this.stopTime = stopTime;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof OdFlowInstanceStop))
            return false;

        OdFlowInstanceStop that = (OdFlowInstanceStop) o;

        return instanceId.equals(that.instanceId);
    }

    @Override
    public int hashCode()
    {
        return instanceId.hashCode();
    }
}
