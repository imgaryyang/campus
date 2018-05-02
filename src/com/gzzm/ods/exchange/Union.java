package com.gzzm.ods.exchange;

import com.gzzm.platform.organ.Dept;
import net.cyan.thunwind.annotation.*;

/**
 * 联合发文
 *
 * @author camel
 * @date 2010-9-9
 */
@Entity(table = "ODUNION", keys = "receiveId")
public class Union
{
    /**
     * 收文ID
     */
    @ColumnDescription(type = "number(12)")
    private Long receiveId;

    @Lazy(false)
    private ReceiveBase receiveBase;

    /**
     * 发起联合办文的部门
     */
    private Integer unionDeptId;

    @ToOne("UNIONDEPTID")
    private Dept unionDept;

    /**
     * 此联合发文记录在主办单位流程中的stepId，每个联合发文记录会在主办单位的流程中生成一个step，以方便主办单位跟踪
     */
    @ColumnDescription(type = "number(13)")
    private Long stepId;

    public Union()
    {
    }

    public Long getReceiveId()
    {
        return receiveId;
    }

    public void setReceiveId(Long receiveId)
    {
        this.receiveId = receiveId;
    }

    public ReceiveBase getReceiveBase()
    {
        return receiveBase;
    }

    public void setReceiveBase(ReceiveBase receiveBase)
    {
        this.receiveBase = receiveBase;
    }

    public Integer getUnionDeptId()
    {
        return unionDeptId;
    }

    public void setUnionDeptId(Integer unionDeptId)
    {
        this.unionDeptId = unionDeptId;
    }

    public Dept getUnionDept()
    {
        return unionDept;
    }

    public void setUnionDept(Dept unionDept)
    {
        this.unionDept = unionDept;
    }

    public Long getStepId()
    {
        return stepId;
    }

    public void setStepId(Long stepId)
    {
        this.stepId = stepId;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof Union))
            return false;

        Union union = (Union) o;

        return receiveId.equals(union.receiveId);

    }

    @Override
    public int hashCode()
    {
        return receiveId.hashCode();
    }
}
