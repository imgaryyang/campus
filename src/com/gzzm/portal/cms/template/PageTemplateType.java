package com.gzzm.portal.cms.template;

import com.gzzm.platform.commons.Tools;
import net.cyan.commons.util.AdvancedEnum;

import java.util.*;

/**
 * 页面模版类型
 *
 * @author camel
 * @date 2011-5-10
 */
public class PageTemplateType implements AdvancedEnum<Integer>
{
    private static final long serialVersionUID = 747562436964612510L;

    private static List<PageTemplateType> VALUES = new ArrayList<PageTemplateType>();

    public final static PageTemplateType CHANNEL = new PageTemplateType(0, "channel");

    public final static PageTemplateType INFO = new PageTemplateType(1, "info");

    public final static PageTemplateType MAIN = new PageTemplateType(2, "main");

    public final static PageTemplateType PAGE = new PageTemplateType(3, "page");

    private Integer value;

    private String name;

    static
    {
        VALUES.add(CHANNEL);
        VALUES.add(INFO);
        VALUES.add(MAIN);
        VALUES.add(PAGE);
    }

    public PageTemplateType(Integer value, String name)
    {
        this.value = value;
        this.name = name;
    }

    public static List<PageTemplateType> values()
    {
        return VALUES;
    }

    public static PageTemplateType getValue(int value)
    {
        for (PageTemplateType type : VALUES)
        {
            if (type.value == value)
                return type;
        }

        return null;
    }

    public static PageTemplateType add(Integer value, String name)
    {
        PageTemplateType result = new PageTemplateType(value, name);

        List<PageTemplateType> values = new ArrayList<PageTemplateType>(VALUES);

        values.add(result);

        VALUES = values;

        return result;
    }

    public Integer getValue()
    {
        return value;
    }

    public String getName()
    {
        return name;
    }

    public Integer valueOf()
    {
        return value;
    }

    @Override
    public int hashCode()
    {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        PageTemplateType that = (PageTemplateType) o;

        return value.equals(that.value);
    }

    @Override
    public String toString()
    {
        return Tools.getMessage("com.gzzm.portal.cms.template.PageTemplateType." + name);
    }
}
