package com.gzzm.platform.appauth;

import java.util.List;

/**
 * @author camel
 * @date 2016/5/1
 */
public class AppDeptList
{
    private List<AppDept> appDepts;

    private String tableName;

    public AppDeptList(List<AppDept> appDepts)
    {
        this.appDepts = appDepts;
    }

    public AppDeptList(String tableName)
    {
        this.tableName = tableName;
    }

    public List<AppDept> getAppDepts()
    {
        return appDepts;
    }

    public String getTableName()
    {
        return tableName;
    }
}
