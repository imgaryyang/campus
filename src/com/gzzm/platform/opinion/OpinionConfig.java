package com.gzzm.platform.opinion;

import com.gzzm.platform.organ.User;
import net.cyan.thunwind.annotation.*;

/**
 * @author camel
 * @date 2014/12/26
 */
@Entity(table = "PFOPINIONCONFIG", keys = "userId")
public class OpinionConfig
{
    private Integer userId;

    private User user;

    @ColumnDescription(defaultValue = "0")
    private Boolean autoAdd;

    private Boolean split;

    public OpinionConfig()
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

    public Boolean getAutoAdd()
    {
        return autoAdd;
    }

    public void setAutoAdd(Boolean autoAdd)
    {
        this.autoAdd = autoAdd;
    }

    public Boolean getSplit() {
        return split;
    }

    public void setSplit(Boolean split) {
        this.split = split;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof OpinionConfig))
            return false;

        OpinionConfig that = (OpinionConfig) o;

        return userId.equals(that.userId);
    }

    @Override
    public int hashCode()
    {
        return userId.hashCode();
    }
}
