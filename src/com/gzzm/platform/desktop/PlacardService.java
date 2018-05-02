package com.gzzm.platform.desktop;

import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 系统公告相关的一些服务方法
 *
 * @author camel
 * @date 2010-6-3
 */
public class PlacardService
{
    @Inject
    private PlacardDao dao;

    public PlacardService()
    {
    }

    @SuppressWarnings("unchecked")
    public List<String> getPlacards(Integer userId, Collection<Integer> deptIds) throws Exception
    {
        List<String> placards =
                deptIds == null || deptIds.size() > 0 ? dao.getPlacards(deptIds) : Collections.EMPTY_LIST;
        List<String> userPlacards = dao.getUserPlacards(userId);

        List<String> result = new ArrayList<String>(placards.size() + userPlacards.size());
        result.addAll(placards);
        result.addAll(userPlacards);

        return result;
    }
}
