package com.gzzm.platform.organ;

import java.util.List;

/**
 * @author camel
 * @date 13-7-17
 */
public class OutterUserConfig
{
    private Integer deptId;

    private List<Integer> roleIds;

    public OutterUserConfig()
    {
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public List<Integer> getRoleIds()
    {
        return roleIds;
    }

    public void setRoleIds(List<Integer> roleIds)
    {
        this.roleIds = roleIds;
    }
}
