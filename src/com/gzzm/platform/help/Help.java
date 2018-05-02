package com.gzzm.platform.help;

import com.gzzm.platform.commons.UpdateTimeService;
import com.gzzm.platform.menu.*;
import net.cyan.arachne.annotation.Select;
import net.cyan.commons.util.*;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.nest.annotation.Inject;
import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * 系统帮助
 *
 * @author camel
 * @date 2010-12-12
 */
@Entity(table = "PFHELP", keys = "helpId")
public class Help
{
    @Inject
    private static Provider<MenuContainer> menuContainerProvider;

    /**
     * 帮助id
     */
    @Generatable(length = 7)
    private Integer helpId;

    /**
     * 帮助的内容
     */
    @Require
    private String content;

    /**
     * 帮助对应的url；
     */
    @ColumnDescription(type = "varchar(400)")
    private String url;

    /**
     * 帮助对应的应用
     */
    private String appId;

    public Help()
    {
    }

    public Integer getHelpId()
    {
        return helpId;
    }

    public void setHelpId(Integer helpId)
    {
        this.helpId = helpId;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getAppId()
    {
        return appId;
    }

    public void setAppId(String appId)
    {
        this.appId = appId;
    }

    public String getMenuTitle()
    {
        if (StringUtils.isBlank(appId))
            return "";
        else
            return menuContainerProvider.get().getMenu(appId).getTitle();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof Help))
            return false;

        Help help = (Help) o;

        return helpId.equals(help.helpId);
    }

    @Override
    public int hashCode()
    {
        return helpId.hashCode();
    }

    @Override
    public String toString()
    {
        return content;
    }

    public static void setUpdated() throws Exception
    {
        //数据被修改之后更新菜单更新时间
        UpdateTimeService.updateLastTime("Help", new Date());
    }
}
