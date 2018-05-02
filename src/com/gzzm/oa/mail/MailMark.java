package com.gzzm.oa.mail;

import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.*;

/**
 * 邮件标记
 *
 * @author lfx
 * @date 2010-3-26
 */
@Entity(table = "OAMAILMARK", keys = "markId")
public class MailMark
{
    /**
     * 标记ID，主键
     */
    @Generatable(length = 13)
    private Long markId;

    /**
     * 系统定义标记或者用户定义标记
     */
    private MailMarkType markType;

    /**
     * 所属用户Id，如果markType为user时为null
     */
    @Index
    private Integer userId;

    /**
     * 标记名称
     */
    @Require
    private String markName;

    /**
     * 图片
     */
    private byte[] icon;

    /**
     * 标记颜色
     */
    private String color;

    /**
     * 排序ID，越小表示标记越排在前面
     */
    private Integer orderId;

    public MailMark()
    {
    }

    public Long getMarkId()
    {
        return markId;
    }

    public void setMarkId(Long markId)
    {
        this.markId = markId;
    }

    public String getMarkName()
    {
        return markName;
    }

    public void setMarkName(String markName)
    {
        this.markName = markName;
    }


    public String getColor()
    {
        return color;
    }

    public void setColor(String color)
    {
        this.color = color;
    }

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

    public MailMarkType getMarkType()
    {
        return markType;
    }

    public void setMarkType(MailMarkType markType)
    {
        this.markType = markType;
    }


    public byte[] getIcon()
    {
        return icon;
    }

    public void setIcon(byte[] icon)
    {
        this.icon = icon;
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
        return markId.hashCode();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof MailMark))
            return false;

        MailMark mailMark = (MailMark) o;
        return markId.equals(mailMark.markId);
    }

    @Override
    public String toString()
    {
        return markName;
    }
}
