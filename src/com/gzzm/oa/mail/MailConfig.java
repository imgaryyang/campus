package com.gzzm.oa.mail;

import com.gzzm.platform.organ.User;
import net.cyan.thunwind.annotation.*;

/**
 * 邮件配置，记录每个用户的邮件配置信息
 *
 * @author camel
 * @date 2010-3-17
 */
@Entity(table = "OAMAILCONFIG", keys = "userId")
public class MailConfig
{
    /**
     * 用户ID
     *
     * @see com.gzzm.platform.organ.User
     */
    private Integer userId;

    /**
     * 关联用户对象
     */
    private User user;

    /**
     * 容量 单位：B
     */
    @ColumnDescription(type = "number(14)")
    private Long capacity;

    /**
     * 是否开通smtp服务功能
     */
    private Boolean smtp;

    /**
     * 是否开通pop3服务功能
     */
    private Boolean pop;

    public MailConfig()
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

    public Long getCapacity()
    {
        return capacity;
    }

    public void setCapacity(Long capacity)
    {
        this.capacity = capacity;
    }

    public Boolean isSmtp()
    {
        return smtp;
    }

    public void setSmtp(Boolean smtp)
    {
        this.smtp = smtp;
    }

    public Boolean isPop()
    {
        return pop;
    }

    public void setPop(Boolean pop)
    {
        this.pop = pop;
    }

    public MailConfig copy()
    {
        MailConfig config = new MailConfig();
        config.capacity = capacity;
        config.smtp = smtp;

        return config;
    }
}
