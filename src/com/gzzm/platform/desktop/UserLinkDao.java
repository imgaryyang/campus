package com.gzzm.platform.desktop;

import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.List;

/**
 * @author camel
 * @date 2016/9/24
 */
public abstract class UserLinkDao extends GeneralDao
{
    public UserLinkDao()
    {
    }

    @LoadByKey
    public abstract UserLinkGroup getGroup(Integer groupId) throws Exception;

    @GetByField({"userId", "groupName"})
    public abstract UserLinkGroup getGroupByName(Integer userId, String groupName) throws Exception;

    @OQL("select groupName from UserLinkGroup where userId=:1 order by orderId")
    public abstract List<String> getGroupNames(Integer userId) throws Exception;

    @OQL("select g from UserLinkGroup g where userId=:1 order by orderId")
    public abstract List<UserLinkGroup> getGroups(Integer userId) throws Exception;

    @OQL("select l from UserLink l where userId in :1 order by userId,linkGroup.orderId,orderId")
    public abstract List<UserLink> getUserLinks(Integer... userIds) throws Exception;
}
