package com.gzzm.platform.commons.crud;

import net.cyan.thunwind.annotation.ColumnDescription;

/**
 * 类型维护
 *
 * @author camel
 * @date 2018/3/28
 */
public abstract class TypeEntity<E extends TypeEntity> extends SimpleEntity<E>
{
    private static final long serialVersionUID = 8769549572243660396L;

    @ColumnDescription(type = "number(6)")
    protected Integer typeId;

    @ColumnDescription(type = "varchar(50)")
    protected String typeName;

    public TypeEntity()
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

    @Override
    public Integer valueOf()
    {
        return typeId;
    }
}
