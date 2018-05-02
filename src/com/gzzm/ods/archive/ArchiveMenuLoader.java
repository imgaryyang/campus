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
public class ArchiveMenuLoader implements MenuLoader
{
    public ArchiveMenuLoader()
    {
    }

    private MenuItem getMenuItem(String menuId, String type, int year) throws Exception
    {
        if ("catalog".equals(type))
        {
            MenuItem catalogItem = new MenuItem(menuId + "/catalog_" + year);
            catalogItem.setTitle("案件目录管理");
            catalogItem.setHint("案件目录管理");
            catalogItem.setAppTitle("案件目录管理-" + year + "年");
            catalogItem.setUrl("/ods/archive/catalog?year=" + year);
            return catalogItem;
        }
        else if ("archive".equals(type))
        {
            MenuItem archiveItem = new MenuItem(menuId + "/archive_" + year);
            archiveItem.setTitle("已归档文件");
            archiveItem.setHint("已归档文件");
            archiveItem.setAppTitle("已归档文件-" + year + "年");
            archiveItem.setUrl("/ods/archive/archive?year=" + year);
            return archiveItem;
        }
        else if ("receive".equals(type))
        {
            MenuItem receiveItem = new MenuItem(menuId + "/receive_" + year);
            receiveItem.setTitle("待归档收文");
            receiveItem.setHint("待归档收文");
            receiveItem.setAppTitle("待归档收文-" + year + "年");
            receiveItem.setUrl("/ods/archive/receive?year=" + year);

            return receiveItem;
        }
        else if ("send".equals(type))
        {
            MenuItem sendItem = new MenuItem(menuId + "/send_" + year);
            sendItem.setTitle("待归档发文");
            sendItem.setHint("待归档发文");
            sendItem.setAppTitle("待归档发文-" + year + "年");
            sendItem.setUrl("/ods/archive/send?year=" + year);

            return sendItem;
        }
        else if ("inner".equals(type))
        {
            MenuItem sendItem = new MenuItem(menuId + "/inner_" + year);
            sendItem.setTitle("待归档内部公文");
            sendItem.setHint("待归档内部公文");
            sendItem.setAppTitle("待归档内部公文-" + year + "年");
            sendItem.setUrl("/ods/archive/inner?year=" + year);

            return sendItem;
        }

        return null;
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

            MenuItem yearItem = new MenuItem(menuId + "/year_" + year);
            yearItem.setTitle(year + "年");
            yearItem.setHint(year + "年");
            menuItem0.addChildMenu(yearItem);


            yearItem.addChildMenu(getMenuItem(menuId, "catalog", year));
            yearItem.addChildMenu(getMenuItem(menuId, "archive", year));
            yearItem.addChildMenu(getMenuItem(menuId, "receive", year));
            yearItem.addChildMenu(getMenuItem(menuId, "send", year));
            yearItem.addChildMenu(getMenuItem(menuId, "inner", year));
        }

        return menuItems;
    }

    @Override
    public MenuItem load(MenuItem menuItem, String subMenuId) throws Exception
    {
        String[] ss = subMenuId.split("_");
        String type = ss[0];
        int year = Integer.parseInt(ss[1]);

        return getMenuItem(menuItem.getMenuId(), type, year);
    }

    @Override
    public List<MenuAuth> getAuths() throws Exception
    {
        return null;
    }
}
