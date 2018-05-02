package com.gzzm.platform.organ;

import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.*;

/**
 * @author camel
 * @date 14-3-19
 */
@Entity(table = "PFUSERLEVEL", keys = "levelId")
public class UserLevel
{
    @Generatable(length = 4)
    private Integer levelId;

    @Require
    @ColumnDescription(type = "varchar(50)")
    private String levelName;

    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    public UserLevel()
    {
    }

    public Integer getLevelId()
    {
        return levelId;
    }

    public void setLevelId(Integer levelId)
    {
        this.levelId = levelId;
    }

    public String getLevelName()
    {
        return levelName;
    }

    public void setLevelName(String levelName)
    {
        this.levelName = levelName;
    }

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof UserLevel))
            return false;

        UserLevel userLevel = (UserLevel) o;

        return levelId.equals(userLevel.levelId);
    }

    @Override
    public int hashCode()
    {
        return levelId.hashCode();
    }

    @Override
    public String toString()
    {
        return levelName;
    }
}
