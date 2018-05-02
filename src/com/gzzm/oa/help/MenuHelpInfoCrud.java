package com.gzzm.oa.help;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.menu.MenuTreeModel;
import com.gzzm.portal.cms.channel.*;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

/**
 * 配置菜单与栏目关联关系
 *
 * @author LDP
 * @date 2017/8/9
 */
@Service(url = "/MenuHelpInfoCrud")
public class MenuHelpInfoCrud extends BaseNormalCrud<MenuHelpInfo, Integer>
{
    @Inject
    private ChannelDao channelDao;

    @Inject
    private static Provider<MenuTreeModel> menuTreeModelProvider;

    private MenuTreeModel menuTree;

    private ChannelTreeModel channelTreeModel;

    private String menuId;

    private Integer channelId;

    public MenuHelpInfoCrud()
    {
        setLog(true);
    }

    public String getMenuId()
    {
        return menuId;
    }

    public void setMenuId(String menuId)
    {
        this.menuId = menuId;
    }

    public Integer getChannelId()
    {
        return channelId;
    }

    public void setChannelId(Integer channelId)
    {
        this.channelId = channelId;
    }

    @Select(field = {"entity.menuId", "menuId"})
    public MenuTreeModel getMenuTree()
    {
        if(menuTree == null)
        {
            menuTree = menuTreeModelProvider.get();
            menuTree.setCheckable(false);
        }

        return menuTree;
    }

    @Select(field = {"entity.channelId", "channelId"})
    public ChannelTreeModel getRelatedChannelTree() throws Exception
    {
        if(channelTreeModel == null)
        {
            channelTreeModel = new ChannelTreeModel();
            channelTreeModel.setShowBox(false);

            Channel channel = channelDao.getChannelByCode("916");
            if(channel != null) {
                channelTreeModel.setRootId(channel.getChannelId());
            }
        }

        return channelTreeModel;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView(true);

        view.addComponent("菜单", "menuId");
        view.addComponent("栏目", "channelId");

        view.addColumn("菜单", "menu.menuTitle");
        view.addColumn("栏目", "channel.channelName");

        view.defaultInit(false);

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("菜单", "menuId").setProperty("text", "${menu == null ? '' : menu.menuTitle}");
        view.addComponent("栏目", "channelId").setProperty("text", "${channel == null ? '' : channel.channelName}");

        view.importCss("/platform/template/template.css");

        view.addDefaultButtons();

        return view;
    }
}
