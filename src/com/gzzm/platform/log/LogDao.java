package com.gzzm.platform.log;

import net.cyan.thunwind.dao.GeneralDao;

import java.util.Date;

/**
 * 日志的数据访问对象
 *
 * @author camel
 * @date 2009-7-18
 */
public abstract class LogDao extends GeneralDao
{
    public LogDao()
    {
    }

    public void log(Log log) throws Exception
    {
        log.setLogTime(new Date());
        add(log);
    }

    public void log(OperationLog log) throws Exception
    {
        log((Log)log);

        save(new OperationLogType(log.getType()));
    }
}
