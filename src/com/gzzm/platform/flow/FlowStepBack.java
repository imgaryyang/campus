package com.gzzm.platform.flow;

import com.gzzm.platform.organ.User;
import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * 打回意见
 *
 * @author camel
 * @date 11-11-25
 */
@Entity(table = "PFFLOWSTEPBACK", keys = "backId")
public class FlowStepBack
{
    @Generatable(length = 11)
    private Long backId;

    @ColumnDescription(type = "number(13)")
    private Long stepId;

    @ColumnDescription(type = "varchar(4000)")
    private String remark;

    /**
     * 打回人的ID
     */
    private Integer userId;

    private User user;

    /**
     * 打回的时间
     */
    private Date backTime;

    public FlowStepBack()
    {
    }

    public Long getBackId()
    {
        return backId;
    }

    public void setBackId(Long backId)
    {
        this.backId = backId;
    }

    public Long getStepId()
    {
        return stepId;
    }

    public void setStepId(Long stepId)
    {
        this.stepId = stepId;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
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

    public Date getBackTime()
    {
        return backTime;
    }

    public void setBackTime(Date backTime)
    {
        this.backTime = backTime;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof FlowStepBack))
            return false;

        FlowStepBack that = (FlowStepBack) o;

        return backId.equals(that.backId);
    }

    @Override
    public int hashCode()
    {
        return backId.hashCode();
    }
}