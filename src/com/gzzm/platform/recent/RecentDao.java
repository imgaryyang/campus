package com.gzzm.platform.recent;

import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.*;

/**
 * @author camel
 * @date 2016/1/13
 */
public abstract class RecentDao extends GeneralDao
{
    public static final int RECENT_COUNT = 10;

    public RecentDao()
    {
    }

    public void saveRecent(Recent recent) throws Exception
    {
        Recent recent1 = getRecent(recent.getUserId(), recent.getUrl());

        if (recent1 != null)
        {
            recent1.setRecentName(recent.getRecentName());
            recent1.setType(recent.getType());
            recent1.setTarget(recent.getTarget());
            recent1.setShowTime(recent.getShowTime());
            recent = recent1;
        }

        recent.setLastTime(new Date());
        if (recent1 != null)
        {
            update(recent1);
        }
        else
        {
            add(recent);
        }

        clearRecents(recent.getUserId(), recent.getType());
    }

    public void clearRecents(Integer userId, String type) throws Exception
    {
        List<Recent> recents = getLastRecents(userId, type);

        if (recents.size() >= RECENT_COUNT)
        {
            Recent recent = recents.get(RECENT_COUNT - 1);

            deleteRecents(userId, type, recent.getLastTime());
        }
    }

    @GetByField({"userId", "url"})
    public abstract Recent getRecent(Integer userId, String url) throws Exception;

    @OQL("select r from Recent r where userId=:1 and type=:2 order by lastTime desc limit " + RECENT_COUNT)
    protected abstract List<Recent> getLastRecents(Integer userId, String type) throws Exception;

    @OQLUpdate("delete from Recent where userId=:1 and type=:2 and lastTime<:3")
    protected abstract void deleteRecents(Integer userId, String type, Date lastTime) throws Exception;
}
