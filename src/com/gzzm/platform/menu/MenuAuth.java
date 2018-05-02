package com.gzzm.platform.menu;

import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

import java.util.*;

/**
 * 菜单权限表，记录每个菜单拥有哪些子权限
 *
 * @author camel
 * @date 2009-12-21
 */
@Entity(table = "PFMENUAUTH", keys = "authId")
public class MenuAuth implements Comparable<MenuAuth>
{
    /**
     * 权限ID
     */
    @Generatable(length = 8)
    private Integer authId;

    /**
     * 菜单ID
     */
    @ColumnDescription(type = "varchar(50)", nullable = false)
    private String menuId;

    /**
     * 关联的菜单对象
     */
    @NotSerialized
    @Cascade
    private Menu menu;

    /**
     * 权限代码
     */
    @Require
    @Unique(with = "menuId")
    @ColumnDescription(type = "varchar(50)", nullable = false)
    private String authCode;

    /**
     * 权限名称
     */
    @Require
    @Unique(with = "menuId")
    @ColumnDescription(type = "varchar(250)")
    private String authName;

    /**
     * 排序，对同一个菜单的权限做排序
     */
    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    /**
     * 菜单权限关联的url对象
     */
    @NotSerialized
    private List<MenuAuthUrl> authUrls;

    public MenuAuth()
    {
    }

    public Integer getAuthId()
    {
        return authId;
    }

    public void setAuthId(Integer authId)
    {
        this.authId = authId;
    }

    public String getMenuId()
    {
        return menuId;
    }

    public void setMenuId(String menuId)
    {
        this.menuId = menuId;
    }

    public String getAuthCode()
    {
        return authCode;
    }

    public void setAuthCode(String authCode)
    {
        this.authCode = authCode;
    }

    public String getAuthName()
    {
        return authName;
    }

    public void setAuthName(String authName)
    {
        this.authName = authName;
    }

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
    }

    public Menu getMenu()
    {
        return menu;
    }

    public void setMenu(Menu menu)
    {
        this.menu = menu;
    }

    public List<MenuAuthUrl> getAuthUrls()
    {
        return authUrls;
    }

    public void setAuthUrls(List<MenuAuthUrl> authUrls)
    {
        this.authUrls = authUrls;
    }

    @AfterAdd
    @AfterUpdate
    public void afterModify() throws Exception
    {
        Menu.setUpdated();
    }

    @SuppressWarnings("CloneDoesntCallSuperClone")
    @Override
    public MenuAuth clone() throws CloneNotSupportedException
    {
        MenuAuth c = new MenuAuth();
        c.setAuthCode(authCode);
        c.setAuthName(authName);
        c.setOrderId(orderId);

        List<MenuAuthUrl> urls = getAuthUrls();
        if (urls != null && urls.size() > 0)
        {
            List<MenuAuthUrl> urls2 = new ArrayList<MenuAuthUrl>(urls.size());
            for (MenuAuthUrl url : urls)
                urls2.add(new MenuAuthUrl(url.getUrlName(), url.getUrl()));

            c.setAuthUrls(urls2);
        }

        return c;
    }

    public int compareTo(MenuAuth o)
    {
        if (menuId != null && o.menuId != null)
        {
            int c = menuId.compareTo(o.menuId);
            if (c != 0)
                return c;
        }

        if (orderId != null && o.orderId != null)
        {
            int c = orderId.compareTo(o.orderId);
            if (c != 0)
                return c;
        }

        if (authId != null && o.authId != null)
            return authId.compareTo(o.authId);

        return 0;
    }

    @Override
    public boolean equals(Object o)
    {
        return this == o || o instanceof MenuAuth && authId.equals(((MenuAuth) o).authId);
    }

    @Override
    public int hashCode()
    {
        return authId.hashCode();
    }

    @Override
    public String toString()
    {
        return authName;
    }
}
