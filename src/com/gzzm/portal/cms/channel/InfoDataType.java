package com.gzzm.portal.cms.channel;

/**
 * @author camel
 * @date 12-1-31
 */
public enum InfoDataType
{
    STRING,

    TEXT,

    INTEGER,

    NUMBER,

    DATE;

    public String value()
    {
        return name().toLowerCase();
    }
}
