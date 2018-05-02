package com.gzzm.platform.commons;

import net.cyan.thunwind.annotation.Entity;

import java.util.Date;

/**
 * 记录系统一些数据的更新时间
 * @author camel
 * @date 2009-7-20
 */
@Entity(table = "PFUPDATETIME", keys = "name")
public class UpdateTime
{
    /**
     * 更新的数据的名称
     */
    private String name;

    /**
     * 最后更新的时间
     */
    private Date lastTime;

    public UpdateTime()
    {
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Date getLastTime()
    {
        return lastTime;
    }

    public void setLastTime(Date lastTime)
    {
        this.lastTime = lastTime;
    }

    public boolean equals(Object o)
    {
        return this == o || o instanceof UpdateTime && name.equals(((UpdateTime) o).name);
    }

    public int hashCode()
    {
        return name.hashCode();
    }
}
