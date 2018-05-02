package com.gzzm.im;

import java.util.*;

/**
 * 某个群的好友列表
 *
 * @author camel
 * @date 2010-12-31
 */
public class GroupMemberList
{
    private Integer groupId;

    /**
     * 好友列表
     */
    private List<ImUserInfo> groupMembers;

    private int onlineCount;

    public GroupMemberList(Integer groupId)
    {
        this.groupId = groupId;
    }

    public Integer getGroupId()
    {
        return groupId;
    }

    public void setGroupId(Integer groupId)
    {
        this.groupId = groupId;
    }

    public void addGroupMember(ImUserInfo group)
    {
        if (groupMembers == null)
            groupMembers = new ArrayList<ImUserInfo>();

        group.setOrder(groupMembers.size());

        boolean inserted = false;
        if (group.isOnline())
        {
            onlineCount++;
            int n = groupMembers.size();
            for (int i = 0; i < n; i++)
            {
                ImUserInfo group1 = groupMembers.get(i);
                if (!group1.isOnline())
                {
                    groupMembers.add(i, group);
                    inserted = true;
                    break;
                }
            }
        }

        if (!inserted)
        {
            groupMembers.add(group);
        }
    }

    public List<ImUserInfo> getGroupMembers()
    {
        return groupMembers;
    }

    public int getOnlineCount()
    {
        return onlineCount;
    }
}
