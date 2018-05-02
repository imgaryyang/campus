package com.gzzm.platform.consignation;

import com.gzzm.platform.organ.User;
import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.*;

/**
 * 委托相关的数据操作
 *
 * @author camel
 * @date 2010-8-30
 */
public abstract class ConsignationDao extends GeneralDao
{
    public ConsignationDao()
    {
    }

    public User getUser(Integer userId) throws Exception
    {
        return load(User.class, userId);
    }

    /**
     * 获得某个时间有效的委托
     *
     * @param consigner 委托人
     * @param time      时间
     * @return 有效的委托，如果不存在有效的委托，返回null
     * @throws Exception 数据库查询错误
     */
    @OQL("select c from Consignation c where consigner=:1 and state=1 and startTime<=:2 and " +
            "addday(endTime,1)>:2 and (modules is empty or (exists m in modules : m in ?3)) order by startTime")
    public abstract Consignation getAvailableConsignation(Integer consigner, Date time, String[] modules)
            throws Exception;

    /**
     * 获得某段时间内的委托
     *
     * @param consigner  委托人
     * @param time_start 开始时间
     * @param time_end   结束时间
     * @return 委托列表，如果没有委托，返回空的List
     * @throws Exception 数据库查询错误
     */
    @OQL("select c from Consignation c where consigner=:1 and startTime<=:3 and endTime>=:2 " +
            "and (modules is empty or (exists m in modules : m in ?4)) order by startTime")
    public abstract List<Consignation> getConsignations(Integer consigner, Date time_start, Date time_end,
                                                        Collection<String> modules) throws Exception;
}
