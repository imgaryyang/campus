package com.gzzm.platform.message.sms;

import com.gzzm.platform.message.Message;

/**
 * 支持移动办公短信功能的短消息发送
 * @author camel
 * @date 2010-5-23
 */
public class OfficeSmsMessageSender extends SmsMessageSender
{
    public OfficeSmsMessageSender()
    {
    }

    @Override
    public void send(Message message) throws Exception
    {
        super.send(message);
    }
}
