package com.gzzm.oa.activite;

import com.gzzm.platform.organ.User;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.*;

import java.util.Date;


/**
 * 日常活动成员
 *
 * @author fwj
 * @date 11-9-29
 */
@Entity(table = "OAACTIVITEMEMBER", keys = "memberId")
public class ActiviteMember
{
    /**
     * 成员ID
     */
    @Generatable(length = 8)
    private Integer memberId;

    /**
     * 活动Id
     */
    @Index
    @Require
    private Integer activiteId;

    /**
     * 关联活动表
     */
    @Cascade
    private Activite activite;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 关联用户表
     */
    private User user;

    /**
     * 申请或邀请时间
     */
    @Require
    private Date applyTime;

    /**
     * 状态
     */
    @Require
    private MemberState state;

    /**
     * 排序Id
     */
    @ColumnDescription(type = "number(4)")
    private Integer orderId;

    public ActiviteMember()
    {
    }

    public Integer getMemberId()
    {
        return memberId;
    }

    public void setMemberId(Integer memberId)
    {
        this.memberId = memberId;
    }

    public Integer getActiviteId()
    {
        return activiteId;
    }

    public void setActiviteId(Integer activiteId)
    {
        this.activiteId = activiteId;
    }

    public Activite getActivite()
    {
        return activite;
    }

    public void setActivite(Activite activite)
    {
        this.activite = activite;
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

    public Date getApplyTime()
    {
        return applyTime;
    }

    public void setApplyTime(Date applyTime)
    {
        this.applyTime = applyTime;
    }

    public MemberState getState()
    {
        return state;
    }

    public void setState(MemberState state)
    {
        this.state = state;
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
    public int hashCode()
    {
        return memberId.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;

        if (!(obj instanceof ActiviteMember))
            return false;

        ActiviteMember activiteMember = (ActiviteMember) obj;

        return memberId.equals(activiteMember.memberId);
    }

    @Override
    public String toString()
    {
        return user.getUserName();
    }
}
