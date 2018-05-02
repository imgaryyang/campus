package com.gzzm.portal.weather;

import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.*;

import java.sql.Date;

/**
 * @author lk
 * @date 13-9-25
 */
@Entity(table = "PLWEATHER", keys = "weatherId")
public class Weather
{
    @Generatable(length = 9)
    private Integer weatherId;

    @Require
    @ColumnDescription(type = "number(7)", nullable = false)
    private Integer deptId;

    @NotSerialized
    private Dept dept;

    @Require
    @ColumnDescription(type = "varchar(4000)", nullable = false)
    private String content;

    @Require
    private java.sql.Date weatherDate;

    private Integer creatorId;


    @ToOne("CREATORID")
    @NotSerialized
    private User creator;

    private java.util.Date createTime;

    public Weather()
    {
    }

    public Integer getWeatherId()
    {
        return weatherId;
    }

    public void setWeatherId(Integer weatherId)
    {
        this.weatherId = weatherId;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public Dept getDept()
    {
        return dept;
    }

    public void setDept(Dept dept)
    {
        this.dept = dept;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public Date getWeatherDate()
    {
        return weatherDate;
    }

    public void setWeatherDate(Date weatherDate)
    {
        this.weatherDate = weatherDate;
    }

    public Integer getCreatorId()
    {
        return creatorId;
    }

    public void setCreatorId(Integer creatorId)
    {
        this.creatorId = creatorId;
    }

    public User getCreator()
    {
        return creator;
    }

    public void setCreator(User creator)
    {
        this.creator = creator;
    }

    public java.util.Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(java.util.Date createTime)
    {
        this.createTime = createTime;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof Weather))
            return false;

        Weather weather = (Weather) o;

        return weatherId.equals(weather.weatherId);
    }

    @Override
    public int hashCode()
    {
        return weatherId.hashCode();
    }

    @Override
    public String toString()
    {
        return content;
    }
}
