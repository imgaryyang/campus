package com.gzzm.portal.olconsult;

import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.List;

/**
 * User: wym
 * Date: 13-5-31
 * Time: 上午11:20
 * 咨询记录Dao
 */
public abstract class OlConsultDao extends GeneralDao
{
    public OlConsultDao()
    {
    }

    public OlConsult getConsult(Integer consultId) throws Exception
    {
        return load(OlConsult.class, consultId);
    }

    @OQL("select s from OlConsultSeat s where s.typeId=:1 order by orderId")
    public abstract List<OlConsultSeat> getSeats(Integer typeId) throws Exception;

    @GetByField({"userId", "typeId"})
    public abstract OlConsultSeat getSeatByUserId(Integer userId, Integer typeId) throws Exception;

    @OQL("select r from OlConsultRecord r where r.consultId=:1 order by chatTime,r.recordId")
    public abstract List<OlConsultRecord> getRecords(Integer consultId) throws Exception;
}
