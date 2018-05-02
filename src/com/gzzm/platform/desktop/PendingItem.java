package com.gzzm.platform.desktop;

import com.gzzm.platform.menu.*;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

import java.util.Map;

/**
 * 待办项，定义桌面待办事项列表上显示的一个项目，通过配置文件定义
 *
 * @author camel
 * @date 2010-6-11
 */
public class PendingItem
{
    @Inject
    private static Provider<MenuContainer> menuContainer;

    /**
     * 标题，如待办公文等
     */
    private String title;

    /**
     * 查询待办事项数量的sql，sql中可包括:userId和:deptIds两个参数，deptIds表示用户对此功能拥有权限的部门列表
     */
    private String sql;

    /**
     * 查询的数据库，对应db.properties里的定义，如果为null表示默认数据库
     */
    private String database;

    /**
     * 点击待办事项转向的页面，同时也根据此url做权限控制
     */
    private String url;

    /**
     * 待办实现显示的样式
     */
    private String style;

    /**
     * 功能对应的菜单项ID，根据url来获得
     */
    private volatile String menuId;

    /**
     * 参数，附加在url后面
     */
    private Map<String, String> args;

    private String name;

    private boolean displayEmpty;

    public PendingItem()
    {
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getSql()
    {
        return sql;
    }

    public void setSql(String sql)
    {
        this.sql = sql;
    }

    public String getDatabase()
    {
        return database;
    }

    public void setDatabase(String database)
    {
        this.database = database;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        if (url != null)
            url = url.trim();
        this.url = url;
    }

    public boolean isDisplayEmpty()
    {
        return displayEmpty;
    }

    public void setDisplayEmpty(boolean displayEmpty)
    {
        this.displayEmpty = displayEmpty;
    }

    public String getStyle()
    {
        return style;
    }

    public void setStyle(String style)
    {
        this.style = style;
    }

    public Map<String, String> getArgs()
    {
        return args;
    }

    public void setArgs(Map<String, String> args)
    {
        this.args = args;
    }

    public String getMenuId()
    {
        String menuId = this.menuId;

        if (menuId == null)
        {
            MenuItem menuItem = menuContainer.get().getMenuByUrl(url.trim());
            if (menuItem != null)
                menuId = this.menuId = menuItem.getMenuId();
            else
                menuId = "";
        }

        return menuId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
