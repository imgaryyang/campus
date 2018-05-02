package com.gzzm.platform.message;

import com.gzzm.platform.annotation.UserId;
import com.gzzm.platform.commons.LoginExpireException;
import net.cyan.arachne.annotation.*;
import net.cyan.nest.annotation.Inject;

import java.util.List;

/**
 * 即时消息相关的访问入口
 *
 * @author camel
 * @date 2011-4-26
 */
@Service
public class ImMessagePage
{
    @Inject
    private MessageDao dao;

    @UserId
    private Integer userId;

    public ImMessagePage()
    {
    }

    @Service(url = "/message/im/noreaded")
    public List<ImMessage> getNoReadMessages() throws Exception
    {
        if (userId == null)
            throw new LoginExpireException();
        return dao.getNoReadedImMessages(userId);
    }

    @ObjectResult
    @Service(url = "/message/im/{$0}/read")
    public void setReaded(Long messageId) throws Exception
    {
        dao.setImMessageReaded(messageId);
    }
}
