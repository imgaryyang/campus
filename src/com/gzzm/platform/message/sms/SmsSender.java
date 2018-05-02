package com.gzzm.platform.message.sms;

import net.cyan.nest.annotation.ImplementedBy;

/**
 * 发送手机短信的接口
 *
 * @author camel
 * @date 2010-5-23
 */
@ImplementedBy(BaseSmsSender.class)
public interface SmsSender
{
    /**
     * 发送手机短信
     *
     * @param smsLog 消息对象
     * @throws Exception 发送短信异常
     */
    public void send(SmsLog smsLog) throws Exception;
}
