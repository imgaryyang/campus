package com.gzzm.platform.desktop;

import com.gzzm.platform.menu.*;
import net.cyan.commons.util.*;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.nest.annotation.Inject;
import net.cyan.thunwind.annotation.*;

/**
 * 桌面快捷图标
 *
 * @author camel
 * @date 2011-2-16
 */
@Entity(table = "PFDESKTOPSHORTCUT", keys = "shortcutId")
public class DesktopShortcut
{
    @Inject
    private static Provider<MenuContainer> menuContainerProvider;

    /**
     * 快捷图标ID
     */
    @Generatable(length = 5)
    private Integer shortcutId;

    /**
     * 快捷图标名称，同时也是提示信息
     */
    private String shortcutName;

    /**
     * 快捷图标的图片名称，不包括路径和扩展名，路径和扩展名由样式js决定
     */
    @ColumnDescription(type = "varchar(20)", nullable = false)
    private String icon;

    /**
     * 快捷图标对应的应用id，对应到菜单表的menuId
     *
     * @see com.gzzm.platform.menu.Menu#menuId
     */
    @ColumnDescription(type = "varchar(50)", nullable = false)
    @Require
    private String appId;

    /**
     * 动作，当此快捷方式不关联菜单时，定义一段javascript脚本，点击快捷图标运行此脚本
     * 当关联菜单时，此字段为空
     */
    private String action;

    /**
     * 快捷图标类型，可能是关联一个菜单，也可能是自定义一段javascript
     */
    private ShortcutType type;

    /**
     * 组ID，默认为oa
     */
    @ColumnDescription(type = "varchar(25)", defaultValue = "'oa'", nullable = false)
    private String groupId;

    /**
     * 排序ID
     */
    @ColumnDescription(type = "number(6)", nullable = false)
    private Integer orderId;

    public DesktopShortcut()
    {
    }

    public Integer getShortcutId()
    {
        return shortcutId;
    }

    public void setShortcutId(Integer shortcutId)
    {
        this.shortcutId = shortcutId;
    }

    public String getShortcutName()
    {
        return shortcutName;
    }

    public void setShortcutName(String shortcutName)
    {
        this.shortcutName = shortcutName;
    }

    public String getIcon()
    {
        return icon;
    }

    public void setIcon(String icon)
    {
        this.icon = icon;
    }

    public String getAppId()
    {
        return appId;
    }

    public void setAppId(String appId)
    {
        this.appId = appId;
    }

    public String getAction()
    {
        return action;
    }

    public void setAction(String action)
    {
        this.action = action;
    }

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
    }

    public ShortcutType getType()
    {
        return type;
    }

    public void setType(ShortcutType type)
    {
        this.type = type;
    }

    public String getGroupId()
    {
        return groupId;
    }

    public void setGroupId(String groupId)
    {
        this.groupId = groupId;
    }

    public String getMenuTitle()
    {
        if ((type == null || type == ShortcutType.menu) && !StringUtils.isBlank(appId))
        {
            MenuItem menu = menuContainerProvider.get().getMenu(appId);
            if (menu != null)
                return menu.getTitle();
        }

        return "";
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof DesktopShortcut))
            return false;

        DesktopShortcut that = (DesktopShortcut) o;

        return shortcutId.equals(that.shortcutId);
    }

    @Override
    public int hashCode()
    {
        return shortcutId.hashCode();
    }

    @Override
    public String toString()
    {
        return shortcutName;
    }
}