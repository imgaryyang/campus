package com.gzzm.platform.opinion;

import com.gzzm.platform.organ.User;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.*;

/**
 * 常用意见
 *
 * @author camel
 * @date 11-10-19
 */
@Entity(table = "PFOPINION", keys = "opinionId")
public class Opinion
{
    @Generatable(length = 9)
    private Integer opinionId;

    @Require
    @ColumnDescription(type = "varchar(1000)")
    private String title;

    @Require
    @ColumnDescription(type = "varchar(1000)")
    private String content;

    @Index
    private Integer userId;

    @NotSerialized
    private User user;

    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    @ColumnDescription(type = "number(9)", nullable = false, defaultValue = "0")
    private Integer frequency;

    public Opinion()
    {
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public Integer getOpinionId()
    {
        return opinionId;
    }

    public void setOpinionId(Integer opinionId)
    {
        this.opinionId = opinionId;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
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

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
    }

    public Integer getFrequency()
    {
        return frequency;
    }

    public void setFrequency(Integer frequency)
    {
        this.frequency = frequency;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof Opinion))
            return false;

        Opinion opinion = (Opinion) o;

        return opinionId.equals(opinion.opinionId);
    }

    @Override
    public int hashCode()
    {
        return opinionId.hashCode();
    }

    @Override
    public String toString()
    {
        return title;
    }
}
