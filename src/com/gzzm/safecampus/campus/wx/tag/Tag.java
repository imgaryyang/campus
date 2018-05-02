package com.gzzm.safecampus.campus.wx.tag;

import com.gzzm.safecampus.wx.user.IdentifyType;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.ColumnDescription;
import net.cyan.thunwind.annotation.Entity;

/**
 * 微信用户标签，通过微信api进行创建
 *
 * @author Neo
 * @date 2018/4/20 20:00
 */
@Entity(table = "SCTAG", keys = "tagId")
public class Tag
{
    /**
     * 微信返回的标签Id，唯一
     */
    @ColumnDescription(type = "NUMBER(4)")
    private Integer tagId;

    @ColumnDescription(type = "VARCHAR2(20)")
    @Unique
    private String tagName;

    @ColumnDescription(type = "NUMBER(3)")
    private Integer orderId;

    /**
     * 标签关联的身份类型
     * 系统会根据微信用户认证的身份类型给该用户打上对应的标签
     */
    @Require
    private IdentifyType identifyType;

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

    public IdentifyType getIdentifyType()
    {
        return identifyType;
    }

    public void setIdentifyType(IdentifyType identifyType)
    {
        this.identifyType = identifyType;
    }

    @Override
    public String toString()
    {
        return tagName;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof Tag)) return false;

        Tag tag = (Tag) o;

        return tagId.equals(tag.tagId);

    }

    @Override
    public int hashCode()
    {
        return tagId.hashCode();
    }
}
