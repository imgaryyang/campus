package com.gzzm.platform.menu;

import java.io.Serializable;
import java.util.*;

/**
 * 菜单配置
 *
 * @author camel
 * @date 11-11-22
 */
public class MenuConfig implements Serializable, Comparator<MenuItem>
{
    private static final long serialVersionUID = -299813917319718829L;

    private List<String> menuIds;

    public MenuConfig()
    {
    }

    public List<String> getMenuIds()
    {
        return menuIds;
    }

    public void setMenuIds(List<String> menuIds)
    {
        this.menuIds = menuIds;
    }

    public int compare(MenuItem o1, MenuItem o2)
    {
        if (menuIds == null)
            return 0;

        int index1 = menuIds.indexOf(o1.getMenuId());
        int index2 = menuIds.indexOf(o2.getMenuId());

        return index1 - index2;
    }
}
