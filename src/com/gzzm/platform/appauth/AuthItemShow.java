package com.gzzm.platform.appauth;

/**
 * @author camel
 * @date 2011-5-13
 */
public class AuthItemShow extends AuthObject
{
    private String name;

    /**
     * 所属部门
     */
    private String deptName;

    private Boolean[] valids;

    public AuthItemShow()
    {
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDeptName()
    {
        return deptName;
    }

    public void setDeptName(String deptName)
    {
        this.deptName = deptName;
    }

    public Boolean[] getValids()
    {
        return valids;
    }

    public void setValids(Boolean[] valids)
    {
        this.valids = valids;
    }
}
