package com.gzzm.platform.login;

import com.gzzm.platform.commons.Tools;
import net.cyan.arachne.*;

/**
 * @author camel
 * @date 2018/4/8
 */
public class RequestLoginSessionListener implements RequestSessionListener
{
    public RequestLoginSessionListener()
    {
    }

    @Override
    public void sessionDestroyed(RequestSession session) throws Exception
    {
        UserOnlineInfo userOnlineInfo =
                UserOnlineInfo.getUserOnlineInfo((String) session.get(UserOnlineInfo.SESSIONNAME_ID));

        if (userOnlineInfo != null)
        {
            Tools.debug("用户超时，id:" + userOnlineInfo.getId());

            if (userOnlineInfo != null)
                LoginTask.logout(userOnlineInfo, LoginAction.expire);
        }
    }
}
