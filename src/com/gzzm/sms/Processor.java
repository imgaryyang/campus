package com.gzzm.sms;

/**
 * 短消息处理
 *
 * @author camel
 * @date 2010-11-22
 */
public interface Processor
{
    /**
     * 处理消息回执
     *
     * @param mt SmsMt对象，对应数据库中的SmsMt表
     * @throws Exception 处理错误
     */
    public void processReport(SmsMt mt) throws Exception;

    /**
     * 处理接收到的短信
     *
     * @param mo SmsMo对象，对应数据库中的SmsMt表
     * @throws Exception 处理错误
     */
    public void processReceive(SmsMo mo) throws Exception;
}