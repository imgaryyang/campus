package com.gzzm.platform.organ;

import net.cyan.commons.util.StringUtils;

import java.util.*;

/**
 * 功能信息，定义在线用户对某个功能的使用情况 包括对此功能拥有的权限，和功能的使用范围
 *
 * @author camel
 * @date 2009-7-21
 */
public class AppInfo extends Scopes
{
    /**
     * 拥有的权限，此属性为null表示拥有此功能的任何权限
     */
    private List<String> auths;

    private List<String> menuAuths;

    private String appId;

    /**
     * 附加查询条件
     *
     * @see com.gzzm.platform.organ.RoleApp#condition
     * @see com.gzzm.platform.commons.crud.SystemCrudUtils#getCondition(String)
     */
    private String condition;

    private boolean self = true;

    public AppInfo(String appId)
    {
        this.appId = appId;
    }

    public AppInfo(String appId, List<String> menuAuths)
    {
        this.appId = appId;
        this.menuAuths = menuAuths;
    }

    public String getAppId()
    {
        return appId;
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

    public void add(RoleApp roleApp, Integer userDeptId) throws Exception
    {
        String s = roleApp.getAuths();
        if (s == null)
            s = "";
        String[] auths = s.split(",");
        for (String auth : auths)
        {
            if (this.auths == null)
                this.auths = new ArrayList<String>();

            if (!this.auths.contains(auth))
                this.auths.add(auth);
        }

        String condition = roleApp.getCondition();
        if (!StringUtils.isEmpty(condition))
        {
            if (this.condition != null)
                this.condition += " or (" + condition + ")";
            else
                this.condition = "(" + condition + ")";
        }

        if (roleApp.getScopeId() != null && roleApp.getScopeId() == -1)
        {
            setAll();
        }
        else
        {
            RoleScope scope = roleApp.getScope();
            if (scope != null)
            {
                add(scope.getRoleScopeDepts(), userDeptId, null);
            }
            else
            {
                putDept(userDeptId, false, false, false, null);
            }
        }
    }

    /**
     * 判断是否拥有某个权限
     *
     * @param auth 权限
     * @return 是返回true，不是返回false
     */
    public boolean hasAuth(String auth)
    {
        return hasAuth(auth, false);
    }

    /**
     * 判断是否拥有某个权限
     *
     * @param auth 权限
     * @return 是返回true，不是返回false
     */
    public boolean hasAuth(String auth, boolean force)
    {
        if (menuAuths == null || !menuAuths.contains(auth))
            return !force;

        return auths != null && (auths.contains(auth) || auths.contains("#all"));
    }
}