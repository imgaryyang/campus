package com.gzzm.platform.organ;

import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.*;

/**
 * @author camel
 * @date 2016/9/22
 */
@Entity(table = "PFUSERPROPERTY", keys = {"userId", "propertyName"})
public class UserProperty
{
    private Integer userId;

    @NotSerialized
    private User user;

    @ColumnDescription(type = "varchar(500)")
    private String propertyName;

    @ColumnDescription(type = "varchar(4000)")
    private String propertyValue;

    public UserProperty()
    {
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

    public String getPropertyName()
    {
        return propertyName;
    }

    public void setPropertyName(String propertyName)
    {
        this.propertyName = propertyName;
    }

    public String getPropertyValue()
    {
        return propertyValue;
    }

    public void setPropertyValue(String propertyValue)
    {
        this.propertyValue = propertyValue;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof UserProperty))
            return false;

        UserProperty that = (UserProperty) o;

        return propertyName.equals(that.propertyName) && userId.equals(that.userId);
    }

    @Override
    public int hashCode()
    {
        int result = userId.hashCode();
        result = 31 * result + propertyName.hashCode();
        return result;
    }
}
