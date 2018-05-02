package com.gzzm.oa.mail;

import com.gzzm.platform.organ.User;
import net.cyan.commons.util.Provider;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.nest.annotation.Inject;
import net.cyan.thunwind.annotation.*;
import net.cyan.arachne.annotation.NotSerialized;

/**
 * 邮件个人目录
 *
 * @author camel
 * @date 2010-3-15
 */
@Entity(table = "OAMAILCATALOG", keys = "catalogId")
public class MailCatalog
{
    @Inject
    private static Provider<MailDao> daoProvider;

    /**
     * 目录ID，-4表示已删除，-3表示发件箱，-2表示草稿箱，-1表示收件箱
     */
    @Generatable(length = 10)
    private Long catalogId;

    /**
     * 目录名称
     */
    @Unique(with = "userId")
    @Require
    private String catalogName;

    /**
     * 目录所属用户ID
     */
    @Index
    private Integer userId;

    /**
     * 关联User对象
     */
    private User user;

    /**
     * 邮件目录排序ID
     */
    private Integer orderId;

    @Transient
    @NotSerialized
    private MailCatalogInfo info;

    public MailCatalog()
    {
    }

    public MailCatalog(Long catalogId, String catalogName)
    {
        this.catalogId = catalogId;
        this.catalogName = catalogName;
    }

    public MailCatalog(Long catalogId, String catalogName, Integer userId)
    {
        this.catalogId = catalogId;
        this.catalogName = catalogName;
        this.userId = userId;
    }

    public Long getCatalogId()
    {
        return catalogId;
    }

    public void setCatalogId(Long catalogId)
    {
        this.catalogId = catalogId;
    }

    public String getCatalogName()
    {
        return catalogName;
    }

    public void setCatalogName(String catalogName)
    {
        this.catalogName = catalogName;
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

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
    }

    public MailCatalogInfo getInfo() throws Exception
    {
        if (info == null && catalogId != null)
        {
            MailDao dao = daoProvider.get();

            if (catalogId == -1)
            {
                //收件箱
                info = dao.getCatalogInfoWithType(userId, MailType.received);
            }
            else if (catalogId == -2)
            {
                //草稿箱
                info = dao.getCatalogInfoWithType(userId, MailType.draft);
            }
            else if (catalogId == -3)
            {
                //发件箱
                info = dao.getCatalogInfoWithType(userId, MailType.sended);
            }
            else if (catalogId == -4)
            {
                //已删除
                info = dao.getCatalogInfoWithDeleted(userId);
            }
            else
            {
                info = dao.getCatalogInfoWithCatalog(userId, catalogId);
            }
        }

        return info;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof MailCatalog))
            return false;

        MailCatalog that = (MailCatalog) o;

        return catalogId.equals(that.catalogId);

    }

    @Override
    public int hashCode()
    {
        return catalogId.hashCode();
    }

    @Override
    public String toString()
    {
        return catalogName;
    }
}
