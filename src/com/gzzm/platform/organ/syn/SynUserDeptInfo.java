package com.gzzm.platform.organ.syn;

import java.io.Serializable;
import java.util.*;

/**
 * 用户部门信息
 *
 * @author camel
 * @date 2011-4-19
 */
public class SynUserDeptInfo implements Serializable
{
    private static final long serialVersionUID = -2265728388001244783L;

    /**
     * 原系统部门ID
     */
    private String deptId;

    /**
     * 此用户在此部门内的排序
     */
    private int sort;

    private List<Integer> stationIds;

    private List<Integer> roleIds;

    public SynUserDeptInfo()
    {
    }

    public SynUserDeptInfo(String deptId, int sort)
    {
        this.deptId = deptId;
        this.sort = sort;
    }

    public String getDeptId()
    {
        return deptId;
    }

    public void setDeptId(String deptId)
    {
        this.deptId = deptId;
    }

    public int getSort()
    {
        return sort;
    }

    public void setSort(int sort)
    {
        this.sort = sort;
    }

    public List<Integer> getStationIds()
    {
        return stationIds;
    }

    public List<Integer> getRoleIds()
    {
        return roleIds;
    }

    public void addStation(Integer stationId)
    {
        if (stationIds == null)
        {
            stationIds = new ArrayList<Integer>();
        }

        if (!stationIds.contains(stationId))
            stationIds.add(stationId);
    }

    public void addRole(Integer roleId)
    {
        if (roleIds == null)
        {
            roleIds = new ArrayList<Integer>();
        }

        if (!roleIds.contains(roleId))
            roleIds.add(roleId);
    }
}
