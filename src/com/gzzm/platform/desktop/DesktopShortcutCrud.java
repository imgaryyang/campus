package com.gzzm.platform.desktop;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.platform.menu.MenuTreeModel;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.Provider;
import net.cyan.crud.view.Align;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 桌面快捷图标维护
 *
 * @author camel
 * @date 2011-2-16
 */
@Service(url = "/desktop/shortcut")
public class DesktopShortcutCrud extends BaseNormalCrud<DesktopShortcut, Integer>
{
    @Inject
    private static Provider<MenuTreeModel> menuTreeModelProvider;

    private MenuTreeModel menuTree;

    @Inject
    private UserOnlineInfo userOnlineInfo;

    private String groupId = "oa";

    public DesktopShortcutCrud()
    {
        setPageSize(-1);
    }

    @Override
    public String getOrderField()
    {
        return "orderId";
    }

    public String getGroupId()
    {
        return groupId;
    }

    public void setGroupId(String groupId)
    {
        this.groupId = groupId;
    }

    @Select(field = "entity.appId")
    public MenuTreeModel getMenuTree()
    {
        DesktopShortcut shortcut = getEntity();
        if (shortcut != null)
        {
            ShortcutType type = shortcut.getType();
            if (type == null || type == ShortcutType.menu)
            {
                if (menuTree == null)
                {
                    menuTree = menuTreeModelProvider.get();
                    menuTree.setCheckable(false);
                    menuTree.setGroup(groupId);
                }
                return menuTree;
            }
        }

        return null;
    }

    @Override
    @Forwards({@Forward(name = "menu", page = Pages.EDIT), @Forward(name = "action", page = Pages.EDIT)})
    public String add(String forward) throws Exception
    {
        return super.add(forward);
    }

    @Override
    public void initEntity(DesktopShortcut entity) throws Exception
    {
        if ("action".equals(getForward()))
            entity.setType(ShortcutType.action);
        else
            entity.setType(ShortcutType.menu);

        entity.setGroupId(groupId);
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        view.addColumn("名称", "shortcutName").setWidth("250");
        view.addColumn("菜单", new CHref("${menuTitle}").setAction("System.goMenu('${appId}')")).setAlign(Align.left);
        view.addColumn("编码", "(type!=null&&type.toString()=='action')?appId:''");
        view.addColumn("图标", "icon");
        view.addColumn("动作", new CHref("${action}").setAction("${action}")).setAutoExpand(true)
                .setAlign(Align.left);

        view.addButton(Buttons.query());
        view.addButton(Buttons.setId(Buttons.add("menu", "新增菜单快捷图标"), "addWithMenu"));
        view.addButton(Buttons.setId(Buttons.add("action", "新增动作快捷图标"), "addWithAdd"));
        view.addButton(Buttons.delete());
        view.addButton(Buttons.sort());
        view.makeEditable();

        return view;

    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        if (getEntity().getType() == ShortcutType.action)
        {
            view.addComponent("名称", "shortcutName");
            view.addComponent("编码", "appId");
            view.addComponent("图标", "icon");
            view.addComponent("动作", new CTextArea("action"));
        }
        else
        {
            view.addComponent("名称", "shortcutName");
            view.addComponent("菜单", "appId").setProperty("text", getEntity().getMenuTitle())
                    .setProperty("require", null);
            view.addComponent("图标", "icon");
            view.importCss("/platform/desktop/shortcut.css");
        }

        view.addDefaultButtons();

        return view;
    }

    /**
     * 加载当前用户拥有权限的快捷图标
     *
     * @return 当前用户拥有权限的快捷图标列表
     * @throws Exception 从数据库加载快捷图标错误
     */
    @Service(url = "/desktop/shortcuts/user")
    public List<DesktopShortcut> loadShortcuts() throws Exception
    {
        loadList();

        List<DesktopShortcut> shortcuts = getList();

        //超级管理员，不需要过滤图标
        if (userOnlineInfo.isAdmin())
            return shortcuts;

        List<DesktopShortcut> result = new ArrayList<DesktopShortcut>();

        //过滤有权限的数据
        for (DesktopShortcut shortcut : shortcuts)
        {
            if (shortcut.getType() == ShortcutType.action)
            {
                //自定义动作图标，每个用户都有权限
                result.add(shortcut);
            }
            else if (userOnlineInfo.isAccessable(shortcut.getAppId()))
            {
                //当前用户对此图标指向的菜单拥有权限，加载之
                result.add(shortcut);
            }
        }

        return result;
    }
}
