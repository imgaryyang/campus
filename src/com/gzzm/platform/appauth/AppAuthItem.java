package com.gzzm.platform.appauth;

import com.gzzm.platform.organ.RoleScope;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.*;

/**
 * appauth的具体内容，设置此权可被哪些具体对象使用，每个对象使用的数据范围是什么
 *
 * @author camel
 * @date 2011-5-13
 */
@Entity(table = "PFAPPAUTHITEM", keys = {"authId", "type", "objectId"})
public class AppAuthItem extends ObjectScope
{
    private Integer authId;

    @Lazy(false)
    private AppAuth auth;

    /**
     * 作用范围
     */
    @NotSerialized
    private RoleScope scope;

    public AppAuthItem()
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

    public AppAuth getAuth()
    {
        return auth;
    }

    public void setAuth(AppAuth auth)
    {
        this.auth = auth;
    }

    public RoleScope getScope()
    {
        return scope;
    }

    public void setScope(RoleScope scope)
    {
        this.scope = scope;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof AppAuthItem))
            return false;

        AppAuthItem that = (AppAuthItem) o;

        return authId.equals(that.authId) && objectId.equals(that.objectId) && type == that.type;
    }

    @Override
    public int hashCode()
    {
        int result = authId.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + objectId.hashCode();
        return result;
    }
}
