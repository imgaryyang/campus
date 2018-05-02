package com.gzzm.platform.message.sms;

/**
 * 短信回执处理器
 *
 * @author camel
 * @date 2010-5-23
 */
public interface SmsReceiptProcessor
{
    /**
     * 处理一条回执
     *
     * @param receipt 回执
     * @param service 短信service，集成了和短信操作相关的方法
     * @throws Exception 允许实现类抛出异常
     */
    public void process(ReceiptInfo receipt, SmsService service) throws Exception;
}