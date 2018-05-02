package com.gzzm.safecampus.campus.wx.menu;

import com.gzzm.platform.commons.UpdateTimeService;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 微信菜单管理
 *
 * @author Neo
 * @date 2018/4/8 11:43
 */
@Entity(table = "SCMENU", keys = "menuId")
public class Menu
{
    /**
     * 菜单Id
     */
    @Generatable(length = 6)
    private Integer menuId;

    /**
     * 父菜单
     */
    private Integer parentMenuId;

    @NotSerialized
    @ToOne("PARENTMENUID")
    private Menu parentMenu;

    /**
     * 菜单名称
     */
    @ColumnDescription(type = "VARCHAR2(50)")
    private String menuName;

    /**
     * 需要打开的链接
     */
    @ColumnDescription(type = "VARCHAR2(500)")
    private String url;

    /**
     * view表示网页类型，
     * click表示点击类型，
     * miniprogram表示小程序类型
     */
    private MenuType type;

    /**
     * 排序Id
     */
    @ColumnDescription(type = "NUMBER(6)")
    private Integer orderId;

    /**
     * 是否需要登录
     */
    private boolean login;

    /**
     * 所属分组
     */
    private Integer groupId;

    @NotSerialized
    @ToOne("GROUPID")
    private MenuGroup menuGroup;

    /**
     * 子菜单
     */
    @NotSerialized
    @OneToMany("PARENTMENUID")
    private List<Menu> childMenus;

    public Menu()
    {
    }

    public Integer getMenuId()
    {
        return menuId;
    }

    public void setMenuId(Integer menuId)
    {
        this.menuId = menuId;
    }

    public Integer getParentMenuId()
    {
        return parentMenuId;
    }

    public void setParentMenuId(Integer parentMenuId)
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

    public String getMenuName()
    {
        return menuName;
    }

    public void setMenuName(String menuName)
    {
        this.menuName = menuName;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public MenuType getType()
    {
        return type;
    }

    public void setType(MenuType type)
    {
        this.type = type;
    }

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
    }

    public boolean isLogin()
    {
        return login;
    }

    public void setLogin(boolean login)
    {
        this.login = login;
    }

    public Integer getGroupId()
    {
        return groupId;
    }

    public void setGroupId(Integer groupId)
    {
        this.groupId = groupId;
    }

    public MenuGroup getMenuGroup()
    {
        return menuGroup;
    }

    public void setMenuGroup(MenuGroup menuGroup)
    {
        this.menuGroup = menuGroup;
    }

    public List<Menu> getChildMenus()
    {
        return childMenus;
    }

    public void setChildMenus(List<Menu> childMenus)
    {
        this.childMenus = childMenus;
    }

    public Menu copy()
    {
        Menu newMenu = new Menu();
        newMenu.setMenuName(menuName);
        newMenu.setLogin(login);
        newMenu.setOrderId(orderId);
        newMenu.setType(type);
        newMenu.setUrl(url);
        return newMenu;
    }

    @Override
    public String toString()
    {
        return menuName;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof Menu)) return false;

        Menu menu = (Menu) o;

        return menuId.equals(menu.menuId);

    }

    @Override
    public int hashCode()
    {
        return menuId.hashCode();
    }

    public static void setUpdated() throws Exception
    {
        //数据被修改之后更新菜单更新时间
        UpdateTimeService.updateLastTime("WxMenu", new Date());
    }
}
