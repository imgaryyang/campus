package com.gzzm.safecampus.wx.user;

import net.cyan.commons.cache.Cache;
import net.cyan.nest.annotation.Inject;
import net.cyan.nest.annotation.Injectable;

import java.util.ArrayList;
import java.util.List;

/**
 * 微信在线用户列表
 *
 * @author Neo
 * @date 2018-04-03
 */
@Injectable(singleton = true)
public class WxUserOnlineList
{

    @Inject("wxuserOnline")
    private Cache<String, WxUserOnlineInfo> wxUserOnlineInfos;

    /**
     * 映射用户和所有在线信息
     */
    @Inject("wxuserOnline_ids")
    private Cache<Integer, List<String>> wxUserOnlineIds;

    public WxUserOnlineList()
    {
    }

    public void put(WxUserOnlineInfo wxUserOnlineInfo)
    {
        wxUserOnlineInfos.update(wxUserOnlineInfo.getId(), wxUserOnlineInfo);

        Integer userId = wxUserOnlineInfo.getUserId();
        List<String> ids = wxUserOnlineIds.getCache(userId);
        if (ids == null)
            ids = new ArrayList<>();

        ids.add(wxUserOnlineInfo.getId());
        wxUserOnlineIds.update(userId, ids);
    }

    public void remove(WxUserOnlineInfo wxUserOnlineInfo)
    {
        String id = wxUserOnlineInfo.getId();
        Integer userId = wxUserOnlineInfo.getUserId();

        wxUserOnlineInfos.remove(id);

        List<String> ids = wxUserOnlineIds.getCache(userId);
        if (ids == null)
            ids = new ArrayList<>();

        ids.remove(id);
        wxUserOnlineIds.update(userId, ids);
    }

    public WxUserOnlineInfo get(String id)
    {
        return wxUserOnlineInfos.getCache(id);
    }

    public List<String> getIds(Integer userId)
    {
        return wxUserOnlineIds.getCache(userId);
    }

    public int size()
    {
        return wxUserOnlineInfos.getKeys().size();
    }
}
