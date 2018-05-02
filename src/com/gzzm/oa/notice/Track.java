package com.gzzm.oa.notice;

import com.gzzm.platform.organ.User;

import java.util.Date;

/**
 * 跟踪信息显示类，只在显示跟踪信息时有用
 *
 * @author czf
 * @date 2010-3-30
 */
public class Track
{
    /**
     * 阅读的用户
     */
    private User user;

    /**
     * 阅读时间，如果用户未阅读，则为null
     */
    private Date readTime;

    public Track(User user)
    {
        this.user = user;
    }

    public User getUser()
    {
        return user;
    }

    public Date getReadTime()
    {
        return readTime;
    }

    void setReadTime(Date readTime)
    {
        this.readTime = readTime;
    }
}
