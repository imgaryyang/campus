package com.gzzm.platform.message.sms;

/**
 * 短消息回复处理器
 *
 * @author camel
 * @date 2010-6-3
 */
public interface SmsReplyProcessor
{
    public void process(SmsReply reply) throws Exception;
}