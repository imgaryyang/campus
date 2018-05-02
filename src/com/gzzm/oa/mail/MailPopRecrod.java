package com.gzzm.oa.mail;

import com.gzzm.platform.organ.User;
import net.cyan.thunwind.annotation.*;

/**
 * 邮件Pop3接收记录，记录每个用户从pop3服务器上接收邮件的记录，避免重复接收邮件
 *
 * @author camel
 * @date 2010-3-16
 */
@Entity(table = "OAMAILPOPRECORD", keys = {"uidl", "pop3", "userId"})
public class MailPopRecrod
{
    /**
     * pop3地址
     */
    @ColumnDescription(type = "varchar(30)")
    private String pop3;

    /**
     * 系统用户ID，关联User对象
     *
     * @see com.gzzm.platform.organ.User
     */
    private Integer userId;

    /**
     * 关联User对象
     */
    private User user;

    /**
     * 接收的邮件的uidl
     */
    @ColumnDescription(type = "varchar(200)")
    private String uidl;

    public MailPopRecrod()
    {
    }

    public String getPop3()
    {
        return pop3;
    }

    public void setPop3(String pop3)
    {
        this.pop3 = pop3;
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

    public String getUidl()
    {
        return uidl;
    }

    public void setUidl(String uidl)
    {
        this.uidl = uidl;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof MailPopRecrod))
            return false;

        MailPopRecrod that = (MailPopRecrod) o;

        return pop3.equals(that.pop3) && uidl.equals(that.uidl) &&
                userId.equals(that.userId);

    }

    @Override
    public int hashCode()
    {
        int result = pop3.hashCode();
        result = 31 * result + userId.hashCode();
        result = 31 * result + uidl.hashCode();
        return result;
    }
}
