package com.gzzm.platform.message.comet;

import com.gzzm.platform.commons.LoginExpireException;
import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.arachne.RequestContext;
import net.cyan.arachne.annotation.*;
import net.cyan.nest.annotation.Inject;

/**
 * 支持comet功能的page
 *
 * @author camel
 * @date 2010-5-29
 */
@Service
public class CometPage
{
    @Inject
    private CometService service;

    private String cometId;

    public CometPage()
    {
    }

    public String getCometId()
    {
        return cometId;
    }

    public void setCometId(String cometId)
    {
        this.cometId = cometId;
    }

    /**
     * 连接，当用户登录时调用
     *
     * @throws Exception 发消息通知客户端错误
     */
    @Comet
    public void connect() throws Exception
    {
        RequestContext requestContext = RequestContext.getContext();

        if (cometId == null)
        {
            UserOnlineInfo userOnlineInfo = UserOnlineInfo.getUserOnlineInfo(requestContext.getRequest());
            if (userOnlineInfo == null)
                throw new LoginExpireException();
            cometId = "user:" + userOnlineInfo.getUserId();
        }

        service.put(new CometInfo(cometId, requestContext.getCometContext()));
    }

    /**
     * 断开，当用户退出时调用
     *
     * @param id 当前comet的id
     */
    @ObjectResult
    @Service(url = "/comet/disconnect/{$0}")
    public void disconnect(String id)
    {
        service.remove(id);
    }

    /**
     * 检查连接是否有效
     *
     * @param id 当前comet的id
     */
    @ObjectResult
    @Service(url = "/comet/ping/{$0}")
    public boolean ping(String id)
    {
        return service.ping(id);
    }
}
