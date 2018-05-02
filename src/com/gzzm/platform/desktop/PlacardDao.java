package com.gzzm.platform.desktop;

import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.*;

/**
 * 系统公告相关的Dao
 *
 * @author camel
 * @date 2010-6-2
 */
public abstract class PlacardDao extends GeneralDao
{
    public PlacardDao()
    {
    }

    @OQL("select content from Placard where deptId in ?1 and valid<>0 " +
            "order by dept.leftValue,orderId,createTime desc")
    public abstract List<String> getPlacards(Collection<Integer> deptIds) throws Exception;

    @OQL("select content from UserPlacard where userId=:1 and timeout<sysdate() order by sendTime desc")
    public abstract List<String> getUserPlacards(Integer userId) throws Exception;
}