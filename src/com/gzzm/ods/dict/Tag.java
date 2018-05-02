package com.gzzm.ods.dict;

import com.gzzm.platform.organ.User;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

/**
 * 流程标签，给流程打上各种标记
 *
 * @author camel
 * @date 13-1-4
 */
@Entity(table = "ODTAG", keys = "tagId")
public class Tag
{
    /**
     * 主键
     */
    @Generatable(length = 7)
    private Integer tagId;

    /**
     * 标记名称
     */
    @Require
    @Unique
    @ColumnDescription(type = "varchar(250)")
    private String tagName;

    /**
     * 排序
     */
    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    private Integer userId;

    @NotSerialized
    private User user;

    public Tag()
    {
    }

    public Integer getTagId()
    {
        return tagId;
    }

    public void setTagId(Integer tagId)
    {
        this.tagId = tagId;
    }

    public String getTagName()
    {
        return tagName;
    }

    public void setTagName(String tagName)
    {
        this.tagName = tagName;
    }

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
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

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof Tag))
            return false;

        Tag tag = (Tag) o;

        return tagId.equals(tag.tagId);
    }

    @Override
    public int hashCode()
    {
        return tagId.hashCode();
    }

    @Override
    public String toString()
    {
        return tagName;
    }
}
