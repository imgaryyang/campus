package com.gzzm.safecampus.campus.pay;

/**
 * 缴费周期
 * @author yuanfang
 * @date 18-02-23 14:47
 */
public enum PaymentCycle
{
    /**
     * 无时间限制
     */
    flex,
    /**
     * 按月
     */
    month,
    /**
     * 按学期
     */
    term,
    /**
     * 按学年
     */
    year,
    /**
     * 其它
     */
    other
}
