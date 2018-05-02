package com.gzzm.platform.timeout;

import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.*;

/**
 * @author camel
 * @date 12-8-17
 */
public abstract class TimeoutDao extends GeneralDao
{
    public TimeoutDao()
    {
    }

    @OQL("select l from TimeoutLevel l order by orderId")
    public abstract List<TimeoutLevel> getLevelLists() throws Exception;

    @LoadByKey
    public abstract TimeoutLevel getTimeoutLevel(Integer levelId) throws Exception;

    @OQL("select c from TimeoutConfig c")
    public abstract List<TimeoutConfig> getAllTimeoutConfigs() throws Exception;

    @GetByField({"typeId", "recordId", "levelId", "startTime"})
    public abstract Timeout getTimeout(String typeId, Long recordId, Integer levelId, Date startTime)
            throws Exception;
}
