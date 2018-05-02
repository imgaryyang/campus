package com.gzzm.platform.organ.export;

import com.gzzm.platform.menu.*;

import java.io.Serializable;
import java.util.*;

/**
 * @author camel
 * @date 13-12-26
 */
public class ExportMenu implements Serializable
{
    private static final long serialVersionUID = -7708504423495799594L;

    private String menuId;

    private String menuTitle;

    private String url;

    private String hint;

    private String appTitle;

    private String appRemark;

    private byte[] icon;

    private boolean hidden;

    private String condition;

    private String countCondition;

    private List<ExportMenuAuth> auths;

    private List<ExportMenu> children;

    private String group;

    public ExportMenu()
    {
    }

    public ExportMenu(Menu menu, String group)
    {
        this.group = group;

        menuId = menu.getMenuId();
        menuTitle = menu.getMenuTitle();
        url = menu.getUrl();
        hint = menu.getHint();
        appTitle = menu.getAppTitle();
        appRemark = menu.getAppRemark();
        icon = menu.getIcon();
        hidden = menu.isHidden() != null && menu.isHidden();
        condition = menu.getCondition();
        countCondition = menu.getCountCondition();

        children = new ArrayList<ExportMenu>();
        for (Menu childMenu : menu.getChildMenus())
        {
            children.add(new ExportMenu(childMenu, group));
        }

        auths = new ArrayList<ExportMenuAuth>();
        for (MenuAuth auth : menu.getAuths())
        {
            auths.add(new ExportMenuAuth(auth));
        }
    }

    void getMenuIds(List<String> menuIds)
    {
        menuIds.add(menuId);

        if (children != null)
        {
            for (ExportMenu child : children)
            {
                child.getMenuIds(menuIds);
            }
        }
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

    public byte[] getIcon()
    {
        return icon;
    }

    public void setIcon(byte[] icon)
    {
        this.icon = icon;
    }

    public boolean isHidden()
    {
        return hidden;
    }

    public void setHidden(boolean hidden)
    {
        this.hidden = hidden;
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

    public List<ExportMenuAuth> getAuths()
    {
        return auths;
    }

    public void setAuths(List<ExportMenuAuth> auths)
    {
        this.auths = auths;
    }

    public List<ExportMenu> getChildren()
    {
        return children;
    }

    public void setChildren(List<ExportMenu> children)
    {
        this.children = children;
    }

    public String getGroup()
    {
        return group;
    }

    public void setGroup(String group)
    {
        this.group = group;
    }
}
