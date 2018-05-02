package com.gzzm.oa.schedule;

import net.cyan.commons.util.AdvancedEnum;

/**
 * 日程优先级，优先级的取值通过配置文件配置，参考/WEB-INF/config/schedule.xml
 * 实现EnumObject接口，以实现动态枚举功能
 *
 * @author czf
 * @date 2010-3-10
 * @see com.gzzm.oa.schedule.Schedule#priority
 */
public class Priority implements AdvancedEnum<Integer>
{
    private static final long serialVersionUID = 2808681062339940367L;

    /**
     * 优先级的数值，为一个整数，从0开始
     */
    private int value;

    /**
     * 优先级显示的名称，例如紧急，一般等
     */
    private String name;

    /**
     * 此优先级在前台显示的颜色，为html中颜色的编码格式，如#FFFFFF
     */
    private String color;
    
    /**
     * 优先级在前台显示的图标，不同的优先级通过不同的图标区分
     * 其值为一个文件名，不包含路径，所有图标的路径为/oa/schedule/icons
     */
    private String icon;

    public int getValue()
    {
        return value;
    }

    public void setValue(int value)
    {
        this.value = value;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getColor()
    {
        return color;
    }

    public void setColor(String color)
    {
        this.color = color;
    }

    public Integer valueOf()
    {
        return value;
    }

    public String getIcon()
    {
        return icon;
    }

    public void setIcon(String icon)
    {
        this.icon = icon;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
