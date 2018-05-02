package com.gzzm.portal.cms.commons;

import com.gzzm.portal.cms.station.StationPathDomainMapper;
import net.cyan.arachne.exts.Gb5DomainMap;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

/**
 * 站点繁体域名的支持
 *
 * @author camel
 * @date 2011-6-9
 */
public class CmsGb5DomainMap implements Gb5DomainMap
{
    @Inject
    private static Provider<StationPathDomainMapper> stationDomainMapperProvider;

    public CmsGb5DomainMap()
    {
    }

    public String getGbkDomain(String gb5Domain)
    {
        return stationDomainMapperProvider.get().getDomainByGb5Domain(gb5Domain);
    }
}
