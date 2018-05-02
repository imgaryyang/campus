package com.gzzm.platform.message.sms;

/**
 * 短信发送状态
 *
 * @author camel
 * @date 2010-5-23
 */
public enum SmsState
{
    /**
     * 未发送到短信服务
     */
    notsended,

    /**
     * 发送到短信服务
     */
    sended,

    /**
     * 已发送到短信服务，并且收信人已接收
     */
    accepted,

    /**
     * 发送失败
     */
    error,

    /**
     * 已取消发送
     */
    canceled
}
