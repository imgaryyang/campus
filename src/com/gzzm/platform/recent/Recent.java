package com.gzzm.platform.recent;

import com.gzzm.platform.organ.User;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * 近期文件
 *
 * @author camel
 * @date 2016/1/13
 */
@Entity(table = "PFRECENT", keys = "recentId")
@Indexes({@Index(columns = {"userId", "url"})})
public class Recent
{
    @Generatable(name = "uuid", length = 32)
    private String recentId;

    /**
     * 文件名称
     */
    @ColumnDescription(type = "varchar(800)")
    private String recentName;

    /**
     * 最后一次打开的时间
     */
    private Date lastTime;

    private Integer userId;

    @ToOne
    @NotSerialized
    private User user;

    /**
     * 文件类型
     */
    @ColumnDescription(type = "varchar(50)")
    private String type;

    @ColumnDescription(type = "varchar(250)")
    private String url;

    private RecentTarget target;

    private Date showTime;

    public Recent()
    {
    }

    public String getRecentId()
    {
        return recentId;
    }

    public void setRecentId(String recentId)
    {
        this.recentId = recentId;
    }

    public String getRecentName()
    {
        return recentName;
    }

    public void setRecentName(String recentName)
    {
        this.recentName = recentName;
    }

    public Date getLastTime()
    {
        return lastTime;
    }

    public void setLastTime(Date lastTime)
    {
        this.lastTime = lastTime;
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

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public RecentTarget getTarget()
    {
        return target;
    }

    public void setTarget(RecentTarget target)
    {
        this.target = target;
    }

    public Date getShowTime()
    {
        return showTime;
    }

    public void setShowTime(Date showTime)
    {
        this.showTime = showTime;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof Recent))
            return false;

        Recent recent = (Recent) o;

        return recentId.equals(recent.recentId);
    }

    @Override
    public int hashCode()
    {
        return recentId.hashCode();
    }
}
