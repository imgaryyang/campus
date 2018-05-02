package com.gzzm.im;

import java.util.*;

/**
 * 好友列表，并将好友分组存放
 *
 * @author camel
 * @date 2010-12-31
 */
public class FriendList
{
    /**
     * 好友分组，分组中存储好友信息
     */
    private List<FriendGroupInfo> groups = new ArrayList<FriendGroupInfo>();

    public FriendList()
    {
        //添加一个默认分组，用于存放那些不属于任何分组的好友
        addGroup(new FriendGroupInfo(0L, "常用联系人"));//原来是我的好友
    }

    public List<FriendGroupInfo> getGroups()
    {
        return groups;
    }

    public void addGroup(FriendGroupInfo group)
    {
        groups.add(group);
    }

    public FriendGroupInfo getGroupInfo(Long groupId)
    {
        for (FriendGroupInfo group : groups)
        {
            if (group.getGroupId().equals(groupId))
                return group;
        }

        return null;
    }

    public void addFriend(ImUserInfo friend, Long groupId)
    {
        if (groupId == null)
            groupId = 0L;

        FriendGroupInfo group = getGroupInfo(groupId);
        if (group == null)
            group = getGroupInfo(0L);

        group.addFriend(friend);
    }
}
