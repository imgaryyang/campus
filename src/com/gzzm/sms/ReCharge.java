package com.gzzm.sms;

import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.*;

import java.sql.Date;

/**
 * 充值记录
 *
 * @author camel
 * @date 11-11-28
 */
@Entity(table = "SMRECHARGE", keys = "reChargeId")
public class ReCharge
{
    @Generatable(length = 9)
    private Integer reChargeId;

    @Require
    private Integer userId;

    private SmsUser user;

    /**
     * 网关类型
     */
    @Require
    private GatewayType type;

    /**
     * 充值时间
     */
    @Require
    private Date reChargeTime;

    /**
     * 充值数量
     */
    @Require
    @ColumnDescription(type = "number(6,2)")
    private Double amount;

    public ReCharge()
    {
    }

    public Integer getReChargeId()
    {
        return reChargeId;
    }

    public void setReChargeId(Integer reChargeId)
    {
        this.reChargeId = reChargeId;
    }

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

    public SmsUser getUser()
    {
        return user;
    }

    public void setUser(SmsUser user)
    {
        this.user = user;
    }

    public GatewayType getType()
    {
        return type;
    }

    public void setType(GatewayType type)
    {
        this.type = type;
    }

    public Date getReChargeTime()
    {
        return reChargeTime;
    }

    public void setReChargeTime(Date reChargeTime)
    {
        this.reChargeTime = reChargeTime;
    }

    public Double getAmount()
    {
        return amount;
    }

    public void setAmount(Double amount)
    {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof ReCharge))
            return false;

        ReCharge reCharge = (ReCharge) o;

        return reChargeId.equals(reCharge.reChargeId);

    }

    @Override
    public int hashCode()
    {
        return reChargeId.hashCode();
    }
}
