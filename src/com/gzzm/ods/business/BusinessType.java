package com.gzzm.ods.business;

import net.cyan.commons.util.AdvancedEnum;

/**
 * 业务类型，动态枚举类型，实现从配置文件读取业务类型的配置
 *
 * @author camel
 * @date 2011-7-4
 */
public class BusinessType implements AdvancedEnum<String>
{
    private static final long serialVersionUID = 1273455881371169672L;

    private String type;

    private String name;

    public BusinessType()
    {
    }

    public BusinessType(String type, String name)
    {
        this.type = type;
        this.name = name;
    }

    public BusinessType(String type)
    {
        this.type = type;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String valueOf()
    {
        return type;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
