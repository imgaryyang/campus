package com.gzzm.platform.desktop;

import java.io.Serializable;

/**
 * 桌面模块
 *
 * @author camel
 * @date 2010-6-3
 */
public class DesktopModule implements Serializable
{
    private static final long serialVersionUID = -5224566445294038152L;

    /**
     * 功能ID
     */
    private String appId;

    /**
     * 功能模块的url，与appId互斥，如果定义了appId，则此域为null，自动生成url
     */
    private String url;

    /**
     * title，如果定义了appId，则此域为null，自动从Menu表取得title
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    public DesktopModule()
    {
    }

    public String getAppId()
    {
        return appId;
    }

    public void setAppId(String appId)
    {
        this.appId = appId;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }
}
