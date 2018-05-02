package com.gzzm.im;

import com.gzzm.platform.message.comet.*;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

/**
 * 即时消息登录，离线监听
 *
 * @author camel
 * @date 2011-2-10
 */
public class ImCometListener implements CometListener
{
    @Inject
    private static Provider<ImService> serviceProvider;

    public ImCometListener()
    {
    }

    public void connect(CometInfo cometInfo, CometService service) throws Exception
    {
        if (cometInfo.getCometId().startsWith("user:"))
        {
            serviceProvider.get().notifyFriends(Integer.valueOf(cometInfo.getCometId().substring(5)),
                    FriendNotifyType.online);
        }
    }

    public void disconnect(CometInfo cometInfo, CometService service) throws Exception
    {
        if (cometInfo.getCometId().startsWith("user:"))
        {
            Integer userId = Integer.valueOf(cometInfo.getCometId().substring(5));
            if (!service.isOnline(userId))
                serviceProvider.get().notifyFriends(userId, FriendNotifyType.offline);
        }
    }
}
