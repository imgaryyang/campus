package com.gzzm.platform.desktop;

import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.platform.menu.*;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 桌面定义相关的请求
 *
 * @author camel
 * @date 2010-6-3
 */
@Service
public class DesktopDefPage
{
    @Inject
    private UserOnlineInfo userOnlineInfo;

    @Inject
    private DesktopDao dao;

    @Inject
    private MenuContainer menuContainer;

    /**
     * 已经选中的应用ID列表
     */
    @NotSerialized
    private List<String> appIds;

    /**
     * 可选的菜单项
     */
    @NotSerialized
    private List<MenuItem> menuItems;

    private String groupId = "desktop";

    public DesktopDefPage()
    {
    }

    public String getGroupId()
    {
        return groupId;
    }

    public void setGroupId(String groupId)
    {
        this.groupId = groupId;
    }

    @Service(url = "/desktop")
    @NotSerialized
    public DesktopDef getDesktopDef() throws Exception
    {
        DesktopDef def = loadDesktopDef();

        if (def != null)
            init(def);

        return def;
    }

    private DesktopDef loadDesktopDef() throws Exception
    {
        UserDesktopConfig config = dao.getDesktopStyle(userOnlineInfo.getUserId(), groupId);

        DesktopDef def = null;
        if (config != null)
            def = config.getDesktopDef();

        if (def == null)
        {
            //用户还未自定义桌面，使用默认桌面
            UserDesktopConfig config0 = dao.getDesktopStyle(-1, getGroupId());
            if (config0 != null)
                def = config0.getDesktopDef();
        }

        if (def == null)
        {
            List<MenuItem> items = menuContainer.getMenuTree(groupId).getTree(userOnlineInfo, false, false);
            if (items != null && items.size() > 0)
            {
                def = new DesktopDef();

                ArrayList<DesktopColumn> columns = new ArrayList<DesktopColumn>(items.size());
                def.setColumns(columns);

                for (MenuItem menuItem : items)
                {
                    DesktopColumn column = new DesktopColumn();
                    DesktopModule module = new DesktopModule();
                    module.setAppId(menuItem.getMenuId());

                    List<DesktopModule> modules = new ArrayList<DesktopModule>();
                    modules.add(module);
                    column.setModules(modules);
                    columns.add(column);
                }
            }
        }

        return def;
    }

    @Service(url = "/desktop", method = HttpMethod.post)
    public void saveDesktopDef(DesktopDef def) throws Exception
    {
        if (groupId == null)
            groupId = "desktop";

        UserDesktopConfig config = new UserDesktopConfig();
        config.setUserId(userOnlineInfo.getUserId());
        config.setGroupId(groupId);
        config.setDesktopDef(def);

        dao.save(config);
    }

    private void init(DesktopDef def) throws Exception
    {
        for (DesktopColumn column : def.getColumns())
        {
            if (column != null && column.getModules() != null)
            {
                for (Iterator<DesktopModule> iterator = column.getModules().iterator(); iterator.hasNext(); )
                {
                    DesktopModule module = iterator.next();
                    if (module != null)
                    {
                        if (!StringUtils.isEmpty(module.getAppId()))
                        {
                            if (!userOnlineInfo.isAccessable(module.getAppId()))
                            {
                                //删除没有权限的功能点
                                iterator.remove();
                            }
                            else
                            {
                                //删除已经被删除的功能点
                                MenuItem menu = menuContainer.getMenu(module.getAppId());
                                if (menu == null)
                                    iterator.remove();
                                else
                                    module.setTitle(menu.getTitle());
                            }
                        }
                        else if (!StringUtils.isEmpty(module.getContent()))
                        {
                            module.setContent(HtmlUtils.clearScript(module.getContent()));
                        }
                    }
                }
            }
        }
    }

    @Service(url = "/desktop/def")
    @Forward(page = "/platform/desktop/defconfig.ptl")
    public String config() throws Exception
    {
        //加载桌面功能点
        for (MenuItem menuItem : menuContainer.getMenuTree(groupId).getTree(userOnlineInfo, false, false))
            addMenuItem(menuItem);

        //加载定义的桌面模块
        DesktopDef def = loadDesktopDef();

        if (def != null && def.getColumns() != null)
        {
            for (DesktopColumn column : def.getColumns())
            {
                if (column != null && column.getModules() != null)
                {
                    for (DesktopModule module : column.getModules())
                    {
                        if (module != null)
                        {
                            if (!StringUtils.isEmpty(module.getAppId()))
                            {
                                if (appIds == null)
                                    appIds = new ArrayList<String>();
                                appIds.add(module.getAppId());
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    private void addMenuItem(MenuItem menuItem)
    {
        if (menuItems == null)
            menuItems = new ArrayList<MenuItem>();

        menuItems.add(menuItem);
    }

    public List<String> getAppIds()
    {
        return appIds;
    }

    public void setAppIds(List<String> appIds)
    {
        this.appIds = appIds;
    }

    public List<MenuItem> getMenuItems()
    {
        return menuItems;
    }
}
