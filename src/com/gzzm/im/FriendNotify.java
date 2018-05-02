package com.gzzm.im;

import java.io.Serializable;
import java.util.List;

/**
 * 好友通知
 *
 * @author camel
 * @date 2011-2-8
 */
public class FriendNotify implements Serializable
{
    private static final long serialVersionUID = -7831075989381438978L;

    private Integer userId;

    //消息接收者姓名
    private String userName;

    private FriendNotifyType type;

    private List<Integer> groupIds;

    public FriendNotify()
    {
    }

    public FriendNotify(Integer userId, String userName, FriendNotifyType type)
    {
        this.userId = userId;
        this.userName = userName;
        this.type = type;
    }

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public FriendNotifyType getType()
    {
        return type;
    }

    public void setType(FriendNotifyType type)
    {
        this.type = type;
    }

    public List<Integer> getGroupIds()
    {
        return groupIds;
    }

    public void setGroupIds(List<Integer> groupIds)
    {
        this.groupIds = groupIds;
    }
}
