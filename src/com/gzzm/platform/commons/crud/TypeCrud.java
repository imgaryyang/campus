package com.gzzm.platform.commons.crud;

import net.cyan.arachne.annotation.Service;
import net.cyan.crud.annotation.Like;

/**
 * @author camel
 * @date 2018/3/28
 */
@Service
public class TypeCrud<T extends TypeEntity> extends SimpleNormalCrud<T>
{
    @Like
    protected String typeName;

    public TypeCrud()
    {
        super("typeName","类型名称");
        setLog(true);
    }

    public String getTypeName()
    {
        return typeName;
    }

    public void setTypeName(String typeName)
    {
        this.typeName = typeName;
    }
}
