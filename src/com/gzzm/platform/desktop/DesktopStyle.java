package com.gzzm.platform.desktop;

import net.cyan.commons.util.AdvancedEnum;

/**
 * 桌面风格
 *
 * @author camel
 * @date 2010-3-25
 */
public class DesktopStyle implements AdvancedEnum<String>
{
    private static final long serialVersionUID = -2804922763569839051L;

    private String path;

    private String name;

    public DesktopStyle()
    {
    }

    public DesktopStyle(String path)
    {
        this.path = path;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
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
        return path;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true
                    ;
        if (!(o instanceof DesktopStyle))
            return false;

        DesktopStyle that = (DesktopStyle) o;

        return path.equals(that.path);

    }

    @Override
    public int hashCode()
    {
        return path.hashCode();
    }

    @Override
    public String toString()
    {
        return name;
    }
}
