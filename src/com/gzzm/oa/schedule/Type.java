package com.gzzm.oa.schedule;

import net.cyan.commons.util.AdvancedEnum;

/**
 * 日程类型枚举，如约会，会议等
 * 日程类型的取值通过配置文件配置，参考/WEB-INF/config/schedule.xml
 * 实现EnumObject接口，以实现动态枚举功能
 *
 * @author czf
 * @date 2010-3-10
 * @see com.gzzm.oa.schedule.Schedule#type
 */
public class Type implements AdvancedEnum<Integer>
{
    private static final long serialVersionUID = 2808681062339940367L;

    /**
     * 日程类型的数值，对应到数据库的字段值，为一整数，从0开始
     */
    private int value;

    /**
     * 日程类型在前台显示的名称，如约会，会议等
     */
    private String name;

    /**
     * 此类型的日程在前台显示的图标，不同类型的日程通过不同的图标区分
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

    public String getIcon()
    {
        return icon;
    }

    public void setIcon(String icon)
    {
        this.icon = icon;
    }

    public Integer valueOf()
    {
        return value;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
