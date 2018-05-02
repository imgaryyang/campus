package com.gzzm.platform.menu;

import com.gzzm.platform.organ.User;
import net.cyan.thunwind.annotation.*;

/**
 * 用户菜单配置
 *
 * @author camel
 * @date 11-11-23
 */
@Entity(table = "PFUSERMENUCONFIG", keys = {"userId", "menuGroup"})
public class UserMenuConfig
{
    private Integer userId;

    private User user;

    @ColumnDescription(type = "varchar(50)", nullable = false)
    private String menuGroup;

    private Integer showSize;

    @Xml
    private MenuConfig menuConfig;

    public UserMenuConfig()
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

    public Integer getShowSize()
    {
        return showSize;
    }

    public void setShowSize(Integer showSize)
    {
        this.showSize = showSize;
    }

    public String getMenuGroup()
    {
        return menuGroup;
    }

    public void setMenuGroup(String menuGroup)
    {
        this.menuGroup = menuGroup;
    }

    public MenuConfig getMenuConfig()
    {
        return menuConfig;
    }

    public void setMenuConfig(MenuConfig menuConfig)
    {
        this.menuConfig = menuConfig;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof UserMenuConfig))
            return false;

        UserMenuConfig that = (UserMenuConfig) o;

        return menuGroup.equals(that.menuGroup) && userId.equals(that.userId);
    }

    @Override
    public int hashCode()
    {
        int result = userId.hashCode();
        result = 31 * result + menuGroup.hashCode();
        return result;
    }
}
