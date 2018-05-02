package com.gzzm.ods.urge;

import com.gzzm.platform.organ.User;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * 会签督办
 *
 * @author camel
 * @date 13-12-5
 */
@Entity(table = "ODCOLLECTURGE", keys = "receiveId")
public class CollectUrge
{
    @ColumnDescription(type = "number(12)")
    private Long receiveId;

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

    public CollectUrge()
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

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof CollectUrge))
            return false;

        CollectUrge that = (CollectUrge) o;

        return receiveId.equals(that.receiveId);
    }

    @Override
    public int hashCode()
    {
        return receiveId.hashCode();
    }
}
