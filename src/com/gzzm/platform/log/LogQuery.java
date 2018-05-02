package com.gzzm.platform.log;

import com.gzzm.platform.commons.crud.BaseQueryCrud;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.*;
import net.cyan.crud.annotation.*;

import java.sql.Date;

/**
 * 日志查询的基类
 *
 * @author camel
 * @date 2009-7-30
 */
@Service
public abstract class LogQuery<L extends Log> extends BaseQueryCrud<L, String>
{
    /**
     * 时间范围作查询条件
     */
    @Lower(column = "logTime")
    private java.sql.Date time_start;

    @Upper(column = "logTime")
    private java.sql.Date time_end;

    public LogQuery()
    {
        addOrderBy("logTime", OrderType.desc);
    }

    public Date getTime_start()
    {
        return time_start;
    }

    public void setTime_start(Date time_start)
    {
        this.time_start = time_start;
    }

    public Date getTime_end()
    {
        return time_end;
    }

    public void setTime_end(Date time_end)
    {
        this.time_end = time_end;
    }
}
