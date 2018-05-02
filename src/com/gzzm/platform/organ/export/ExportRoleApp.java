package com.gzzm.platform.organ.export;

import com.gzzm.platform.organ.RoleApp;

import java.io.Serializable;

/**
 * @author camel
 * @date 13-12-26
 */
public class ExportRoleApp implements Serializable
{
    private static final long serialVersionUID = -414513542834978834L;

    private String appId;

    private Integer scopeId;

    private String auths;

    private String condition;

    private boolean self;

    public ExportRoleApp()
    {
    }

    public ExportRoleApp(RoleApp app)
    {
        appId = app.getAppId();
        scopeId = app.getScopeId();
        auths = app.getAuths();
        condition = app.getCondition();
        self = app.isSelf() != null && app.isSelf();
    }

    public String getAppId()
    {
        return appId;
    }

    public void setAppId(String appId)
    {
        this.appId = appId;
    }

    public Integer getScopeId()
    {
        return scopeId;
    }

    public void setScopeId(Integer scopeId)
    {
        this.scopeId = scopeId;
    }

    public String getAuths()
    {
        return auths;
    }

    public void setAuths(String auths)
    {
        this.auths = auths;
    }

    public String getCondition()
    {
        return condition;
    }

    public void setCondition(String condition)
    {
        this.condition = condition;
    }

    public boolean isSelf()
    {
        return self;
    }

    public void setSelf(boolean self)
    {
        this.self = self;
    }
}
