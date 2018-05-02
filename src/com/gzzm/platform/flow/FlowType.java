package com.gzzm.platform.flow;

import net.cyan.commons.util.AdvancedEnum;

/**
 * 流程类型，在xml文件中配置
 *
 * @author camel
 * @date 2011-7-4
 */
public class FlowType implements AdvancedEnum<String>
{
    private static final long serialVersionUID = -9006103403193018989L;

    private String type;

    private String name;

    public FlowType()
    {
    }

    public FlowType(String type)
    {
        this.type = type;
    }

    public FlowType(String type, String name)
    {
        this.type = type;
        this.name = name;
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
