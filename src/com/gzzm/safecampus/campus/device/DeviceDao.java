package com.gzzm.safecampus.campus.device;

import com.gzzm.safecampus.campus.bus.BusRoute;
import com.gzzm.safecampus.campus.bus.BusSite;
import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.annotation.OQLUpdate;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.List;

/**
 * 硬件Dao
 *
 * @author yiuman
 * @date 2018/3/14
 */
public abstract class DeviceDao extends GeneralDao {

    @OQLUpdate("update DeviceCard  set status = ?2 where studentId in ?1")
    public abstract void changeStatus(Integer[] keys, Integer state);

    @OQL("select g from BusRoute g  where g.deptId = ?1")
    public abstract List<BusRoute> getRouteBydeptIds(Integer deptId);

    @OQL("select c from BusSite c where c.siteName like ?1")
    public abstract List<BusSite> getBusRoutesByName(String s);

    @OQL("select c.route from BusSite c where c.siteId = ?1")
    public abstract BusRoute getBusRouteBySiteId(Integer integer);

    @OQL("select c from BusSite c where c.routeId = ?1")
    public abstract List<BusSite> getBusSiteByGra(Integer integer);
}
