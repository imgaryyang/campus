package com.gzzm.safecampus.campus.wx.menu;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.BaseNormalCrud;
import com.gzzm.platform.commons.crud.PageTableView;
import com.gzzm.platform.commons.crud.SimpleDialogView;
import com.gzzm.safecampus.campus.wx.Config;
import com.gzzm.safecampus.campus.wx.tag.TagList;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import net.cyan.arachne.annotation.Select;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.view.components.CButton;
import net.cyan.crud.view.components.CTextArea;
import net.cyan.nest.annotation.Inject;

/**
 * 菜单分组，用于实现自定义菜单和个性化菜单的分组管理
 *
 * @author Neo
 * @date 2018/4/23 12:37
 */
@Service(url = "/campus/wx/menu/menugroup")
public class MenuGroupCrud extends BaseNormalCrud<MenuGroup, Integer>
{
    @Inject
    private MenuService menuService;

    @Inject
    private WxMpInMemoryConfigStorage wxMpInMemoryConfigStorage;

    @Inject
    private Config config;

    private String appId;

    private String appSecret;

    private String domain;

    /**
     * 微信用户标签选择控件
     */
    private TagList tagList;

    public MenuGroupCrud()
    {
    }

    public String getAppId()
    {
        return appId;
    }

    public void setAppId(String appId)
    {
        this.appId = appId;
    }

    public String getAppSecret()
    {
        return appSecret;
    }

    public void setAppSecret(String appSecret)
    {
        this.appSecret = appSecret;
    }

    public String getDomain()
    {
        return domain;
    }

    public void setDomain(String domain)
    {
        this.domain = domain;
    }

    @Select(field = "entity.tagId")
    public TagList getTagList() throws Exception
    {
        if (tagList == null)
            tagList = Tools.getBean(TagList.class);
        return tagList;
    }

    public void setTagList(TagList tagList)
    {
        this.tagList = tagList;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();
        view.addComponent("分组名称", "groupName");
        view.addColumn("分组名称", "groupName");
        view.addColumn("用户标签", "tag.tagName");
        view.addColumn("编辑菜单", new CButton("编辑菜单", "editMenu(${groupId})"));
        view.defaultInit();
        view.addButton("生成菜单", "showCreateMenu()").setIcon(Tools.getCommonIcon("refresh"));
        view.importJs("/safecampus/campus/wx/menu/group.js");
        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();
        view.addComponent("分组名称", "groupName");
        view.addComponent("用户标签", "tagId").setProperty("text","${tag.tagName}");
        view.addComponent("备注", new CTextArea("remark"));
        view.addDefaultButtons();
        return view;
    }

    @Override
    public void afterInsert() throws Exception
    {
        super.afterInsert();
        if (getDuplicateKey() != null)
        {
            //复制后拷贝菜单
            menuService.copyMenus(getDuplicateKey(), getEntity().getGroupId());
        }
    }

    @Service(url = "/campus/wx/menu/showCreateMenu")
    public String showCreateMenu0() throws Exception
    {
        appId = wxMpInMemoryConfigStorage.getAppId();
        appSecret = wxMpInMemoryConfigStorage.getSecret();
        domain = config.getDomain();
        return "/safecampus/campus/wx/menu/createmenu.ptl";
    }

    @Service
    public void createMenu() throws Exception
    {
        menuService.createMenus(getKeys(), appId, appSecret, domain);
    }

    @Override
    public void afterChange() throws Exception
    {
        Menu.setUpdated();
    }
}
