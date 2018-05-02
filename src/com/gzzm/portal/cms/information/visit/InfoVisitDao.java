package com.gzzm.portal.cms.information.visit;

import net.cyan.commons.transaction.*;
import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.*;

import java.sql.*;


/**
 * @author sjy
 * @date 2018/3/2
 */
public abstract class InfoVisitDao extends GeneralDao
{

    @OQL("select count(t) from com.gzzm.portal.cms.information.visit.InfoVisitRecord t where t.informationId=:1 and t.ip=:2 and t.visitTime>=:3 and t.visitTime<:4")
    public abstract Long queryInfoVisitRecord(Long infoId, String ip, Date visitDay, Date visitDay1) throws Exception;

    @OQL("select sum(t.visitCount) from InfoVisitTotal t where t.informationId=:1")
    public abstract Long countInfoVisitTotal(Long infoId) throws Exception;

    @OQL("select sum(t.clickCount) from InfoVisitTotal t where t.informationId=:1")
    public abstract Long countInfoClickTotal(Long infoId) throws Exception;

    @Transactional
    public void saveInfoVisit(InfoVisitRecord record, InfoVisitTotal visitTotal) throws Exception
    {
        save(record);
        save(visitTotal);
    }
}
