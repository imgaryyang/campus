package com.gzzm.platform.commons;

import net.cyan.arachne.annotation.NotSerialized;

/**
 * 性别
 *
 * @author camel
 * @date 2009-7-18
 */
public enum Sex
{
    male, female;

    @NotSerialized
    public String getIcon()
    {
        return Tools.getCommonIcon(name());
    }
}
