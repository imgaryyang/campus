package com.gzzm.portal.weather;

import net.cyan.nest.annotation.Injectable;

/**
 * @author camel
 * @date 2014/8/12
 */
@Injectable
public class WeatherConfig
{
    private String defaultContent = "";

    public WeatherConfig()
    {
    }

    public String getDefaultContent()
    {
        return defaultContent;
    }

    public void setDefaultContent(String defaultContent)
    {
        this.defaultContent = defaultContent;
    }
}
