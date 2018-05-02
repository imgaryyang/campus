package com.gzzm.platform.appauth;

import net.cyan.nest.annotation.Injectable;

/**
 * @author camel
 * @date 2016/5/1
 */
@Injectable(singleton = true)
public class AppAuthConfig
{
    private String schema;

    private boolean cacheAuth;

    public AppAuthConfig()
    {
    }

    public String getSchema()
    {
        return schema;
    }

    public void setSchema(String schema)
    {
        this.schema = schema;
    }

    public boolean isCacheAuth()
    {
        return cacheAuth;
    }

    public void setCacheAuth(boolean cacheAuth)
    {
        this.cacheAuth = cacheAuth;
    }
}
