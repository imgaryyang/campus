package com.gzzm.platform.menu;

import com.gzzm.platform.commons.*;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.util.*;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.nest.annotation.Inject;
import net.cyan.thunwind.annotation.*;

import java.util.*;

/**
 * 菜单项
 *
 * @author camel
 * @date 2009-7-20
 */
@Entity(table = "PFMENU", keys = "menuId")
public class Menu
{
    @Inject
    private static Provider<List<MenuAuthsProvider>> authProvidersProvider;

    /**
     * 菜单id，长度为6,前2位为系统id，后4位为序列号
     * 实际字段为varchar，可以自定义一些菜单的编码
     */
    @Generatable(length = 6)
    @Unique
    @ColumnDescription(type = "varchar(50)", nullable = false)
    private String menuId;

    /**
     * 菜单标题
     */
    @Require
    @ColumnDescription(type = "varchar(250)", nullable = false)
    private String menuTitle;

    /**
     * 上一级菜单id，如果为null表示第一级菜单
     */
    @Index
    @ColumnDescription(type = "varchar(50)")
    private String parentMenuId;

    @ToOne(value = "PARENTMENUID")
    @NotSerialized
    private Menu parentMenu;

    /**
     * 子菜单列表
     */
    @NotSerialized
    @OneToMany("PARENTMENUID")
    @OrderBy(column = "ORDERID")
    private List<Menu> childMenus;

    /**
     * 菜单的url
     */
    @ColumnDescription(type = "varchar(250)")
    private String url;

    /**
     * 菜单提示
     */
    @ColumnDescription(type = "varchar(250)")
    private String hint;

    /**
     * 功能标题
     */
    @ColumnDescription(type = "varchar(250)")
    private String appTitle;

    /**
     * 功能说明
     */
    @ColumnDescription(type = "varchar(500)")
    private String appRemark;

    /**
     * 在同一级菜单中的排序
     */
    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    /**
     * 菜单图标
     */
    private byte[] icon;


    /**
     * 此菜单所关联的操作
     */
    @NotSerialized
    @OneToMany
    private SortedSet<MenuAuth> auths;

    /**
     * 菜单是否隐藏
     */
    @NotSerialized
    private Boolean hidden;

    /**
     * 菜单创建的时间
     */
    private Date createTime;

    /**
     * 附加查询条件，此字符串将会附加到查询语句后面
     *
     * @see com.gzzm.platform.menu.MenuItem#condition
     * @see com.gzzm.platform.commons.crud.SystemCrudUtils#getCondition(String)
     */
    @ColumnDescription(type = "varchar(500)")
    private String condition;

    /**
     * 数量提示的条件
     */
    @ColumnDescription(type = "varchar(500)")
    private String countCondition;

    @Require
    @ColumnDescription(nullable = false, defaultValue = "1")
    private Boolean valid;

    private String defaultMenuId;

    @ToOne("DEFAULTMENUID")
    @NotSerialized
    private Menu defaultMenu;

    public Menu()
    {
    }

    public Menu(String menuId, String menuTitle)
    {
        this.menuId = menuId;
        this.menuTitle = menuTitle;
    }

    public String getMenuId()
    {
        return menuId;
    }

    public void setMenuId(String menuId)
    {
        this.menuId = menuId;
    }

    public String getMenuTitle()
    {
        return menuTitle;
    }

    public void setMenuTitle(String menuTitle)
    {
        this.menuTitle = menuTitle;
    }

    public String getParentMenuId()
    {
        return parentMenuId;
    }

    public void setParentMenuId(String parentMenuId)
    {
        this.parentMenuId = parentMenuId;
    }

    public Menu getParentMenu()
    {
        return parentMenu;
    }

    public void setParentMenu(Menu parentMenu)
    {
        this.parentMenu = parentMenu;
    }

    public List<Menu> getChildMenus()
    {
        return childMenus;
    }

    public void setChildMenus(List<Menu> childMenus)
    {
        this.childMenus = childMenus;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getHint()
    {
        return hint;
    }

    public void setHint(String hint)
    {
        this.hint = hint;
    }

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
    }

    public byte[] getIcon()
    {
        return icon;
    }

