package com.gzzm.platform.commons.crud;

import net.cyan.commons.util.*;
import net.cyan.thunwind.annotation.ColumnDescription;

/**
 * @author camel
 * @date 2018/3/29
 */
public abstract class SimpleEntity<E extends SimpleEntity> implements Comparable<E>, AdvancedEnum<Integer>
{
    private static final long serialVersionUID = 2481663200332242712L;

    @ColumnDescription(type = "number(6)")
    protected Integer orderId;

    /**
     * 删除标志，1表示删除，0表示未删除
     */
    @ColumnDescription(type = "number(1)")
    protected Integer deleteTag;

    public SimpleEntity()
    {
    }

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
    }

    public Integer getDeleteTag()
    {
        return deleteTag;
    }

    public void setDeleteTag(Integer deleteTag)
    {
        this.deleteTag = deleteTag;
    }

    @Override
    public int compareTo(E o)
    {
        return DataConvert.compare(orderId, o.getOrderId());
    }
}
