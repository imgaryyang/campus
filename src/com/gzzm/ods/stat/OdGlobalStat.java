package com.gzzm.ods.stat;

import com.gzzm.platform.commons.crud.DeptTreeStat;
import net.cyan.crud.annotation.*;

import java.sql.Date;

/**
 * @author camel
 * @date 12-12-12
 */
public abstract class OdGlobalStat extends DeptTreeStat
{
    @Lower
    protected Date time_start;

    @Upper
    protected Date time_end;

    public OdGlobalStat()
    {
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
