package com.gzzm.ods.archive;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.menu.*;
import net.cyan.commons.util.DateUtils;
import net.cyan.nest.annotation.Injectable;

import java.util.*;

/**
 * @author camel
 * @date 2016/6/12
 */
@Injectable(singleton = true)
public class ArchiveQueryLoader implements MenuLoader
{
    public ArchiveQueryLoader()
    {
    }

    private MenuItem getMenuItem(String menuId, int year) throws Exception
    {

        MenuItem archiveItem = new MenuItem(menuId + "/" + year);
        archiveItem.setTitle(year + "年");
        archiveItem.setHint("归档文件查询-" + year + "年");
        archiveItem.setAppTitle("归档文件查询-" + year + "年");
        archiveItem.setUrl("/ods/archive/archive?year=" + year + "&readOnly=true");
        return archiveItem;
    }

    @Override
    public List<MenuItem> load(MenuItem menuItem) throws Exception
    {
        List<MenuItem> menuItems = new ArrayList<MenuItem>(1);

        MenuItem menuItem0 = new MenuItem(menuItem.getMenuId() + "/root");
        menuItem0.setTitle(menuItem.getTitle());
        menuItem0.setHint(menuItem.getHint());
        menuItem0.setIconPath(menuItem.getIconPath());
        menuItems.add(menuItem0);

        int startYear = Tools.getStartYear();
        int endYear = DateUtils.getYear();
        for (int year = endYear; year > startYear; year--)
        {
            String menuId = menuItem.getMenuId();

            menuItem0.addChildMenu(getMenuItem(menuId, year));
        }

        return menuItems;
    }

    @Override
    public MenuItem load(MenuItem menuItem, String subMenuId) throws Exception
    {
        return getMenuItem(menuItem.getMenuId(), Integer.parseInt(subMenuId));
    }

    @Override
    public List<MenuAuth> getAuths() throws Exception
    {
        return null;
    }
}
