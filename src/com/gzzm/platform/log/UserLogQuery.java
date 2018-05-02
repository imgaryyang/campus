package com.gzzm.platform.log;

import com.gzzm.platform.commons.crud.DeptOwnedQuery;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;

import java.sql.Date;

/**
 * 用户日志查询
 *
 * @author camel
 * @date 2009-7-30
 */
@Service
public abstract class UserLogQuery<L extends UserLog> extends DeptOwnedQuery<L, String>
{
    /**
     * 时间范围作查询条件
     */
    @Lower(column = "logTime")
    private java.sql.Date time_start;

    @Upper(column = "logTime")
    private java.sql.Date time_end;

    /**
     * 用部门名称作查询条件
     */
    @Like("dept.deptName")
    private String deptName;

    /**
     * 用户名作查询条件
     */
    @Like("user.userName")
    private String userName;

    public UserLogQuery()
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

    public String getDeptName()
    {
        return deptName;
    }

    public void setDeptName(String deptName)
    {
        this.deptName = deptName;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }
}
