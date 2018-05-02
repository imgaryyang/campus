package com.gzzm.platform.message;

import com.gzzm.platform.message.comet.CometService;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.util.Date;

/**
 * 即时消息发送
 *
 * @author camel
 * @date 2010-9-6
 */
public class ImMessageSender implements OnlineMessageSender
{
    /**
     * 短信
     */
    public static final String IM = "im";

    @Inject
    private static Provider<CometService> cometServiceProvider;

    @Inject
    private static Provider<MessageDao> daoProvider;

    public ImMessageSender()
    {
    }

    public void send(Message message) throws Exception
    {
        if (message.getUserId() != null)
        {
            ImMessage im = new ImMessage();

            im.setUserId(message.getUserId());
            im.setContent(message.getMessage());
            im.setSendTime(new Date());
            if (message.getUrls() != null)
                im.setUrl(StringUtils.concat(message.getUrls(), ","));
            im.setReaded(false);

            daoProvider.get().add(im);

            cometServiceProvider.get().sendMessage(im, message.getUserId());
        }
    }

    public String getType()
    {
        return IM;
    }
}
