package com.gzzm.portal.user.crud;

import com.gzzm.platform.commons.Tools;

/**
 * 定义排序项
 *
 * @author camel
 * @date 2011-8-11
 */
public class OrderItem
{
    /**
     * 排序的字段
     */
    private String field;

    /**
     * 字段的标题，即中文名称
     */
    private String title;

    public OrderItem()
    {
    }

    public OrderItem(String field, String title)
    {
        this.field = field;
        this.title = title;
    }

    public String getField()
    {
        return field;
    }

    public void setField(String field)
    {
        this.field = field;
    }

    public String getTitle()
    {
        return Tools.getMessage(title);
    }

    public void setTitle(String title)
    {
        this.title = title;
    }
}
