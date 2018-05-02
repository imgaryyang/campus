package com.gzzm.platform.menu;

import com.gzzm.platform.annotation.CacheInstance;
import com.gzzm.platform.commons.*;
import net.cyan.commons.collections.tree.TreeLoader;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 菜单cache
 *
 * @author camel
 * @date 2009-7-20
 */
@CacheInstance("Menu")
public class MenuContainer
{
    @Inject
    private static Provider<MenuDao> menuDaoProvider;

    private class MenuTreeLoader extends TreeLoader<Menu, MenuItem, String>
    {
        private Collection<MenuAuth> auths;

        public MenuTreeLoader(Collection<MenuAuth> auths)
        {
            this.auths = auths;
        }

        protected String getKey(Menu menu)
        {
            return menu.getMenuId();
        }

        protected String getParentKey(Menu menu)
        {
            return menu.getParentMenuId();
        }

        protected MenuItem create(String key)
        {
            return new MenuItem(key);
        }

        protected void copy(Menu menu, MenuItem menuItem)
        {
            menuItem.setParentMenuId(menu.getParentMenuId());
            menuItem.setTitle(menu.getMenuTitle());
            menuItem.setUrl(menu.getUrl());
            menuItem.setHint(menu.getHint());
            menuItem.setAppTitle(menu.getAppTitle());
            menuItem.setAppRemark(menu.getAppRemark());
            menuItem.setHidden(menu.isHidden() != null && menu.isHidden());
            menuItem.setCondition(menu.getCondition());
            menuItem.setDefaultMenuId(menu.getDefaultMenuId());

            if (menu.getParentMenuId() == null)
                menuItem.setGroup(menu.getMenuId());

            List<String> auths = new ArrayList<String>();
            for (MenuAuth menuAuth : this.auths)
            {
                if (menuAuth.getMenuId().equals(menu.getMenuId()))
                    auths.add(menuAuth.getAuthCode());
            }
            menuItem.setAuths(auths);

            //将图片加载到硬盘
            byte[] icon = menu.getIcon();
            if (icon != null)
            {
                String path = MenuItem.getIconPath(menuItem.getMenuId());
                menuItem.setIconPath(path);
                try
                {
                    IOUtils.bytesToFile(icon, Tools.getAppPath(path));
                }
                catch (Throwable ex)
                {
//                    Tools.log(ex);
                }
            }
        }

        protected void addChild(MenuItem parent, MenuItem menu)
        {
            menu.setGroup(parent.getGroup());
            parent.addChildMenu(menu);
        }

        protected void addRoot(MenuItem root)
        {
            trees.add(new MenuTree(root));
        }
    }

    /**
     * 保存所有的菜单树，key为groupId，值为对应的MenuTree对象
     */
    private List<MenuTree> trees = new ArrayList<MenuTree>();

    private List<MenuTree> unmodifiableTrees = Collections.unmodifiableList(trees);

    /**
     * 保存所有的菜单，key为menuId，值为菜单对应的MenuItem对象
     */
    private Map<String, MenuItem> menuMap = new HashMap<String, MenuItem>();

    /**
     * 保存所有系统定义的url
     */
    private List<AppUrl> urls;

    public MenuContainer() throws Exception
    {
        load();
    }

    private void load() throws Exception
    {
        //需要加载数据
        MenuDao dao = menuDaoProvider.get();
        List<Menu> menus = dao.getAllMenus();
        Map<Integer, MenuAuth> auths = dao.getAllMenuAuths();
        List<MenuAuthUrl> urls = dao.getAllMenuAuthUrls();

        Map<String, AppUrl> urlMap = new HashMap<String, AppUrl>();

        for (Menu menu : menus)
        {
            String menuId = menu.getMenuId();
            putUrl(menu.getUrl(), HttpMethod.GET, menuId, "default", urlMap);
        }

        for (MenuAuthUrl authUrl : urls)
        {
            Integer authId = authUrl.getAuthId();
            MenuAuth menuAuth = auths.get(authId);
            putUrl(authUrl.getUrl(), authUrl.getMethod(), menuAuth.getMenuId(), menuAuth.getAuthCode(), urlMap);
        }

        this.urls = Collections.unmodifiableList(new ArrayList<AppUrl>(urlMap.values()));

        new MenuTreeLoader(auths.values()).load(menus, menuMap);
    }

    public MenuTree getMenuTree(String group) throws SystemException
    {
        for (MenuTree tree : trees)
        {
            if (tree.getGroup().equals(group))
                return tree;
        }

        throw new SystemException("menu " + group + " does not exists");
    }

    public MenuItem getMenu(String menuId)
    {
        return menuMap.get(menuId);
    }

    public MenuItem getMenuByUrl(String url)
    {
        for (MenuItem menuItem : menuMap.values())
        {
            if (url.equals(menuItem.getUrl()))
                return menuItem;
        }

        return null;
    }

    public List<String> getMenuIdsByUrl(String url)
    {
        return getMenuIdsByUrl(Collections.singleton(url));
    }

    public List<String> getMenuIdsByUrl(String... urls)
    {
        return getMenuIdsByUrl(Arrays.asList(urls));
    }

    public List<String> getMenuIdsByUrl(Collection<String> urls)
    {
        List<String> menuIds = new ArrayList<String>();

        for (MenuItem menuItem : menuMap.values())
        {
            if (urls.contains(menuItem.getUrl()))
                menuIds.add(menuItem.getMenuId());
        }

        return menuIds;
    }

    public List<AppUrl> getUrls() throws Exception
    {
        return urls;
    }

    private static void putUrl(String url, HttpMethod method, String menuId, String auth, Map<String, AppUrl> urlMap)
    {
        if (url != null)
        {
            url = url.trim();

            String s = url;
            if (method != null)
                s += ":" + method;

            AppUrl u = urlMap.get(s);
            if (u == null)
                urlMap.put(s, u = new AppUrl(url, method == null ? null : method.toString()));

            u.addApp(menuId, auth);
        }
    }

    public List<MenuTree> getTrees()
    {
        return unmodifiableTrees;
    }
}
