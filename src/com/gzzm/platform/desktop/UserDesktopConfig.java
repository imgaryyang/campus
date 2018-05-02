package com.gzzm.platform.desktop;

import com.gzzm.platform.organ.User;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.*;
import net.cyan.thunwind.annotation.*;

/**
 * 用户桌面风格配置
 *
 * @author camel
 * @date 2009-10-5
 */
@Entity(table = "PFUSERDESKTOPCONFIG", keys = {"userId", "groupId"})
public class UserDesktopConfig
{
    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 组ID，默认为oa
     */
    @ColumnDescription(type = "varchar(25)", defaultValue = "'desktop'")
    private String groupId;

    /**
     * 用户对应的桌面风格的路径
     */
    @ColumnDescription(type = "varchar(250)")
    private DesktopStyle stylePath;

    /**
     * 页面大小
     */
    @MaxVal("99")
    @MinVal("0")
    @ColumnDescription(type = "number(3)")
    private Integer pageSize;

    /**
     * 关联user对象
     */
    private User user;

    @NotSerialized
    @Xml
    private DesktopDef desktopDef;

    /**
     * 是否自动加载列表
     */
    @ColumnDescription(defaultValue = "1")
    private Boolean autoReload;

    public UserDesktopConfig()
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

    public String getGroupId()
    {
        return groupId;
    }

    public void setGroupId(String groupId)
    {
        this.groupId = groupId;
    }

    public DesktopStyle getStylePath()
    {
        return stylePath;
    }

    public void setStylePath(DesktopStyle stylePath)
    {
        this.stylePath = stylePath;
    }

    public void setStylePath(String stylePath)
    {
        this.stylePath = new DesktopStyle(stylePath);
    }

    public Integer getPageSize()
    {
        return pageSize;
    }

    public void setPageSize(Integer pageSize)
    {
        this.pageSize = pageSize;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public DesktopDef getDesktopDef()
    {
        return desktopDef;
    }

    public void setDesktopDef(DesktopDef desktopDef)
    {
        this.desktopDef = desktopDef;
    }

    public Boolean getAutoReload()
    {
        return autoReload;
    }

    public void setAutoReload(Boolean autoReload)
    {
        this.autoReload = autoReload;
    }

    @Override
    public boolean equals(Object o)
    {
        return this == o || o instanceof UserDesktopConfig && userId.equals(((UserDesktopConfig) o).userId);
    }

    @Override
    public int hashCode()
    {
        return userId.hashCode();
    }
}