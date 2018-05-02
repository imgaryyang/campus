package com.gzzm.ods.exchange;

import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.*;

/**
 * 抄送记录，即讲公文发送到人
 *
 * @author camel
 * @date 12-12-16
 */
@Entity(table = "ODCOPY", keys = "receiveId")
public class Copy
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
     * 在pfstep表对应的stepId;
     */
    @Index
    @ColumnDescription(type = "number(13)")
    private Long stepId;

    public Copy()
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
        if (this == o) return true;
        if (!(o instanceof Copy)) return false;

        Copy copy = (Copy) o;

        return receiveId.equals(copy.receiveId);
    }

    @Override
    public int hashCode()
    {
        return receiveId.hashCode();
    }
}
