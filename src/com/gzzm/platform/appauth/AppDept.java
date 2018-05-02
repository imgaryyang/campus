package com.gzzm.platform.appauth;

/**
 * 用于创建临时表，表示用户对哪些应用拥有哪些部门的权限
 *
 * @author camel
 * @date 2011-5-21
 */
public class AppDept
{
    /**
     * 应用ID
     */
    private Integer appId;

    /**
     * 部门ID
     */
    private Integer deptId;

    public AppDept()
    {
    }

    public AppDept(Integer appId, Integer deptId)
    {
        this.appId = appId;
        this.deptId = deptId;
    }

    public Integer getAppId()
    {
        return appId;
    }

    public void setAppId(Integer appId)
    {
        this.appId = appId;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }
}
