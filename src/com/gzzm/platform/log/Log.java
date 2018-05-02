package com.gzzm.platform.log;

import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * 日志类型的实体对象
 *
 * @author camel
 * @date 2009-7-30
 */
public class Log
{
    /**
     * 日志id，主键，由uuid生成
     */
    @Generatable(name = "uuid", length = 32)
    protected String logId;

    /**
     * 日志时间
     */
    @Index
    private Date logTime;

    public String getLogId()
    {
        return logId;
    }

    public void setLogId(String logId)
    {
        this.logId = logId;
    }

    public Date getLogTime()
    {
        return logTime;
    }

    public void setLogTime(Date logTime)
    {
        this.logTime = logTime;
    }

    public int hashCode()
    {
        return logId.hashCode();
    }
}