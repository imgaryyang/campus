package com.gzzm.platform.timeout;

import com.gzzm.platform.organ.Dept;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * 超时记录备份，当超时被修正后，将原来的超时记录备份，以备查询
 *
 * @author camel
 * @date 14-4-1
 */
@Entity(table = "PFTIMEOUTBAK", keys = "timeoutId")
public class TimeoutBak
{
    @ColumnDescription(type = "number(11)")
    private Long timeoutId;

    @ColumnDescription(type = "varchar(250)")
    private String typeId;

    /**
     * 业务记录ID，由业务表决定，例如审批是审批记录ID，咨询投诉是咨询投诉处理过程ID
     */
    @ColumnDescription(type = "number(18)")
    private Long recordId;

    /**
     * 开始计时时间
     */
    private Date startTime;

    /**
     * 截止时间
     */
    private Date deadline;

    /**
     * 超时时间
     */
    private Date timeoutTime;

    /**
     * 超时级别
     */
    private Integer levelId;

    @NotSerialized
    private TimeoutLevel level;

    /**
     * 超时的天数，记录超时时系统配置的达到此级别的警告时的天数，当系统配置改变时能保留记录
     */
    private Integer day;

    @ColumnDescription(nullable = false, defaultValue = "0")
    private TimeUnit unit;

    /**
     * 是否有效，每个记录只有一个有效超时
     */
    private Boolean valid;

    /**
     * 超时的部门
     */
    private Integer deptId;

    /**
     * 超时的部门
     */
    @NotSerialized
    private Dept dept;

    /**
     * 记录时间
     */
    private Date recordTime;

    /**
     * 备份时间
     */
    private Date bakTime;

    public TimeoutBak()
    {
    }

    public Long getTimeoutId()
    {
        return timeoutId;
    }

    public void setTimeoutId(Long timeoutId)
    {
        this.timeoutId = timeoutId;
    }

    public String getTypeId()
    {
        return typeId;
    }

    public void setTypeId(String typeId)
    {
        this.typeId = typeId;
    }

    public Long getRecordId()
    {
        return recordId;
    }

    public void setRecordId(Long recordId)
    {
        this.recordId = recordId;
    }

    public Date getStartTime()
    {
        return startTime;
    }

    public void setStartTime(Date startTime)
    {
        this.startTime = startTime;
    }

    public Date getDeadline()
    {
        return deadline;
    }

    public void setDeadline(Date deadline)
    {
        this.deadline = deadline;
    }

    public Date getTimeoutTime()
    {
        return timeoutTime;
    }

    public void setTimeoutTime(Date timeoutTime)
    {
        this.timeoutTime = timeoutTime;
    }

    public Integer getLevelId()
    {
        return levelId;
    }

    public void setLevelId(Integer levelId)
    {
        this.levelId = levelId;
    }

    public TimeoutLevel getLevel()
    {
        return level;
    }

    public void setLevel(TimeoutLevel level)
    {
        this.level = level;
    }

    public Integer getDay()
    {
        return day;
    }

    public void setDay(Integer day)
    {
        this.day = day;
    }

    public TimeUnit getUnit()
    {
        return unit;
    }

    public void setUnit(TimeUnit unit)
    {
        this.unit = unit;
    }

    public Boolean getValid()
    {
        return valid;
    }

    public void setValid(Boolean valid)
    {
        this.valid = valid;
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

    public Date getRecordTime()
    {
        return recordTime;
    }

    public void setRecordTime(Date recordTime)
    {
        this.recordTime = recordTime;
    }

    public Date getBakTime()
    {
        return bakTime;
    }

    public void setBakTime(Date bakTime)
    {
        this.bakTime = bakTime;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof TimeoutBak))
            return false;

        TimeoutBak timeout = (TimeoutBak) o;

        return timeoutId.equals(timeout.timeoutId);
    }

    @Override
    public int hashCode()
    {
        return timeoutId.hashCode();
    }
}