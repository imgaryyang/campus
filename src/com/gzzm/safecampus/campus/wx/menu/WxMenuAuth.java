package com.gzzm.safecampus.campus.wx.menu;

import com.gzzm.safecampus.campus.wx.tag.Tag;
import com.gzzm.safecampus.campus.wx.tag.TagDao;
import com.gzzm.safecampus.wx.user.IdentifyType;
import com.gzzm.safecampus.wx.user.WxUserOnlineInfo;
import net.cyan.commons.util.Provider;
import net.cyan.commons.util.io.WebUtils;
import net.cyan.nest.annotation.Inject;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

/**
 * 微信菜单权限
 *
 * @author Neo
 * @date 2018/4/24 23:44
 */
public class WxMenuAuth
{
    @Inject
    private static Provider<WxMenuContainer> wxMenuContainerProvider;

    @Inject
    private static Provider<TagDao> tagDaoProvider;

    public WxMenuAuth()
    {
    }

    /**
     * 微信菜单权限验证
     * 仅对菜单进行校验，不进行功能级别的验证
     *
     * @param request request
     * @return true为通过 false不通过
     * @throws Exception
     */
    public boolean accept(HttpServletRequest request) throws Exception
    {
        String url = WebUtils.getRequestURI(request);
        //默认的菜单分组（拥有全部的菜单）
        Collection<String> defaultUrls = wxMenuContainerProvider.get().getTagUrls(-1);
        if (defaultUrls.contains(url))
        {
            //获取当前用户的身份标签
            IdentifyType identifyType = WxUserOnlineInfo.getWxUserOnlineInfo(request).getIdentifyType();
            Tag tag = tagDaoProvider.get().loadTag(identifyType);
            if (tag != null)
            {
                //当前用户标签对应的菜单
                Collection<String> tagUrls = wxMenuContainerProvider.get().getTagUrls(tag.getTagId());
                return tagUrls.contains(url);
            }
        } else
        {
            return true;
        }
        return false;
    }
}
