package com.gzzm.platform.menu;

import net.cyan.arachne.RequestContext;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 菜单项对象，Menu在内存中的表示
 *
 * @author camel
 * @date 2009-7-20
 */
public class MenuItem implements Value<String>
{
    private static final long serialVersionUID = -7174063112478322356L;

    public static final String MENUID = "menuId$";

    public static final String SUB_MENUID = "subMenuId$";

    @Inject
    private static Provider<MenuContainer> containerProvider;

    /**
     * 菜单id
     */
    private String menuId;

    /**
     * 上级菜单ID
     */
    @NotSerialized
    private String parentMenuId;

    /**
     * 菜单所属的组
     */
    @NotSerialized
    private String group;

    /**
     * 标题
     */
    private String title;

    /**
     * 子菜单列表
     */
    private List<MenuItem> childMenus;

    /**
     * 防止外界修改childMenus
     */
    private List<MenuItem> unmodifiableChildMenus;

    /**
     * url
     */
    private String url;

    /**
     * 菜单提示
     */
    private String hint;

    /**
     * 图片路径
     */
    private String iconPath;

    /**
     * 功能标题
     */
    @NotSerialized
    private String appTitle;

    /**
     * 功能说明
     */
    @NotSerialized
    private String appRemark;

    @NotSerialized
    private boolean hidden;

    /**
     * @see com.gzzm.platform.menu.Menu#condition
     * @see com.gzzm.platform.commons.crud.SystemCrudUtils#getCondition(String)
     */
    @NotSerialized
    private String condition;

    @NotSerialized
    private String countCondition;

    /**
     * 权限列表
     */
    @NotSerialized
    private List<String> auths;

    private String defaultMenuId;

    public MenuItem(String menuId)
    {
        this.menuId = menuId;
    }

    public String getMenuId()
    {
        return menuId;
    }

    void setMenuId(String menuId)
    {
        this.menuId = menuId;
    }

    public String getParentMenuId()
    {
        return parentMenuId;
    }

    void setParentMenuId(String parentMenuId)
    {
        this.parentMenuId = parentMenuId;
    }

    public String getGroup()
    {
        return group;
    }

    void setGroup(String group)
    {
        this.group = group;

        if (childMenus != null)
        {
            for (MenuItem child : childMenus)
                child.setGroup(group);
        }
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        if (this.title != null)
            throw new IllegalStateException();

        this.title = title;
    }

    public List<String> getAuths()
    {
        return auths;
    }

    void setAuths(List<String> auths)
    {
        this.auths = Collections.unmodifiableList(auths);
    }

    public List<MenuItem> getChildren()
    {
        return unmodifiableChildMenus == null ? childMenus : unmodifiableChildMenus;
    }

    public void addChildMenu(MenuItem menu)
    {
        if (childMenus == null)
        {
            childMenus = new ArrayList<MenuItem>();
            unmodifiableChildMenus = Collections.unmodifiableList(childMenus);
        }

        childMenus.add(menu);
    }

    public boolean isLeaf()
    {
        return childMenus == null;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        if (this.url != null)
            throw new IllegalStateException();

        this.url = url;
    }

    public String getHint()
    {
        return hint;
    }

    public void setHint(String hint)
    {
        if (this.hint != null)
            throw new IllegalStateException();

        this.hint = hint;
    }

    public String getIconPath()
    {
        return iconPath;
    }

    public void setIconPath(String iconPath)
    {
        if (this.iconPath != null)
            throw new IllegalStateException();

        this.iconPath = iconPath;
    }

    public String getAppTitle()
    {
        return appTitle;
    }

    public void setAppTitle(String appTitle)
    {
        if (this.appTitle != null)
            throw new IllegalStateException();

        this.appTitle = appTitle;
    }

    public String getAppRemark()
    {
        return appRemark;
    }

    public void setAppRemark(String appRemark)
    {
        if (this.appRemark != null)
            throw new IllegalStateException();

        this.appRemark = appRemark;
    }

    public boolean isHidden()
    {
        return hidden;
    }

    void setHidden(boolean hidden)
    {
        this.hidden = hidden;
    }

    public String getCondition()
    {
        return condition;
    }

    void setCondition(String condition)
    {
        if ("".equals(condition))
            condition = null;
        this.condition = condition;
    }

    public String getCountCondition()
    {
        return countCondition;
    }

    void setCountCondition(String countCondition)
    {
        if ("".equals(countCondition))
            countCondition = null;
        this.countCondition = countCondition;
    }

    public String getDefaultMenuId()
    {
        return defaultMenuId;
    }

