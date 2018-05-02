package com.gzzm.platform.group;

import com.gzzm.platform.organ.OrganDao;
import net.cyan.thunwind.annotation.OQL;

import java.util.List;

/**
 * 组相关的dao
 *
 * @author camel
 * @date 2010-8-26
 */
public abstract class GroupDao extends OrganDao
{
    public GroupDao()
    {
    }

    public UserGroup getUserGroup(Integer groupId) throws Exception
    {
        return get(UserGroup.class, groupId);
    }

    public DeptGroup getDeptGroup(Integer groupId) throws Exception
    {
        return get(DeptGroup.class, groupId);
    }

    @OQL("select g from UserGroup g where type=0 and owner=:1 order by orderId")
    public abstract List<UserGroup> getUserGroupsByUser(Integer userId) throws Exception;

    @OQL("select g from UserGroup g where type=1 and owner=:1 order by orderId")
    public abstract List<UserGroup> getUserGroupsByDept(Integer deptId) throws Exception;

    @OQL("select g from DeptGroup g where deptId=:1 and (self=0 or creator=:2) order by self desc,orderId")
    public abstract List<DeptGroup> getDeptGroupsByDept(Integer deptId, Integer userId) throws Exception;
}