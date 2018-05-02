package com.gzzm.portal.cms.station;

import com.gzzm.portal.annotation.Tag;
import com.gzzm.portal.tag.PortalTag;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 站点列表标签
 *
 * @author camel
 * @date 2011-6-16
 */
@Tag(name = "station")
public class StationListTag implements PortalTag
{
    @Inject
    private StationDao dao;

    private String type;

    public StationListTag()
    {
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public Object getValue(Map<String, Object> context) throws Exception
    {
        List<Station> stations = dao.getStations(type);

        List<StationInfo> result = new ArrayList<StationInfo>(stations.size());

        for (Station station : stations)
        {
            result.add(new StationInfo(station));
        }

        return result;
    }
}