    public void setIcon(byte[] icon)
    {
        this.icon = icon;
    }

    public String getAppTitle()
    {
        return appTitle;
    }

    public void setAppTitle(String appTitle)
    {
        this.appTitle = appTitle;
    }

    public String getAppRemark()
    {
        return appRemark;
    }

    public void setAppRemark(String appRemark)
    {
        this.appRemark = appRemark;
    }

    public SortedSet<MenuAuth> getAuths()
    {
        return auths;
    }

    public void setAuths(SortedSet<MenuAuth> auths)
    {
        this.auths = auths;
    }

    public Boolean isHidden()
    {
        return hidden;
    }

    public void setHidden(Boolean hidden)
    {
        this.hidden = hidden;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public String getCondition()
    {
        return condition;
    }

    public void setCondition(String condition)
    {
        this.condition = condition;
    }

    public String getCountCondition()
    {
        return countCondition;
    }

    public void setCountCondition(String countCondition)
    {
        this.countCondition = countCondition;
    }

    public Boolean getValid()
    {
        return valid;
    }

    public void setValid(Boolean valid)
    {
        this.valid = valid;
    }

    public boolean equals(Object o)
    {
        return this == o || o instanceof Menu && menuId.equals(((Menu) o).menuId);
    }

    public int hashCode()
    {
        return menuId.hashCode();
    }

    public String toString()
    {
        return menuTitle;
    }

    public String getDefaultMenuId()
    {
        return defaultMenuId;
    }

    public void setDefaultMenuId(String defaultMenuId)
    {
        this.defaultMenuId = defaultMenuId;
    }

    public Menu getDefaultMenu()
    {
        return defaultMenu;
    }

    public void setDefaultMenu(Menu defaultMenu)
    {
        this.defaultMenu = defaultMenu;
    }

    public static MenuLoader getLoader(String url) throws Exception
    {
        if (url == null)
            return null;

        url = url.trim();
        if (url.endsWith(".class"))
        {
            String className = url.substring(0, url.length() - 6);
            Class c = Class.forName(className);
            return (MenuLoader) Tools.getBean(c);
        }

        return null;
    }

    public static List<MenuAuth> getMenuAuths(String url)
    {
        try
        {
            MenuLoader loader = getLoader(url);
            if (loader != null)
                return loader.getAuths();
        }
        catch (Exception ex)
        {
            //获得默认权限失败，不影响菜单创建
            Tools.log("get auths for " + url + " fail", ex);
        }

        List<MenuAuthsProvider> authProviders = authProvidersProvider.get();

        if (authProviders != null)
        {
            for (MenuAuthsProvider authProvider : authProviders)
            {
                try
                {
                    List<MenuAuth> authList = authProvider.getAuths(url);
                    if (authList != null && authList.size() > 0)
                    {
                        return authList;
                    }
                }
                catch (Exception ex)
                {
                    //获得默认权限失败，不影响菜单创建，并且可以尝试使用下一个Provider
                    Tools.log("get auths for " + url + " fail", ex);
                }
            }
        }

        return null;
    }

    /**
     * 初始化菜单权限
     *
     * @return 初始化后菜单权限是否变化，变化返回true，没有变化返回false
     */
    public boolean initAuths()
    {
        if (!StringUtils.isEmpty(url) && (auths == null || auths.size() == 0))
        {
            List<MenuAuth> authList = getMenuAuths(url);
            if (authList != null)
            {
                int n = authList.size();
                if (n > 0)
                {
                    for (int i = 0; i < n; i++)
                    {
                        MenuAuth auth = authList.get(i);
                        auth.setOrderId(i);
                        auth.setAuthId(null);
                        auth.setMenuId(null);
                    }

                    auths = new TreeSet<MenuAuth>(authList);

                    return true;
                }
            }
        }

        return false;
    }

    @BeforeAdd
    public void beforeAdd() throws Exception
    {
        setCreateTime(new Date());

        //添加菜单之前初始化默认的权限
        initAuths();
    }

    public static void setUpdated() throws Exception
    {
        //数据被修改之后更新菜单更新时间
        UpdateTimeService.updateLastTime("Menu", new Date());
    }
}
