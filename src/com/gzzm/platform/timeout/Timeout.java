package com.gzzm.platform.timeout;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.organ.Dept;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;
import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * 超时记录
 *
 * @author camel
 * @date 14-4-1
 */
@Entity(table = "PFTIMEOUT", keys = "timeoutId")
@Indexes({
        @Index(name = "IX_PFTIMEOUT", columns = {"TYPEID", "RECORDID", "LEVELID", "VALID", "TIMEOUTTIME"}),
        @Index(columns = {"DEPTID", "TYPEID"})
})

public class Timeout
{
    @Inject
    private static Provider<TimeoutService> serviceProvider;

    @Generatable(length = 11)
    private Long timeoutId;

    @ColumnDescription(type = "varchar(250)", nullable = false)
    private String typeId;

    /**
     * 业务记录ID，由业务表决定，例如审批是审批记录ID，咨询投诉是咨询投诉处理过程ID
     */
    @ColumnDescription(type = "number(18)", nullable = false)
    private Long recordId;

    /**
     * 开始计时时间
     */
    @ColumnDescription(nullable = false)
    private Date startTime;

    /**
     * 截止时间
     */
    @ColumnDescription(nullable = false)
    private Date deadline;

    /**
     * 超时时间
     */
    @ColumnDescription(nullable = false)
    private Date timeoutTime;

    /**
     * 超时的结束时间
     */
    private Date endTime;

    /**
     * 超时级别
     */
    @ColumnDescription(nullable = false)
    private Integer levelId;

    @NotSerialized
    private TimeoutLevel level;

    /**
     * 超时的天数，记录超时时系统配置的达到此级别的警告时的天数，当系统配置改变时能保留记录
     */
    @ColumnDescription(nullable = false)
    private Integer day;

    @ColumnDescription(nullable = false, defaultValue = "0")
    private TimeUnit unit;

    /**
     * 是否有效，每个记录只有一个有效超时
     */
    @ColumnDescription(nullable = false, defaultValue = "1")
    private Boolean valid;

    /**
     * 超时的部门
     */
    @ColumnDescription(nullable = false)
    private Integer deptId;

    /**
     * 超时的部门
     */
    @NotSerialized
    private Dept dept;

    /**
     * 记录时间
     */
    @ColumnDescription(nullable = false)
    private Date recordTime;

    public Timeout()
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

    public Date getEndTime()
    {
        return endTime;
    }

    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
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

    @NotSerialized
    public String getTypeName() throws Exception
    {
        TimeoutType type = serviceProvider.get().getTimeoutType(typeId);
        if (type != null)
            return type.getTypeName();

        return null;
    }

    @NotSerialized
    public String getSimpleTypeName() throws Exception
    {
        TimeoutType type = serviceProvider.get().getTimeoutType(typeId);
        if (type != null)
            return type.getSimpleName();

        return null;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof Timeout))
            return false;

        Timeout timeout = (Timeout) o;

        return timeoutId.equals(timeout.timeoutId);
    }

    public boolean equals1(Timeout timeout)
    {
        return DataConvert.equal(startTime, timeout.startTime) &&
                DataConvert.equal(deadline, timeout.deadline) &&
                DataConvert.equal(timeoutTime, timeout.timeoutTime) &&
                DataConvert.equal(endTime, timeout.endTime) &&
                DataConvert.equal(levelId, timeout.levelId) &&
                DataConvert.equal(day, timeout.day) &&
                DataConvert.equal(unit, timeout.unit) &&
                DataConvert.equal(valid, timeout.valid) &&
                DataConvert.equal(deptId, timeout.deptId);
    }

    @Override
    public int hashCode()
    {
        return timeoutId.hashCode();
    }

    @Override
    public String toString()
    {
        try
        {
            TimeoutType type = serviceProvider.get().getTimeoutType(typeId);

            return type.getSimpleName() + getLevel().getLevelName();
        }
        catch (Exception ex)
        {
            Tools.wrapException(ex);

            return null;
        }
    }
}