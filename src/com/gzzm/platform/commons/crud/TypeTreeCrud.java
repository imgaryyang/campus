package com.gzzm.platform.commons.crud;

import net.cyan.arachne.annotation.Service;

/**
 * @author camel
 * @date 2018/3/28
 */
@Service
public abstract class TypeTreeCrud<T extends TreeTypeEntity> extends SimpleTreeCrud<T>
{
    public TypeTreeCrud()
    {
        super("typeName", "类型名称");
    }
}
