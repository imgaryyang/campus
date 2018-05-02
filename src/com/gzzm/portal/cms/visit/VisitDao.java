package com.gzzm.portal.cms.visit;

import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.*;

import java.util.*;

/**
 * @author sjy
 * @date 2018/3/21
 */
public abstract class VisitDao extends GeneralDao
{

    @OQL("select t from com.gzzm.portal.cms.visit.VisitDayTotal t order by t.visitDay desc limit 1")
    public abstract VisitDayTotal getCurrentVisitDayTotal() throws Exception;

    @OQL("select t.visitTime from com.gzzm.platform.visit.VisitRecord t where t.visitTime is not null order by t.visitTime")
    public abstract java.sql.Date queryMinVisitTime() throws Exception;

    @OQL("select t.type as type , t.objectId as objectId ,count(t.visitId) as clickCount,count(distinct ip) as visitCount from com.gzzm.platform.visit.VisitRecord t where t.visitTime >=:1 and t.visitTime <:2 group by t.type , t.objectId")
    public abstract List<VisitDayTotalService.CountVisitBean> countVisitByDay(java.sql.Date visitDay,java.sql.Date nextVisitDay) throws Exception;

    @OQL("select t.channelId from com.gzzm.portal.cms.channel.Channel t where t.channelCode=:1 and t.deleteTag=0")
    public abstract Integer getChannelId(String channelCode) throws Exception;
}
