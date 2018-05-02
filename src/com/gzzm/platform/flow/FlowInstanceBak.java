package com.gzzm.platform.flow;

import com.gzzm.platform.organ.User;
import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * @author camel
 * @date 2014/9/2
 */
@Entity(table = "PFFLOWINSTANCEBAK", keys = "instanceId")
public class FlowInstanceBak extends SystemFlowInstance
{
    /**
     * 当时删除这个流程实例使用的dao的java类
     */
    @ColumnDescription(type = "varchar(1000)")
    private String dao;

    /**
     * 删除流程的用户
     */
    private Integer deleteUserId;

    @ToOne("DELETEUSERID")
    private User deleteUser;

    private Date deleteTime;

    public FlowInstanceBak()
    {
    }

    public String getDao()
    {
        return dao;
    }

    public void setDao(String dao)
    {
        this.dao = dao;
    }

    public Integer getDeleteUserId()
    {
        return deleteUserId;
    }

    public void setDeleteUserId(Integer deleteUserId)
    {
        this.deleteUserId = deleteUserId;
    }

    public User getDeleteUser()
    {
        return deleteUser;
    }

    public void setDeleteUser(User deleteUser)
    {
        this.deleteUser = deleteUser;
    }

    public Date getDeleteTime()
    {
        return deleteTime;
    }

    public void setDeleteTime(Date deleteTime)
    {
        this.deleteTime = deleteTime;
    }
}
