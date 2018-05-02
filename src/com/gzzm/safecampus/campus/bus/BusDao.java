package com.gzzm.safecampus.campus.bus;

import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.dao.GeneralDao;

import java.sql.Date;
import java.util.List;

/**
 * @author Neo
 * @date 2018/3/23 8:44
 */
public abstract class BusDao extends GeneralDao
{
    public BusDao()
    {
    }

    /**
     * 获取所有的校巴
     *
     * @return 校巴列表
     * @throws Exception 操作异常
     */
    @OQL("select s from BusInfo s where s.deptId=:1 and (s.deleteTag=0 or s.deleteTag is null) order by s.orderId")
    public abstract List<BusInfo> getBusInfos(Integer deptId) throws Exception;

    /**
     * 获取校巴路线所有的站点
     *
     * @param busId 校巴Id
     * @return 校巴站点列表
     * @throws Exception 操作异常
     */
    @OQL("select s from BusSite s where (select 1 from BusInfo b where b.routeId=s.routeId and b.busId=:1) is not empty order by s.orderId")
    public abstract List<BusSite> getBusSiteByBusId(Integer busId) throws Exception;

    /**
     * 根据校巴名称和站点名称查询站点信息
     * @param busName 校巴名称
     * @param siteName 站点名称
     * @param deptId 部门Id
     * @return 站点信息
     * @throws Exception 操作异常
     */
    @OQL("select s from BusSite s where (select 1 from BusInfo b where b.routeId=s.routeId and b.busName=:1 and b.deptId=:3) is not empty and s.siteName=:2")
    public abstract BusSite getBusSite(String busName, String siteName, Integer deptId) throws Exception;

    /**
     * 获取校巴信息
     * @param busName 校巴名称
     * @param deptId 部门Id
     * @return 校巴信息
     */
    @OQL("select s from BusInfo s where busName=:1 and deptId=:2")
    public abstract BusInfo getBusIdByBusName(String busName, Integer deptId);

    /**
     *
     * @param busId
     * @return
     */
    @OQL("select b.teacherId from BusSchedule b where b.busId=?1 and b.scheduleTime=?2 and b.scheduleType=?3 limit 0,1")
    public abstract Integer getTeacherIdByBusId(Integer busId ,Date date,ScheduleType scheduleType);
}
