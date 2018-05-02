package com.gzzm.safecampus.campus.wx.menu;

import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.menu.WxMenu;
import me.chanjar.weixin.common.bean.menu.WxMenuButton;
import me.chanjar.weixin.common.bean.menu.WxMenuRule;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpMenuService;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import net.cyan.commons.util.DataConvert;
import net.cyan.nest.annotation.Inject;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Neo
 * @date 2018/4/8 12:48
 */
public class MenuService
{
    @Inject
    private MenuDao menuDao;

    public MenuService()
    {
    }

    /**
     * 从原来的菜单分组中拷贝菜单到新的分组
     *
     * @param oldGroupId 原菜单分组Id
     * @param newGroupId 新菜单分组Id
     * @throws Exception 操作异常
     */
    public void copyMenus(Integer oldGroupId, Integer newGroupId) throws Exception
    {
        List<Menu> firstMenus = menuDao.getChildMenu(oldGroupId, 0);
        for (Menu firstMenu : firstMenus)
        {
            Menu newFirstMenu = firstMenu.copy();
            newFirstMenu.setGroupId(newGroupId);
            newFirstMenu.setParentMenuId(0);
            menuDao.add(newFirstMenu);
            for (Menu secondMenu : firstMenu.getChildMenus())
            {
                Menu newSecondMenu = secondMenu.copy();
                newSecondMenu.setGroupId(newGroupId);
                newSecondMenu.setParentMenuId(newFirstMenu.getMenuId());
                menuDao.add(newSecondMenu);
            }
        }
    }

    public void createMenus(Integer[] groupIds, String appId, String appSecret, String domain) throws Exception
    {
        //先删除现有的菜单（包括自定义菜单和个性菜单）
        getMenuService(appId, appSecret).menuDelete();
        for (Integer groupId : groupIds)
        {
            createMenu(groupId, appId, appSecret, domain);
        }
    }

    public void createMenu(Integer groupId, String appId, String appSecret, String domain) throws Exception
    {
        MenuGroup group = menuDao.load(MenuGroup.class, groupId);
        WxMenu wxMenu = new WxMenu();
        List<Menu> menus = menuDao.getChildMenu(groupId, 0);
        List<WxMenuButton> buttons = new ArrayList<>(menus.size());
        for (Menu menu : menus)
        {
            WxMenuButton wxMenuButton = new WxMenuButton();
            wxMenuButton.setType(DataConvert.toString(menu.getType()).toLowerCase());
            wxMenuButton.setName(menu.getMenuName());
            wxMenuButton.setUrl(getUrl(appId, domain, menu));
            List<Menu> subMenus = menuDao.getChildMenu(groupId, menu.getMenuId());
            List<WxMenuButton> subButtons = new ArrayList<>(subMenus.size());
            for (Menu subMenu : subMenus)
            {
                WxMenuButton subButton = new WxMenuButton();
                subButton.setType(DataConvert.toString(subMenu.getType()).toLowerCase());
                subButton.setName(subMenu.getMenuName());
                subButton.setUrl(getUrl(appId, domain, subMenu));
                subButtons.add(subButton);
            }
            wxMenuButton.setSubButtons(subButtons);
            buttons.add(wxMenuButton);
        }
        wxMenu.setButtons(buttons);
        //添加个性菜单的匹配条件
        if (group.getTagId() != null)
        {
            WxMenuRule matchRule = new WxMenuRule();
            matchRule.setTagId(group.getTagId().toString());
            wxMenu.setMatchRule(matchRule);
        }
        getMenuService(appId, appSecret).menuCreate(wxMenu);
    }

    private WxMpMenuService getMenuService(String appId, String appSecret)
    {
        WxMpService service = new WxMpServiceImpl();
        WxMpInMemoryConfigStorage wxConfigProvider = new WxMpInMemoryConfigStorage();
        wxConfigProvider.setAppId(appId);
        wxConfigProvider.setSecret(appSecret);
        service.setWxMpConfigStorage(wxConfigProvider);
        return service.getMenuService();
    }

    private String getUrl(String appId, String domain, Menu menu) throws UnsupportedEncodingException
    {
        if (StringUtils.isEmpty(menu.getUrl()))
            return null;
        //不需要登陆
        if (!menu.isLogin())
        {
            return menu.getUrl().indexOf("http") > 0 ? menu.getUrl() : domain + menu.getUrl();
        } else
        {
            String redirect_uri = domain + "/wxlogin?redirectPath=" + menu.getUrl();
            return String.format(WxMpService.CONNECT_OAUTH2_AUTHORIZE_URL, appId, URLEncoder.encode(redirect_uri, "utf-8"), WxConsts.OAuth2Scope.SNSAPI_USERINFO, "1");
        }
    }
}
