package com.gzzm.platform.appauth;

/**
 * @author camel
 * @date 13-11-11
 */
public class ObjectScope extends AuthObject
{
    protected Integer scopeId;

    public ObjectScope()
    {
    }

    public Integer getScopeId()
    {
        return scopeId;
    }

    public void setScopeId(Integer scopeId)
    {
        this.scopeId = scopeId;
    }
}
