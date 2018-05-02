package com.gzzm.safecampus.device.card.entity;

import net.cyan.commons.validate.annotation.*;
import net.cyan.thunwind.annotation.*;

/**
 * @author liyabin
 * @date 2018/3/26
 */
@Entity(table = "SCDEVICEHJOB", keys = "id")
public class Job
{
    @Generatable(length = 8)
    private Integer id;
    @ColumnDescription(type = "VARCHAR2(8)")
    private String jobId;
    @ColumnDescription(type = "VARCHAR2(50)")
    private String jobName;
    @ColumnDescription(type = "NUMBER(3)")
    @MaxVal(value = "255")
    @MinVal(value = "0")
    private Integer privillege;

    public Job()
    {
    }

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getJobId()
    {
        return jobId;
    }

    public void setJobId(String jobId)
    {
        this.jobId = jobId;
    }

    public String getJobName()
    {
        return jobName;
    }

    public void setJobName(String jobName)
    {
        this.jobName = jobName;
    }

    public Integer getPrivillege()
    {
        return privillege;
    }

    public void setPrivillege(Integer privillege)
    {
        this.privillege = privillege;
    }
}
