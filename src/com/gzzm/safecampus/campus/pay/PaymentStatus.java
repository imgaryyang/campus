package com.gzzm.safecampus.campus.pay;

/**
 * 支付状态
 *
 * @author yuanfang
 * @date 18-02-06 18:19
 */

public enum PaymentStatus
{
    /**
     * 超时
     */
    TimeOut,
    /**
     * 等待
     */
    Wait,
    /**
     * 成功
     */
    Succeed,
    /**
     * 失败
     */
    Fail


}
