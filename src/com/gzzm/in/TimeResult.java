package com.gzzm.in;

import java.util.Date;

/**
 * @author camel
 * @date 2017/7/10
 */
public class TimeResult extends InterfaceResult
{
    private Date time;

    public TimeResult()
    {
    }

    public Date getTime()
    {
        return time;
    }

    public void setTime(Date time)
    {
        this.time = time;
    }
}
