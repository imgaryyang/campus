package com.gzzm.platform.commons.crud;

import com.gzzm.platform.commons.Tools;
import net.cyan.commons.util.StringUtils;

/**
 * 动作
 *
 * @author camel
 * @date 2010-6-8
 */
public class Action
{
    /**
     * 动作对应的提示
     */
    private String title;

    /**
     * 动作对应的动作
     */
    private Object action;

    /**
     * 动作对应的图标
     */
    private Object icon;

    public Action(String title, Object action, Object icon)
    {
        this.title = title;
        this.action = action;
        this.icon = icon;
    }

    public String getTitle()
    {
        return Tools.getMessage(title);
    }

    public Object getIcon()
    {
        return icon;
    }

    public Object getAction()
    {
        return action;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public void setIcon(Object icon)
    {
        this.icon = icon;
    }

    public void setAction(Object action)
    {
        this.action = action;
    }

    public static String getIcon(String name)
    {
        return Tools.getCommonIcon(name);
    }

    public static Action getAction(String title, Object action, Object icon)
    {
        if (action == null)
            return null;

        return new Action(title, action, icon);
    }

    public static Action add(String forward, String title)
    {
        if (StringUtils.isEmpty(title))
            title = "crud.add";

        return getAction(title, Actions.add(forward), getIcon("add"));
    }

    public static Action add(String forward)
    {
        return add(forward, null);
    }

    public static Action edit(String forward, String title)
    {
        if (StringUtils.isEmpty(title))
            title = "crud.modify";

        return getAction(title, Actions.show(forward), getIcon("edit"));
    }

    public static Action edit(String forward)
    {
        return edit(forward, null);
    }

    public static Action delete(String title)
    {
        if (StringUtils.isEmpty(title))
            title = "crud.delete";

        return getAction(title, Actions.delete(), getIcon("delete"));
    }

    public static Action delete()
    {
        return delete(null);
    }

    public static Action up(String title)
    {
        if (StringUtils.isEmpty(title))
            title = "crud.up";

        return getAction(title, Actions.up(), getIcon("up"));
    }

    public static Action up()
    {
        return up(null);
    }

    public static Action down(String title)
    {
        if (StringUtils.isEmpty(title))
            title = "crud.down";

        return getAction(title, Actions.down(), getIcon("down"));
    }

    public static Action down()
    {
        return down(null);
    }

    public static Action more(String title)
    {
        if (StringUtils.isEmpty(title))
            title = "crud.more";

        return getAction(title, Actions.more(), getIcon("more"));
    }

    public static Action more()
    {
        return more(null);
    }
}
