package com.gzzm.ods.urge;

import com.gzzm.platform.organ.User;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.util.DateUtils;
import net.cyan.commons.validate.annotation.*;
import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * 督办信息
 *
 * @author camel
 * @date 11-11-17
 */
@Entity(table = "ODFLOWURGE", keys = "instanceId")
public class OdFlowUrge
{
    /**
     * 实例id
     *
     * @see com.gzzm.ods.flow.OdFlowInstance#instanceId
     */
    @ColumnDescription(type = "number(12)")
    private Long instanceId;

    /**
     * 发起督办人
     */
    private Integer userId;

    @NotSerialized
    private User user;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 办理时限
     */
    @Require
    private Date limitTime;

    /**
     * 开始督办时间
     */
    @Require
    private Date urgeTime;

    /**
     * 督办的意见
     */
    @Require
    private String content;

    /**
     * 提醒方式
     */
    @Require
    private UrgeRemindType remindType;

    /**
     * 最后提醒时间
     */
    private Date lastRemindTime;

    public OdFlowUrge()
    {
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

    public Long getInstanceId()
    {
        return instanceId;
    }

    public void setInstanceId(Long instanceId)
    {
        this.instanceId = instanceId;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public Date getLimitTime()
    {
        return limitTime;
    }

    public void setLimitTime(Date limitTime)
    {
        this.limitTime = limitTime;
    }

    public Date getUrgeTime()
    {
        return urgeTime;
    }

    public void setUrgeTime(Date urgeTime)
    {
        this.urgeTime = urgeTime;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public UrgeRemindType getRemindType()
    {
        return remindType;
    }

    public void setRemindType(UrgeRemindType remindType)
    {
        this.remindType = remindType;
    }

    public Date getLastRemindTime()
    {
        return lastRemindTime;
    }

    public void setLastRemindTime(Date lastRemindTime)
    {
        this.lastRemindTime = lastRemindTime;
    }

    @FieldValidator("urgeTime")
    public String checkUrgeTime()
    {
        if (urgeTime != null && limitTime != null)
        {
            if (urgeTime.after(DateUtils.addDate(limitTime, 1)))
                return "com.gzzm.ods.urge.urgeTimeError";
        }

        return null;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof OdFlowUrge))
            return false;

        OdFlowUrge that = (OdFlowUrge) o;

        return instanceId.equals(that.instanceId);
    }

    @Override
    public int hashCode()
    {
        return instanceId.hashCode();
    }
}
