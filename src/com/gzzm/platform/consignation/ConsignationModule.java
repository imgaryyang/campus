package com.gzzm.platform.consignation;

import net.cyan.commons.util.AdvancedEnum;

/**
 * @author camel
 * @date 12-10-27
 */
public class ConsignationModule implements AdvancedEnum<String>
{
    private static final long serialVersionUID = 2624611572144962083L;

    private String type;

    private String name;

    public ConsignationModule()
    {
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
