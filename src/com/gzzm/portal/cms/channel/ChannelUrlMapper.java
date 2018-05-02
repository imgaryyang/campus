package com.gzzm.portal.cms.channel;

import com.gzzm.platform.annotation.CacheInstance;
import com.gzzm.portal.cms.station.Station;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 栏目url映射，缓存所有定义了url的栏目
 * 保存url和页面id直接的关系，urlwriter可以读取此缓存，决定哪个url跳转到哪个栏目
 *
 * @author camel
 * @date 2011-6-9
 */
@CacheInstance("cms.channel")
public class ChannelUrlMapper
{
    @Inject
    private static Provider<ChannelDao> daoProvider;

    /**
     * 保存url和栏目id的关系，key为页面url，value为栏目ID
     */
    private Map<String, Integer> map = new HashMap<String, Integer>();

    private ChannelUrlMapper() throws Exception
    {
        load();
    }

    private void load() throws Exception
    {
        ChannelDao dao = daoProvider.get();
        for (Channel channel : dao.getChannelsForUrlMap())
        {
            String url = channel.getUrl();

            Integer stationId = channel.getStationId();
            if (stationId != null)
            {
                Station station = dao.getStation(stationId);

                String domain = station.getDomainName();
                if (!StringUtils.isEmpty(domain))
                    map.put(domain + url, channel.getChannelId());
            }

            map.put(url, channel.getChannelId());
        }
    }

    public Integer getChannelId(String url)
    {
        return map.get(url);
    }
}
