package com.gzzm.portal.interview.dao;

import com.gzzm.portal.cms.station.Station;
import com.gzzm.portal.interview.entity.InterviewType;
import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.List;

/**
 * @author lk
 * @date 13-9-29
 */
public abstract class InterviewDao extends GeneralDao {

    @OQL("select t from InterviewType t order by orderId")
    public abstract List<InterviewType> getAllTypes();

    @OQL("select t.station.stationName from InterviewType t where t.typeId =:1")
    public abstract String getStationName(Integer typeId);

    @OQL("select t.station from InterviewType t where t.typeId =:1")
    public abstract Station getStation(Integer typeId);
}
