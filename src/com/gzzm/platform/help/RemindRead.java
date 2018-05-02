package com.gzzm.platform.help;

import com.gzzm.platform.organ.User;
import net.cyan.thunwind.annotation.Entity;

/**
 * 新功能提醒阅读记录，如果用户阅读过某条提醒，在此表中记录一条记录，以后不再显示
 *
 * @author camel
 * @date 2010-12-13
 */
@Entity(table = "PFREMINDREAD", keys = {"remindId", "userId"})
public class RemindRead
{
    private Integer remindId;

    private Integer userId;

    private Remind remind;

    private User user;

    public RemindRead()
    {
    }

    public Integer getRemindId()
    {
        return remindId;
    }

    public void setRemindId(Integer remindId)
    {
        this.remindId = remindId;
    }

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

    public Remind getRemind()
    {
        return remind;
    }

    public void setRemind(Remind remind)
    {
        this.remind = remind;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof RemindRead))
            return false;

        RemindRead that = (RemindRead) o;

        return remindId.equals(that.remindId) && userId.equals(that.userId);
    }

    @Override
    public int hashCode()
    {
        int result = remindId.hashCode();
        result = 31 * result + userId.hashCode();
        return result;
    }
}
