package com.gzzm.platform.message;

/**
 * 消息发送器，定义如何发送一个消息
 *
 * @author camel
 * @date 2010-5-20
 */
public interface MessageSender
{
    /**
     * 发送消息
     *
     * @param message 消息
     * @throws Exception 发送消息错误
     */
    public void send(Message message) throws Exception;

    /**
     * 消息发送器的类型，消息发送器的唯一识别标志，如im,sms,email
     * 即Message.methods的可取值
     *
     * @return 消息的类型
     * @see com.gzzm.platform.message.Message#methods
     * @see com.gzzm.platform.message.UserMessageConfig#defaultMethods
     * @see com.gzzm.platform.message.UserMessageConfig#methods
     */
    public String getType();
}
