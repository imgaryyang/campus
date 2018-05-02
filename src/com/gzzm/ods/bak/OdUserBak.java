package com.gzzm.ods.bak;

import com.gzzm.platform.organ.User;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.*;

import java.sql.Date;

/**
 * @author camel
 * @date 12-10-15
 */
@Entity(table = "ODUSERBAK", keys = "backId")
public class OdUserBak
{
    @Generatable(length = 11)
    private Long backId;

    @Require
    @ColumnDescription(type = "varchar(250)")
    private String backName;

    private Integer userId;

    private User user;

    @Require
    private Date startTime;

    @Require
    private Date endTime;

    private java.util.Date createTime;

    @ColumnDescription(type = "varchar(250)")
    private String path;

    public OdUserBak()
    {
    }

    public Long getBackId()
    {
        return backId;
    }

    public void setBackId(Long backId)
    {
        this.backId = backId;
    }

    public String getBackName()
    {
        return backName;
    }

    public void setBackName(String backName)
    {
        this.backName = backName;
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

    public Date getStartTime()
    {
        return startTime;
    }

    public void setStartTime(Date startTime)
    {
        this.startTime = startTime;
    }

    public Date getEndTime()
    {
        return endTime;
    }

    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
    }

    public java.util.Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(java.util.Date createTime)
    {
        this.createTime = createTime;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof OdUserBak))
            return false;

        OdUserBak odUserBak = (OdUserBak) o;

        return backId.equals(odUserBak.backId);
    }

    @Override
    public int hashCode()
    {
        return backId.hashCode();
    }
}
