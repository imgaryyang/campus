package com.gzzm.safecampus.campus.wx;

import com.gzzm.platform.commons.Tools;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpTemplateMsgService;
import me.chanjar.weixin.mp.api.WxMpUserTagService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;

/**
 * @author Neo
 * @date 2018/3/28 13:55
 */
public class WxMpServiceProvider
{
    /**
     * 微信服务类
     */
    private static WxMpServiceImpl wxMpService;

    private WxMpServiceProvider()
    {
    }

    public static WxMpServiceImpl getWxMpService() throws Exception
    {
        if (wxMpService == null)
        {
            wxMpService = Tools.getBean(WxMpServiceImpl.class);
            WxMpInMemoryConfigStorage wxMpInMemoryConfigStorage = Tools.getBean(WxMpInMemoryConfigStorage.class);
            wxMpService.setWxMpConfigStorage(wxMpInMemoryConfigStorage);
        }
        return wxMpService;
    }

    public static WxMpTemplateMsgService getTemplateMsgService() throws Exception
    {
        return getWxMpService().getTemplateMsgService();
    }

    public static WxMpUserTagService getUserTagService() throws Exception
    {
        return getWxMpService().getUserTagService();
    }
}
