package com.gzzm.platform.menu;

import java.util.*;

/**
 * 简单的menuitem，只包含ID和菜单文本等简单内容
 *
 * @author camel
 * @date 2009-12-30
 */
public class SimpleMenuItem
{
    private String menuId;

    private String title;

    private MenuItem menuItem;

    private List<SimpleMenuItem> menuItems;

    public SimpleMenuItem(MenuItem menuItem)
    {
        this.menuItem = menuItem;
        menuId = menuItem.getMenuId();
        title = menuItem.getTitle();
    }

    public SimpleMenuItem(MenuItem menuItem, List<MenuItem> items)
    {
        menuId = menuItem.getMenuId();
        title = menuItem.getTitle();

        menuItems = new ArrayList<SimpleMenuItem>(items.size());
        for (MenuItem item : items)
            menuItems.add(new SimpleMenuItem(item));
    }

    public SimpleMenuItem(String menuId, String title, List<SimpleMenuItem> menuItems)
    {
        this.menuId = menuId;
        this.title = title;
        this.menuItems = menuItems;
    }

    MenuItem getMenuItem()
    {
        return menuItem;
    }

    List<SimpleMenuItem> getMenuItems()
    {
        return menuItems;
    }

    public String getMenuId()
    {
        return menuId;
    }

    public String getTitle()
    {
        return title;
    }

    @Override
    public String toString()
    {
        return title;
    }
}
