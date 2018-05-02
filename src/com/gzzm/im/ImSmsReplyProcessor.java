package com.gzzm.im;

import com.gzzm.platform.message.sms.*;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

/**
 * 接手机短信回复的消息
 *
 * @author camel
 * @date 2011-2-15
 */
public class ImSmsReplyProcessor implements SmsReplyProcessor
{
    /**
     * 编码为ims开头的短信表示从即时消息发送出去的短信
     *
     * @see com.gzzm.platform.message.Message#code
     * @see SmsReply#code
     */
    public static final String IMS = "IMS";

    @Inject
    private static Provider<ImService> serviceProvider;

    public ImSmsReplyProcessor()
    {
    }

    public void process(SmsReply reply) throws Exception
    {
        ImService service = serviceProvider.get();
        Long messageId = new Long(reply.getCode());
        switch (reply.getType())
        {
            case 0:
                service.setUserMessageReaded(messageId);
                break;
            case 1:
                service.replyUserMessage(reply.getContent(), messageId, MessageType.sms);
                break;
            case 2:
        }
    }
}
