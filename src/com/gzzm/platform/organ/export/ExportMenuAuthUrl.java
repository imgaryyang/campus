package com.gzzm.platform.organ.export;

import com.gzzm.platform.menu.*;

import java.io.Serializable;

/**
 * @author camel
 * @date 13-12-26
 */
public class ExportMenuAuthUrl implements Serializable
{
    private static final long serialVersionUID = -1327729762729864529L;

    private String urlName;

    private String url;

    private HttpMethod method;

    public ExportMenuAuthUrl()
    {
    }

    public ExportMenuAuthUrl(MenuAuthUrl authUrl)
    {
        urlName = authUrl.getUrlName();
        url = authUrl.getUrl();
        method = authUrl.getMethod();
    }

    public String getUrlName()
    {
        return urlName;
    }

    public void setUrlName(String urlName)
    {
        this.urlName = urlName;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public HttpMethod getMethod()
    {
        return method;
    }

    public void setMethod(HttpMethod method)
    {
        this.method = method;
    }
}
