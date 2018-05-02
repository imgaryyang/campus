package com.gzzm.safecampus.wx.user;

import com.gzzm.platform.commons.Tools;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * @author Neo
 * @date 2018/4/8 17:33
 */
public class WxLoginSessionListener implements HttpSessionListener
{
    @Inject
    private static Provider<WxUserOnlineList> wxUserOnlineListProvider;

    public WxLoginSessionListener()
    {
    }

    @Override
    public void sessionCreated(HttpSessionEvent httpSessionEvent)
    {

    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event)
    {
        WxUserOnlineInfo wxUserOnlineInfo = WxUserOnlineInfo.getWxUserOnlineInfo(event.getSession());
        if (wxUserOnlineInfo != null)
        {
            Tools.debug("微信用户超时，id:" + wxUserOnlineInfo.getId());

            if (wxUserOnlineInfo != null)
            {
                wxUserOnlineListProvider.get().remove(wxUserOnlineInfo);
            }
        }
    }
}
