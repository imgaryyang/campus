package com.gzzm.ods.flow;

import com.gzzm.platform.organ.*;
import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * 公文实例删除记录，记录每个删除动作
 *
 * @author camel
 * @date 13-12-4
 */
@Entity(table = "ODFLOWINSTANCEDELETE", keys = "instanceId")
public class OdFlowInstanceDelete
{
    @ColumnDescription(type = "number(12)")
    private Long instanceId;

    private OdFlowInstance instance;

    /**
     * 删除的用户
     */
    private Integer userId;

    private User user;

    private Date deleteTime;

    /**
     * 删除的用户所属的部门，有别于instance的部门
     */
    private Integer deptId;

    private Dept dept;

    public OdFlowInstanceDelete()
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

    public Date getDeleteTime()
    {
        return deleteTime;
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

    public void setDeleteTime(Date deleteTime)
    {
        this.deleteTime = deleteTime;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof OdFlowInstanceDelete))
            return false;

        OdFlowInstanceDelete that = (OdFlowInstanceDelete) o;

        return instanceId.equals(that.instanceId);
    }

    @Override
    public int hashCode()
    {
        return instanceId.hashCode();
    }
}
