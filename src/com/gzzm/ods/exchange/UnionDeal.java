package com.gzzm.ods.exchange;

import com.gzzm.ods.flow.OdFlowInstance;
import com.gzzm.platform.organ.User;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.*;

/**
 * 收文联合办文
 *
 * @author camel
 * @date 12-12-12
 */
@Entity(table = "ODUNIONDEAL", keys = "receiveId")
public class UnionDeal
{
    /**
     * 收文ID
     */
    @ColumnDescription(type = "number(12)")
    private Long receiveId;

    @NotSerialized
    @Lazy(false)
    private ReceiveBase receiveBase;

    /**
     * 发起会签的流程的instanceId
     */
    @Index
    private Long unionInstanceId;

    @NotSerialized
    @ToOne("UNIONINSTANCEID")
    private OdFlowInstance unionInstance;

    private Integer unionUserId;

    @NotSerialized
    @ToOne("UNIONUSERID")
    private User unionUser;

    @ColumnDescription(type = "number(13)")
    private Long unionStepId;

    /**
     * 此联合办文记录在主办单位流程中的stepId，每个联合办文记录会在主办单位的流程中生成一个step，以方便主办单位跟踪
     */
    @ColumnDescription(type = "number(13)")
    private Long stepId;

    public UnionDeal()
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

    public Long getUnionInstanceId()
    {
        return unionInstanceId;
    }

    public void setUnionInstanceId(Long unionInstanceId)
    {
        this.unionInstanceId = unionInstanceId;
    }

    public OdFlowInstance getUnionInstance()
    {
        return unionInstance;
    }

    public void setUnionInstance(OdFlowInstance unionInstance)
    {
        this.unionInstance = unionInstance;
    }

    public Integer getUnionUserId()
    {
        return unionUserId;
    }

    public void setUnionUserId(Integer unionUserId)
    {
        this.unionUserId = unionUserId;
    }

    public User getUnionUser()
    {
        return unionUser;
    }

    public void setUnionUser(User unionUser)
    {
        this.unionUser = unionUser;
    }

    public Long getUnionStepId()
    {
        return unionStepId;
    }

    public void setUnionStepId(Long unionStepId)
    {
        this.unionStepId = unionStepId;
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

        if (!(o instanceof UnionDeal))
            return false;

        UnionDeal unionDeal = (UnionDeal) o;

        return receiveId.equals(unionDeal.receiveId);
    }

    @Override
    public int hashCode()
    {
        return receiveId.hashCode();
    }
}
