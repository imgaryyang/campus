package com.gzzm.oa.help;

import com.gzzm.platform.menu.Menu;
import com.gzzm.portal.cms.channel.Channel;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

/**
 * 菜单帮助信息
 * @author LDP
 * @date 2017/8/9
 */
@Entity(table = "OAMENUHELPINFO",  keys = "infoId")
public class MenuHelpInfo
{
    /**
     * 主键
     * 可能存在多个菜单共用一个栏目的情况，所有单独应以个自增序列作为主键
     */
    @Generatable(length = 7)
    private Integer infoId;

    /**
     * 所属菜单
     */
    @Unique(message = "oa.help.menuIdRepeat")
    private String menuId;

    @ToOne("MENUID")
    private Menu menu;

    /**
     * 关联栏目
     */
    private Integer channelId;

    @ToOne("CHANNELID")
    private Channel channel;

    public MenuHelpInfo()
    {
    }

    public Integer getInfoId()
    {
        return infoId;
    }

    public void setInfoId(Integer infoId)
    {
        this.infoId = infoId;
    }

    public String getMenuId()
    {
        return menuId;
    }

    public void setMenuId(String menuId)
    {
        this.menuId = menuId;
    }

    public Menu getMenu()
    {
        return menu;
    }

    public void setMenu(Menu menu)
    {
        this.menu = menu;
    }

    public Integer getChannelId()
    {
        return channelId;
    }

    public void setChannelId(Integer channelId)
    {
        this.channelId = channelId;
    }

    public Channel getChannel()
    {
        return channel;
    }

    public void setChannel(Channel channel)
    {
        this.channel = channel;
    }

    @Override
    public boolean equals(Object o)
    {
        if(this == o) return true;
        if(!(o instanceof MenuHelpInfo)) return false;

        MenuHelpInfo that = (MenuHelpInfo) o;

        return infoId != null ? infoId.equals(that.infoId) : that.infoId == null;

    }

    @Override
    public int hashCode()
    {
        return infoId != null ? infoId.hashCode() : 0;
    }
}
