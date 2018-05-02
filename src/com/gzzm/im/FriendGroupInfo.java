package com.gzzm.im;

import java.util.*;

/**
 * 好友分组信息，并存储此组中的好友
 *
 * @author camel
 * @date 2010-12-31
 */
public class FriendGroupInfo
{
    private Long groupId;

    private String groupName;

    /**
     * 好友列表
     */
    private List<ImUserInfo> friends;

    private int onlineCount;

    public FriendGroupInfo()
    {
    }

    public FriendGroupInfo(Long groupId, String groupName)
    {
        this.groupId = groupId;
        this.groupName = groupName;
    }

    public Long getGroupId()
    {
        return groupId;
    }

    public void setGroupId(Long groupId)
    {
        this.groupId = groupId;
    }

    public String getGroupName()
    {
        return groupName;
    }

    public void setGroupName(String groupName)
    {
        this.groupName = groupName;
    }

    public void addFriend(ImUserInfo friend)
    {
        if (friends == null)
            friends = new ArrayList<ImUserInfo>();

        friend.setOrder(friends.size());

        boolean inserted = false;
        if (friend.isOnline())
        {
            onlineCount++;
            int n = friends.size();
            for (int i = 0; i < n; i++)
            {
                ImUserInfo friend1 = friends.get(i);
                if (!friend1.isOnline())
                {
                    friends.add(i, friend);
                    inserted = true;
                    break;
                }
            }
        }

        if (!inserted)
        {
            friends.add(friend);
        }
    }

    public List<ImUserInfo> getFriends()
    {
        return friends;
    }

    public int getOnlineCount()
    {
        return onlineCount;
    }
}
