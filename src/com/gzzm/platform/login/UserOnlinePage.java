package com.gzzm.platform.login;

import net.cyan.arachne.annotation.*;
import net.cyan.nest.annotation.Inject;

/**
 * 在线用户相关的服务
 *
 * @author camel
 * @date 2016/7/5
 */
@Service
public class UserOnlinePage
{
    @Inject
    private UserOnlineList userOnlineList;

    public UserOnlinePage()
    {
    }

    @PlainText
    @Service(url = "/userOnline/size")
    public int size()
    {
        return userOnlineList.size();
    }
}
