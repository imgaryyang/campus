package com.gzzm.ods.stat;

import com.gzzm.platform.commons.crud.DeptOwnedStat;
import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.*;
import net.cyan.crud.annotation.*;
import net.cyan.crud.util.*;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;
import java.util.*;

/**
 * 公文相关统计的超类
 *
 * @author camel
 * @date 12-2-23
 */
@Service
public abstract class OdStat<E> extends DeptOwnedStat<E>
{
    @Inject
    protected OdStatDao dao;

    @Lower
    protected Date time_start;

    @Upper
    protected Date time_end;

    @Inject
    protected DeptService deptService;

    public OdStat()
    {
    }

    @NotCondition
    @Override
    public Integer getDeptId()
    {
        return super.getDeptId();
    }

    @NotCondition
    @Override
    public Collection<Integer> getQueryDeptIds()
    {
        return super.getQueryDeptIds();
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

    protected java.util.Date getDefaultMonthStartTime() throws Exception
    {
        return DateUtils.getYearStart(new java.util.Date());
    }

    protected abstract java.util.Date getDefaultYearStartTime() throws Exception;

    @NotSerialized
    public List<Month> getMonths() throws Exception
    {
        java.util.Date startTime = time_start;
        java.util.Date endTime = time_end;

        if (startTime == null || Null.isNull(startTime))
        {
            startTime = new Date(getDefaultMonthStartTime().getTime());
        }

        if (endTime == null || Null.isNull(endTime))
            endTime = new java.util.Date();

        return Month.getMonthList(startTime, endTime, "yyyy年MMM", "M月");
    }

    @NotSerialized
    public List<Year> getYears() throws Exception
    {
        java.util.Date startTime = time_start;
        java.util.Date endTime = time_end;

        if (startTime == null || Null.isNull(startTime))
        {
            java.util.Date defaultYearStartTime = getDefaultYearStartTime();
            if (defaultYearStartTime == null)
                defaultYearStartTime = DateUtils.getYearStart(new java.util.Date());
            startTime = new Date(defaultYearStartTime.getTime());
        }

        if (endTime == null || Null.isNull(endTime))
            endTime = new java.util.Date();

        return Year.getYearList(startTime, endTime, "yyyy年", "yy");
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();

        setDefaultDeptId();
    }

    @NotSerialized
    @Select(field = "deptId")
    public List<DeptInfo> getDepts() throws Exception
    {
        Collection<Integer> authDeptIds = getAuthDeptIds();
        if (authDeptIds != null && authDeptIds.size() <= 3)
        {
            return deptService.getDepts(authDeptIds);
        }

        return null;
    }
}
