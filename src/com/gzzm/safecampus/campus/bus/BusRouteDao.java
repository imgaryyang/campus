package com.gzzm.safecampus.campus.bus;

import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.List;
import java.util.Map;

/**
 * @author czy
 */
public abstract class BusRouteDao extends GeneralDao{
    public BusRouteDao(){}

    @OQL("select b.routeId as Id,b.routeName as name from BusRoute b where b.deptId = :1")
    public abstract List<Map<String,Object>> getBusRoutes(Integer deptId);

    @OQL("select b.busId as Id,b.busName as name from BusInfo b where b.deptId = :1")
    public abstract List<Map<String,Object>> getBusInfos(Integer deptId);

    @OQL("select b from BusInfo b where b.routeId = :1")
    public abstract BusInfo getBusByRouteId(Integer routeId);
}
