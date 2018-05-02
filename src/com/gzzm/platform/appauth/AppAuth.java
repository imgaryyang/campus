package com.gzzm.platform.appauth;

import net.cyan.thunwind.annotation.*;

import java.util.List;

/**
 * 应用模块权限
 *
 * @author camel
 * @date 2011-5-13
 */
@Entity(table = "PFAPPAUTH", keys = "authId")
@Indexes(@Index(columns = {"TYPE", "APPID"}, unique = true))
public class AppAuth
{
    /**
     * 主键，权限ID
     */
    @Generatable(length = 9)
    private Integer authId;

    /**
     * 应用类型，由具体的应用定义
     */
    @ColumnDescription(type = "varchar(50)")
    private String type;

    /**
     * 应用数据ID
     */
    @ColumnDescription(type = "number(9)")
    private Integer appId;

    /**
     * 此权限包含的具体对象，即哪些对象能够使用此权限
     */
    @OneToMany
    private List<AppAuthItem> items;

    public AppAuth()
    {
    }

    public Integer getAuthId()
    {
        return authId;
    }

    public void setAuthId(Integer authId)
    {
        this.authId = authId;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public Integer getAppId()
    {
        return appId;
    }

    public void setAppId(Integer appId)
    {
        this.appId = appId;
    }

    public List<AppAuthItem> getItems()
    {
        return items;
    }

    public void setItems(List<AppAuthItem> items)
    {
        this.items = items;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof AppAuth))
            return false;

        AppAuth appAuth = (AppAuth) o;

        return authId.equals(appAuth.authId);
    }

    @Override
    public int hashCode()
    {
        return authId.hashCode();
    }
}
