package com.gzzm.platform.recent;

import com.gzzm.platform.commons.Tools;

/**
 * @author camel
 * @date 2016/1/15
 */
public class RecentType
{
    private String type;

    private String text;

    public RecentType()
    {
    }

    public RecentType(String type)
    {
        this(type, Tools.getMessage("com.gzzm.platform.recent.Recent." + type));
    }

    public RecentType(String type, String text)
    {
        this.type = type;
        this.text = text;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }
}
