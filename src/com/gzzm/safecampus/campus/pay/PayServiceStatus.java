package com.gzzm.safecampus.campus.pay;

/**
 * 招行给学校开通的缴费服务
 * 0：未开通 1：已开通 2：已注销
 默认为0

 * @author yuanfang
 * @date 18-03-08 10:06
 */
public enum PayServiceStatus
{
    /**
     * 未开通
     */
    not,
    /**
     *已开通
     */
    opened,
    /**
     * 已注销
     */
    cancel
}
