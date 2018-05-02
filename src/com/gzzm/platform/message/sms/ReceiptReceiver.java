package com.gzzm.platform.message.sms;

import java.util.Collection;

/**
 * 手机短信回执接收器，从短信服务器接收短信回执
 *
 * @author camel
 * @date 2010-5-23
 */
public interface ReceiptReceiver
{
    /**
     * 接收短信回执
     * @return 短信回执列表
     * @throws Exception 允许实现类抛出异常
     */
    public Collection<ReceiptInfo> receive() throws Exception;
}