package com.gzzm.platform.form;

import net.cyan.commons.util.AdvancedEnum;

/**
 * 表单类型，在xml文件中配置
 *
 * @author camel
 * @date 2011-7-12
 */
public class FormType implements AdvancedEnum<String>
{
    private static final long serialVersionUID = 3144128697941319207L;

    private String type;

    private String name;

    public FormType()
    {
    }

    public FormType(String type)
    {
        this.type = type;
    }

    public FormType(String type, String name)
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
