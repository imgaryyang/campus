package com.gzzm.platform.login;

import net.cyan.commons.cache.Cache;
import net.cyan.commons.util.Filter;
import net.cyan.nest.annotation.*;

import java.util.*;

/**
 * 在线用户列表
 *
 * @author camel
 * @date 2009-7-24
 */
@Injectable(singleton = true)
public class UserOnlineList
{
    @Inject("userOnline")
    private Cache<String, UserOnlineInfo> userOnlineInfos;

    /**
     * 映射用户和所有在线信息
     */
    @Inject("userOnline_ids")
    private Cache<Integer, Set<String>> userOnlineIds;

    public UserOnlineList()
    {
    }

    public void put(UserOnlineInfo userOnlineInfo)
    {
        userOnlineInfos.update(userOnlineInfo.getId(), userOnlineInfo);

        Integer userId = userOnlineInfo.getUserId();
        userOnlineIds.getSet(userId).add(userOnlineInfo.getId());
    }

    public void remove(UserOnlineInfo userOnlineInfo)
    {
        String id = userOnlineInfo.getId();
        Integer userId = userOnlineInfo.getUserId();

        userOnlineInfos.remove(id);
        userOnlineIds.getSet(userId).remove(id);
    }

    public UserOnlineInfo get(String id)
    {
        return userOnlineInfos.getCache(id);
    }

    public Set<String> getIds(Integer userId)
    {
        return userOnlineIds.getSet(userId);
    }

    /**
     * 查询在线用户
     *
     * @param filter 过滤器，过滤满足条件的数据
     * @return 查询结果
     * @throws Exception 允许filter抛出异常
     */
    public List<UserOnlineInfo> query(Filter<UserOnlineInfo> filter) throws Exception
    {
        List<UserOnlineInfo> result = new ArrayList<UserOnlineInfo>();

        for (String id : userOnlineInfos.getKeys())
        {
            UserOnlineInfo userOnlineInfo = userOnlineInfos.getCache(id);

            if (userOnlineInfo != null && filter.accept(userOnlineInfo))
                result.add(userOnlineInfo);
        }

        return result;
    }

    /**
     * 获得某用户的在线信息
     *
     * @param userId 用户id
     * @return 用户在线信息
     */
    public UserOnlineInfo getUserOnlineInfoByUserId(Integer userId)
    {
        if (userId == null)
            return null;

        Set<String> ids = getIds(userId);
        if (ids != null)
        {
            for (String id : ids)
            {
                UserOnlineInfo userOnlineInfo = userOnlineInfos.getCache(id);
                if (userOnlineInfo != null && userOnlineInfo.getUserId().equals(userId))
                    return userOnlineInfo;
            }
        }

        return null;
    }

    /**
     * 判断某用户是否在线
     *
     * @param userId 用户id
     * @return 用户在线返回true，用户不在线返回false
     */
    public boolean isOnline(Integer userId)
    {
        return getUserOnlineInfoByUserId(userId) != null;
    }

    public int size()
    {
        return userOnlineInfos.getKeys().size();
    }
}
