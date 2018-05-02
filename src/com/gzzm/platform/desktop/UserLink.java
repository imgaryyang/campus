package com.gzzm.platform.desktop;

import com.gzzm.platform.menu.MenuContainer;
import com.gzzm.platform.organ.User;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.util.*;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.nest.annotation.Inject;
import net.cyan.thunwind.annotation.*;

/**
 * 用户自定义的链接和快捷方式
 *
 * @author camel
 * @date 2010-6-5
 */
@Entity(table = "PFUSERLINK", keys = "linkId")
public class UserLink
{
    @Inject
    private static Provider<MenuContainer> menuContainerProvider;

    /**
     * 连接ID，主键
     */
    @Generatable(length = 8)
    private Long linkId;

    /**
     * 用户ID，关联用户表
     */
    @Index
    @Require
    private Integer userId;

    /**
     * 关联用户对象
     */
    private User user;

    /**
     * 链接标题
     */
    @Require
    @ColumnDescription(type = "varchar(250)", nullable = false)
    private String title;

    /**
     * 访问的url
     */
    @ColumnDescription(type = "varchar(100)")
    private String url;

    /**
     * 对应的功能点ID，与url字段互斥
     */
    @ColumnDescription(type = "varchar(50)")
    private String appId;

    /**
     * 链接打开的目标
     */
    @Require
    private UserLinkTarget target;

    /**
     * 排序ID
     */
    @ColumnDescription(type = "number(6)", nullable = false)
    private Integer orderId;

    private Integer groupId;

    @NotSerialized
    private UserLinkGroup linkGroup;

    public UserLink()
    {
    }

    public Long getLinkId()
    {
        return linkId;
    }

    public void setLinkId(Long linkId)
    {
        this.linkId = linkId;
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

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getAppId()
    {
        return appId;
    }

    public void setAppId(String appId)
    {
        this.appId = appId;
    }

    public UserLinkTarget getTarget()
    {
        return target;
    }

    public void setTarget(UserLinkTarget target)
    {
        this.target = target;
    }

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
    }

    public Integer getGroupId()
    {
        return groupId;
    }

    public void setGroupId(Integer groupId)
    {
        this.groupId = groupId;
    }

    public UserLinkGroup getLinkGroup()
    {
        return linkGroup;
    }

    public void setLinkGroup(UserLinkGroup linkGroup)
    {
        this.linkGroup = linkGroup;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getHref()
    {
        if (StringUtils.isBlank(appId))
            return url;
        else
            return "/menu/" + appId;
    }

    public String getHrefText()
    {
        if (StringUtils.isBlank(appId))
            return url;
        else
            return menuContainerProvider.get().getMenu(appId).getTitle();
    }

    public String getMenuTitle()
    {
        if (StringUtils.isBlank(appId))
            return "";
        else
            return menuContainerProvider.get().getMenu(appId).getTitle();
    }

    @Override
    public String toString()
    {
        return title;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof UserLink))
            return false;

        UserLink userLink = (UserLink) o;

        return linkId.equals(userLink.linkId);
    }

    @Override
    public int hashCode()
    {
        return linkId.hashCode();
    }
}
