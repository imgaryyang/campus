package com.gzzm.platform.desktop;

import com.gzzm.platform.annotation.MenuId;
import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.platform.menu.*;
import net.cyan.arachne.annotation.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * @author camel
 * @date 2016/9/25
 */
@Service
public class DesktopModuleGroupPage
{
    @MenuId
    private String menuId;

    @Inject
    private UserOnlineInfo userOnlineInfo;

    @Inject
    private MenuContainer menuContainer;

    @NotSerialized
    private MenuItem menuItem;

    @NotSerialized
    private List<MenuItem> menuItems;

    /**
     * 是否在标签页中显示记录数
     */
    private boolean count;

    /**
     * 是否隐藏没有数据的标签页
     */
    private boolean hideEmpty = true;

    /**
     * 开始是隐藏全部标签
     */
    private boolean hideAllStart = true;

    public DesktopModuleGroupPage()
    {
    }

    public String getMenuId()
    {
        return menuId;
    }

    public void setMenuId(String menuId)
    {
        this.menuId = menuId;
    }

    public boolean isCount()
    {
        return count;
    }

    public void setCount(boolean count)
    {
        this.count = count;
    }

    public boolean isHideEmpty()
    {
        return hideEmpty;
    }

    public void setHideEmpty(boolean hideEmpty)
    {
        this.hideEmpty = hideEmpty;
    }

    public boolean isHideAllStart()
    {
        return hideAllStart;
    }

    public void setHideAllStart(boolean hideAllStart)
    {
        this.hideAllStart = hideAllStart;
    }

    public MenuItem getMenuItem()
    {
        if (menuItem == null)
            menuItem = menuContainer.getMenu(menuId);

        return menuItem;
    }

    public List<MenuItem> getMenuItems() throws Exception
    {
        if (menuItems == null)
        {
            menuItems = new ArrayList<MenuItem>();

            for (MenuItem menuItem : getMenuItem().getChildren())
            {
                if (userOnlineInfo.isAdmin() || userOnlineInfo.isAccessable(menuItem.getMenuId()))
                {
                    menuItems.add(menuItem);
                }
            }
        }
        return menuItems;
    }

    @Service(url = {"/desktop/module/{menuId}/group", "/desktop/module/group"})
    public String showDesktopModuleGroup() throws Exception
    {
        return "module_group.ptl";
    }
}
