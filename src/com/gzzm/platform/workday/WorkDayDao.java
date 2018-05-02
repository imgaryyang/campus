package com.gzzm.platform.workday;

import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.GeneralDao;

import java.sql.Date;
import java.util.List;

/**
 * 工作日相关的一些数据库查询
 *
 * @author camel
 * @date 2010-5-26
 */
public abstract class WorkDayDao extends GeneralDao
{
    public WorkDayDao()
    {
    }

    @OQL("select w from WorkDay w order by workDayDate")
    public abstract List<WorkDay> getAllWorkDays() throws Exception;

    @GetByField("workDayDate")
    public abstract WorkDay getWorkDay(Date date) throws Exception;
}
