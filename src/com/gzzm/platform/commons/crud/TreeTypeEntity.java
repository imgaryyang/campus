package com.gzzm.platform.commons.crud;

import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.*;

/**
 * 树形类型维护
 *
 * @author camel
 * @date 2018/3/28
 */
public abstract class TreeTypeEntity<T extends TreeTypeEntity>
{
    @Generatable(length = 6)
    @ColumnDescription(type = "number(6)")
    protected Integer typeId;

    @Require
    @ColumnDescription(type = "varchar(50)")
    protected String typeName;

    protected Integer parentTypeId;

    @NotSerialized
    @ToOne("PARENTTYPEID")
    protected T parentType;

    @ColumnDescription(type = "number(6)")
    protected Integer orderId;

    /**
     * 删除标志，1表示删除，0表示未删除
     */
    @ColumnDescription(type = "number(1)")
    protected Integer deleteTag;

    public TreeTypeEntity()
    {
    }

    public Integer getTypeId()
    {
        return typeId;
    }

    public void setTypeId(Integer typeId)
    {
        this.typeId = typeId;
    }

    public String getTypeName()
    {
        return typeName;
    }

    public void setTypeName(String typeName)
    {
        this.typeName = typeName;
    }

    public Integer getParentTypeId()
    {
        return parentTypeId;
    }

    public void setParentTypeId(Integer parentTypeId)
    {
        this.parentTypeId = parentTypeId;
    }

    public T getParentType()
    {
        return parentType;
    }

    public void setParentType(T parentType)
    {
        this.parentType = parentType;
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
    public int hashCode()
    {
        return typeId.hashCode();
    }

    @Override
    public String toString()
    {
        return typeName;
    }
}
