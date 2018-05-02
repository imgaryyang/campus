package com.gzzm.oa.notice;

import com.gzzm.platform.organ.User;
import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * 内部信息跟踪实体类，对应数据库的内部信息阅读信息表
 *
 * @author czf
 * @date 2010-3-16
 */
@Entity(table = "OANOTICETRACE", keys = {"noticeId", "userId"})
public class NoticeTrace
{
    /**
     * 信息ID
     */
    private Integer noticeId;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 用户
     */
    private User user;

    /**
     * 关联的信息
     */
    @Cascade
    private Notice notice;

    /**
     * 阅读时间
     */
    private Date readTime;

    public Integer getNoticeId()
    {
        return noticeId;
    }

    public void setNoticeId(Integer noticeId)
    {
        this.noticeId = noticeId;
    }

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

    public Date getReadTime()
    {
        return readTime;
    }

    public void setReadTime(Date readTime)
    {
        this.readTime = readTime;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public Notice getNotice()
    {
        return notice;
    }

    public void setNotice(Notice notice)
    {
        this.notice = notice;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof NoticeTrace))
            return false;

        NoticeTrace that = (NoticeTrace) o;

        return noticeId.equals(that.noticeId) && noticeId.equals(that.noticeId);

    }

    @Override
    public int hashCode()
    {
        int result;
        result = noticeId.hashCode();
        result = 31 * result + userId.hashCode();
        return result;
    }
}
