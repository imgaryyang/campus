package com.gzzm.platform.menu;

import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.*;
import net.cyan.arachne.annotation.NotSerialized;

/**
 * 菜单角色url
 *
 * @author camel
 * @date 2009-12-23
 */
@Entity(table = "PFMENUAUTHURL", keys = "urlId")
public class MenuAuthUrl
{
    /**
     * urlId
     */
    @Generatable(length = 9)
    private Integer urlId;


    /**
     * 关联的权限ID
     */
    @Require
    private Integer authId;

    /**
     * url的名称
     */
    @Require
    @ColumnDescription(type = "varchar(50)")
    private String urlName;

    /**
     * url的内容
     */
    @Require
    @ColumnDescription(type = "varchar(250)")
    private String url;

    /**
     * 对应的http方法，取值为
     */
    private HttpMethod method;

    /**
     * 关联的权限
     */
    @NotSerialized
    @Cascade
    private MenuAuth auth;

    public MenuAuthUrl()
    {
    }

    public MenuAuthUrl(String urlName, String url)
    {
        this.urlName = urlName;
        this.url = url;
    }

    public MenuAuthUrl(String urlName, String url, HttpMethod method)
    {
        this.urlName = urlName;
        this.url = url;
        this.method = method;
    }

    public Integer getUrlId()
    {
        return urlId;
    }

    public void setUrlId(Integer urlId)
    {
        this.urlId = urlId;
    }

    public Integer getAuthId()
    {
        return authId;
    }

    public void setAuthId(Integer authId)
    {
        this.authId = authId;
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

    public MenuAuth getAuth()
    {
        return auth;
    }

    public void setAuth(MenuAuth auth)
    {
        this.auth = auth;
    }

    public HttpMethod getMethod()
    {
        return method;
    }

    public void setMethod(HttpMethod method)
    {
        this.method = method;
    }

    @AfterAdd
    @AfterUpdate
    public void afterModify() throws Exception
    {
        Menu.setUpdated();
    }

    @Override
    public boolean equals(Object o)
    {
        return this == o || o instanceof MenuAuthUrl && urlId.equals(((MenuAuthUrl) o).urlId);
    }

    @Override
    public int hashCode()
    {
        return urlId.hashCode();
    }

    @Override
    public String toString()
    {
        return urlName;
    }
}
