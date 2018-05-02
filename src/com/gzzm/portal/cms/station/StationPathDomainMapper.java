package com.gzzm.portal.cms.station;

import com.gzzm.platform.annotation.CacheInstance;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 网站域名映射，缓存所有网站的域名和繁体域名，urlwriter和gbkfilter可以读取此缓存信息，决定转到哪个网站
 *
 * @author camel
 * @date 2011-6-9
 */
@CacheInstance("cms.station")
public class StationPathDomainMapper
{
    @Inject
    private static Provider<StationDao> daoProvider;

    /**
     * 保存站点路径和站点ID的关系，key为站点路径，value为站点ID
     */
    private Map<String, Integer> pathMap = new HashMap<String, Integer>();

    /**
     * 保存站点简体域名和站点ID的关系，key为站点简体域名，value为站点ID
     */
    private Map<String, Integer> domainMap = new HashMap<String, Integer>();

    /**
     * 保存繁体域名和简体域名直接的关系，key为繁体域名，value为简体域名
     */
    private Map<String, String> gb5DomainMap = new HashMap<String, String>();

    private StationPathDomainMapper() throws Exception
    {
        load();
    }

    private void load() throws Exception
    {
        for (Station station : daoProvider.get().getAllStations())
        {
            pathMap.put(station.getPath(), station.getStationId());

            String domain = station.getDomainName();
            String gb5Domain = station.getGb5Domain();

            if (!StringUtils.isEmpty(domain))
            {
                String domain0 = null;
                for (String s : domain.split(","))
                {
                    s = s.trim();
                    if (domain0 == null)
                        domain0 = s;
                    domainMap.put(s, station.getStationId());
                }

                if (!StringUtils.isEmpty(gb5Domain))
                {
                    for (String s : gb5Domain.split(","))
                    {
                        gb5DomainMap.put(s, domain0);
                    }
                }
            }
        }
    }

    public Integer getStationIdByPath(String path)
    {
        return pathMap.get(path);
    }

    public String getDomainByGb5Domain(String gb5Domain)
    {
        return gb5DomainMap.get(gb5Domain);
    }

    public Integer getStationIdByDomain(String domain)
    {
        return domainMap.get(domain);
    }
}
