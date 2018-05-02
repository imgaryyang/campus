package com.gzzm.platform.devolve;

import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;
import net.cyan.thunwind.annotation.*;

import java.util.*;

/**
 * 工作移交记录
 *
 * @author camel
 * @date 13-1-8
 */
@Entity(table = "PFDEVOLVE", keys = "devolveId")
public class Devolve
{
    @Inject
    private static Provider<List<DevolverItem>> itemsProvider;

    @Generatable(length = 9)
    private Integer devolveId;

    private Integer fromUserId;

    @NotSerialized
    @ToOne("FROMUSERID")
    private User fromUser;

    private Integer toUserId;

    @NotSerialized
    @ToOne("TOUSERID")
    private User toUser;

    private Date devolveTime;

    /**
     * 操作移交的用户
     */
    private Integer userId;

    @NotSerialized
    private User user;

    /**
     * 操作移交的用户所属的部门
     */
    private Integer deptId;

    @NotSerialized
    private Dept dept;

    /**
     * 移交范围，即DevolverItem中的id,多个范围用逗号隔开
     *
     * @see DevolverItem#id
     */
    @ColumnDescription(type = "varchar(200)")
    private String scopes;

    /**
     * 和endiTime一起定义移交哪个时间段内的工作
     */
    private java.sql.Date startTime;

    private java.sql.Date endTime;

    public Devolve()
    {
    }

    public Integer getDevolveId()
    {
        return devolveId;
    }

    public void setDevolveId(Integer devolveId)
    {
        this.devolveId = devolveId;
    }

    public Integer getFromUserId()
    {
        return fromUserId;
    }

    public void setFromUserId(Integer fromUserId)
    {
        this.fromUserId = fromUserId;
    }

    public User getFromUser()
    {
        return fromUser;
    }

    public void setFromUser(User fromUser)
    {
        this.fromUser = fromUser;
    }

    public Integer getToUserId()
    {
        return toUserId;
    }

    public void setToUserId(Integer toUserId)
    {
        this.toUserId = toUserId;
    }

    public User getToUser()
    {
        return toUser;
    }

    public void setToUser(User toUser)
    {
        this.toUser = toUser;
    }

    public Date getDevolveTime()
    {
        return devolveTime;
    }

    public void setDevolveTime(Date devolveTime)
    {
        this.devolveTime = devolveTime;
    }

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
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

    public String getScopes()
    {
        return scopes;
    }

    public void setScopes(String scopes)
    {
        this.scopes = scopes;
    }

    @NotSerialized
    public String getScopeNames()
    {
        if (scopes == null)
            return "";

        String[] scopes = this.scopes.split(",");

        List<DevolverItem> items = itemsProvider.get();
        StringBuilder buffer = new StringBuilder();

        for (String scope : scopes)
        {
            for (DevolverItem item : items)
            {
                if (item.getId().equals(scope))
                {
                    StringUtils.concat(buffer, item.getName(), ",");
                    break;
                }
            }
        }

        return buffer.toString();
    }

    public java.sql.Date getStartTime()
    {
        return startTime;
    }

    public void setStartTime(java.sql.Date startTime)
    {
        this.startTime = startTime;
    }

    public java.sql.Date getEndTime()
    {
        return endTime;
    }

    public void setEndTime(java.sql.Date endTime)
    {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof Devolve))
            return false;

        Devolve devolve = (Devolve) o;

        return devolveId.equals(devolve.devolveId);
    }

    @Override
    public int hashCode()
    {
        return devolveId.hashCode();
    }
}