    void setDefaultMenuId(String defaultMenuId)
    {
        this.defaultMenuId = defaultMenuId;
    }

    @NotSerialized
    public String getAllTitle()
    {
        StringBuilder buffer = new StringBuilder();

        buffer.append(title);

        MenuContainer container = containerProvider.get();
        MenuItem menu = container.getMenu(parentMenuId);
        while (menu != null)
        {
            buffer.insert(0, "-");
            buffer.insert(0, menu.getTitle());

            menu = container.getMenu(menu.getParentMenuId());
        }

        return buffer.toString();
    }

    @NotSerialized
    public MenuLoader getLoader() throws Exception
    {
        if (!hidden && childMenus == null && url != null)
        {
            return Menu.getLoader(url);
        }

        return null;
    }

    /**
     * 获得权限内的菜单
     *
     * @param appIds 权限列表，即菜单id列表
     * @return 过滤后的菜单
     */
    public MenuItem getMenuItem(Collection<String> appIds, boolean showHidden, boolean load) throws Exception
    {
        if (childMenus == null)
        {
            return (showHidden || !hidden) && (appIds == null || appIds.contains(menuId)) ? this : null;
        }

        List<MenuItem> childMenus = null;
        for (MenuItem childMenu : this.childMenus)
        {
            MenuLoader loader = null;
            if (load)
            {
                loader = childMenu.getLoader();
            }

            if (loader != null)
            {
                if (appIds == null || appIds.contains(childMenu.getMenuId()))
                {
                    List<MenuItem> items = loader.load(childMenu);
                    if (items != null)
                    {
                        if (childMenus == null)
                            childMenus = new ArrayList<MenuItem>();
                        childMenus.addAll(items);
                    }
                }
            }
            else
            {
                MenuItem item = childMenu.getMenuItem(appIds, showHidden, load);
                if (item != null)
                {
                    if (childMenus == null)
                        childMenus = new ArrayList<MenuItem>();
                    childMenus.add(item);
                }
            }
        }

        if (childMenus == null)
            return null;

        MenuItem menu = new MenuItem(menuId);
        menu.title = title;
        menu.url = url;
        menu.hint = hint;
        menu.iconPath = iconPath;
        menu.defaultMenuId = defaultMenuId;
        menu.childMenus = childMenus;

        return menu;
    }

    public static String getMenuId(HttpServletRequest request)
    {
        String menuId = (String) request.getAttribute(MENUID);
        if (StringUtils.isEmpty(menuId))
            menuId = request.getParameter(MENUID);

        return menuId;
    }

    public static String getSubMenuId(HttpServletRequest request)
    {
        return request.getParameter(SUB_MENUID);
    }

    public static void setMenuId(HttpServletRequest request, String menuId)
    {
        request.setAttribute(MENUID, menuId);
    }

    @Inject
    public static MenuItem getMenu(HttpServletRequest request) throws Exception
    {
        String menuId = getMenuId(request);
        if (menuId != null)
        {
            MenuItem menu = getMenu(menuId);
            MenuLoader loader = menu.getLoader();
            if (loader != null)
            {
                String subMenuId = getSubMenuId(request);
                if (subMenuId != null)
                {
                    MenuItem subMenu = loader.load(menu, subMenuId);

                    if (subMenu != null)
                    {
                        MenuItem menu1 = new MenuItem(menuId);
                        menu1.group = menu.group;
                        menu1.title = subMenu.title;
                        menu1.url = subMenu.url;
                        menu1.appTitle = subMenu.getAppTitle();
                        menu1.appRemark = subMenu.getAppRemark();
                        menu = menu1;
                    }
                }

            }

            return menu;
        }

        return null;
    }

    public static MenuItem getMenu(String menuId)
    {
        return containerProvider.get().getMenu(menuId);
    }

    public static String formatUrl(String url, HttpServletRequest request) throws Exception
    {
        String menuId = getMenuId(request);
        return url + (url.contains("?") ? "&" : "?") + "menuId$=" + menuId;
    }

    public static String formatUrl(String url) throws Exception
    {
        return formatUrl(url, RequestContext.getContext().getRequest());
    }

    public boolean equals(Object o)
    {
        return this == o || o instanceof MenuItem && menuId.equals(((MenuItem) o).menuId);
    }

    public int hashCode()
    {
        return menuId.hashCode();
    }

    public String valueOf()
    {
        return menuId;
    }

    @Override
    public String toString()
    {
        return title;
    }

    public static String getIconPath(String menuId)
    {
        return "/temp/menu/icon/" + menuId + ".gif";
    }
}
