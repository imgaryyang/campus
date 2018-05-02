package com.gzzm.oa.mail;

import com.gzzm.platform.organ.User;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.*;

/**
 * 邮件归档配置，配置某些来源的邮件自动归到某个目录
 *
 * @author camel
 * @date 2015/10/8
 */
@Entity(table = "OAMAILCATALOGCONFIG", keys = "configId")
public class MailCatalogConfig
{
    @Generatable(length = 9)
    private Integer configId;

    @Index
    private Integer userId;

    @NotSerialized
    private User user;

    @Require
    private Long catalogId;

    @NotSerialized
    private MailCatalog catalog;

    /**
     * 发件人的id，仅对系统内部邮件有效
     */
    private Integer sender;

    /**
     * 关联发件人
     */
    @ToOne("SENDER")
    @NotSerialized
    private User senderUser;

    @ColumnDescription(type = "varchar(500)")
    private String mailFrom;

    @Require
    private MailFromType type;

    public MailCatalogConfig()
    {
    }

    public Integer getConfigId()
    {
        return configId;
    }

    public void setConfigId(Integer configId)
    {
        this.configId = configId;
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

    public Long getCatalogId()
    {
        return catalogId;
    }

    public void setCatalogId(Long catalogId)
    {
        this.catalogId = catalogId;
    }

    public MailCatalog getCatalog()
    {
        return catalog;
    }

    public void setCatalog(MailCatalog catalog)
    {
        this.catalog = catalog;
    }

    public Integer getSender()
    {
        return sender;
    }

    public void setSender(Integer sender)
    {
        this.sender = sender;
    }

    public User getSenderUser()
    {
        return senderUser;
    }

    public void setSenderUser(User senderUser)
    {
        this.senderUser = senderUser;
    }

    public String getMailFrom()
    {
        return mailFrom;
    }

    public void setMailFrom(String mailFrom)
    {
        this.mailFrom = mailFrom;
    }

    public MailFromType getType()
    {
        return type;
    }

    public void setType(MailFromType type)
    {
        this.type = type;
    }

    /**
     * 发件人的名字
     *
     * @return 发送人的名称，如果是本地用户则显示用户及所属的部门名称，如果是异地用户，则直接显示异地邮件地址
     */
    @NotSerialized
    public String getSenderName()
    {
        if (type == MailFromType.IN)
        {
            User user = getSenderUser();
            if (user != null)
            {
                //发件人为本地用户
                return user.getUserName();
            }
            else
            {
                return null;
            }
        }
        else
        {
            return mailFrom;
        }
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof MailCatalogConfig))
            return false;

        MailCatalogConfig that = (MailCatalogConfig) o;

        return configId.equals(that.configId);
    }

    @Override
    public int hashCode()
    {
        return configId.hashCode();
    }
}
