package com.gzzm.portal.cms.stat;

import com.gzzm.platform.visit.*;
import net.cyan.nest.annotation.*;

/**
 * 访问配置
 * @see Visit
 * @author sjy
 * @date 2017/5/25
 */
@Injectable(singleton = true)
public class StationVisitConfig
{
    private Integer visitId;

    private Integer stationId;

    public StationVisitConfig()
    {
    }

    public Integer getStationId()
    {
        return stationId;
    }

    public void setStationId(Integer stationId)
    {
        this.stationId = stationId;
    }

    public Integer getVisitId()
    {
        return visitId;
    }

    public void setVisitId(Integer visitId)
    {
        this.visitId = visitId;
    }
}
