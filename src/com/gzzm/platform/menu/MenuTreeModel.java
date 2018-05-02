package com.gzzm.platform.menu;

import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.arachne.components.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 菜单树模型
 *
 * @author camel
 * @date 2009-12-30
 */
public class MenuTreeModel
        implements CheckBoxTreeModel<SimpleMenuItem>, LazyPageTreeModel<SimpleMenuItem>, SelectableModel<SimpleMenuItem>
{
    @Inject
    private MenuContainer menuContainer;

    @Inject
    private UserOnlineInfo userOnlineInfo;

    private Collection<String> ids;

    private Collection<String> appIds;

    private boolean checkable = true;

    private boolean lazy;

    private String group;

    private boolean showHidden;

    private boolean load;

    private boolean leafOnly;

    private String rootId;

    public MenuTreeModel()
    {
    }

    public boolean isShowHidden()
    {
        return showHidden;
    }

    public void setShowHidden(boolean showHidden)
    {
        this.showHidden = showHidden;
    }

    public boolean isLoad()
    {
        return load;
    }

    public void setLoad(boolean load)
    {
        this.load = load;
    }

    public void setIds(Collection<String> ids)
    {
        this.ids = ids;
    }

    public void setAppIds(Collection<String> appIds)
    {
        this.appIds = appIds;
    }

    public void setCheckable(boolean checkable)
    {
        this.checkable = checkable;
    }

    public void setGroup(String group)
    {
        this.group = group;
    }

    public boolean hasCheckBox(SimpleMenuItem item) throws Exception
    {
        return checkable && item.getMenuItem() != null && (!leafOnly || isLeaf(item));
    }

    public boolean isLeafOnly()
    {
        return leafOnly;
    }

    public void setLeafOnly(boolean leafOnly)
    {
        this.leafOnly = leafOnly;
    }

    public String getRootId()
    {
        return rootId;
    }

    public void setRootId(String rootId)
    {
        this.rootId = rootId;
    }

    public Boolean isChecked(SimpleMenuItem item) throws Exception
    {
        return ids != null && ids.contains(item.getMenuId());
    }

    public SimpleMenuItem getRoot() throws Exception
    {
        if (rootId != null)
        {
            MenuItem menu = menuContainer.getMenu(rootId);
            return new SimpleMenuItem(menu);
        }
        else if (group == null)
        {
            List<MenuTree> menuTrees = menuContainer.getTrees();

            List<SimpleMenuItem> menuItems = new ArrayList<SimpleMenuItem>(menuTrees.size());
            for (MenuTree menuTree : menuTrees)
            {
                List<MenuItem> items = appIds == null ? menuTree.getTree(userOnlineInfo, showHidden, load)
                        : menuTree.getTree(appIds, showHidden, load);
                if (items != null && items.size() > 0)
                    menuItems.add(new SimpleMenuItem(menuTree.getRoot(), items));
            }

            return new SimpleMenuItem("root", "根节点", menuItems);
        }
        else
        {
            MenuTree menuTree = menuContainer.getMenuTree(group);
            List<MenuItem> items = appIds == null ? menuTree.getTree(userOnlineInfo, showHidden, load)
                    : menuTree.getTree(appIds, showHidden, load);
            return new SimpleMenuItem(menuTree.getRoot(), items);
        }
    }

    public boolean isLeaf(SimpleMenuItem item) throws Exception
    {
        MenuItem menuItem = item.getMenuItem();
        if (menuItem != null)
            return menuItem.isLeaf();
        else
            return item.getMenuItems() == null;
    }

    public int getChildCount(SimpleMenuItem parent) throws Exception
    {
        List<SimpleMenuItem> menuItems = parent.getMenuItems();
        if (menuItems != null)
            return menuItems.size();

        MenuItem menuItem = parent.getMenuItem();
        return menuItem == null ? 0 : menuItem.getChildren().size();
    }

    public SimpleMenuItem getChild(SimpleMenuItem parent, int index) throws Exception
    {
        List<SimpleMenuItem> menuItems = parent.getMenuItems();
        return menuItems == null ? new SimpleMenuItem(parent.getMenuItem().getChildren().get(index)) :
                menuItems.get(index);
    }

    public String getId(SimpleMenuItem item) throws Exception
    {
        return item.getMenuId();
    }

    public String toString(SimpleMenuItem item) throws Exception
    {
        return item.getTitle();
    }

    public SimpleMenuItem getNode(String id) throws Exception
    {
        if ("root".equals(id))
            return getRoot();
        MenuItem menuItem = menuContainer.getMenu(id);

        if (appIds != null)
            menuItem = menuItem.getMenuItem(appIds, showHidden, load);
        else if (!userOnlineInfo.isAdmin())
            menuItem = menuItem.getMenuItem(userOnlineInfo.getAppIds(), showHidden, load);

        return new SimpleMenuItem(menuItem);
    }

    public Boolean isRootVisible()
    {
        return null;
    }

    public void setLazy(boolean lazy)
    {
        this.lazy = lazy;
    }

    public boolean isLazyLoad(SimpleMenuItem item) throws Exception
    {
        return lazy;
    }

    public void beforeLazyLoad(String id) throws Exception
    {
    }

    @Override
    public boolean isSelectable(SimpleMenuItem item) throws Exception
    {
        return !leafOnly || isLeaf(item);
    }
}
