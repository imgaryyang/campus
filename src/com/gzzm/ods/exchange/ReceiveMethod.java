package com.gzzm.ods.exchange;

/**
 * 收文方式
 *
 * @author camel
 * @date 11-10-11
 */
public enum ReceiveMethod
{
    /**
     * 系统发送的公文
     */
    system,

    /**
     * 手工录入的公文
     */
    manual,

    /**
     * 从外部系统传入的数据，即通过接口程序获得的数据
     */
    outter
}
