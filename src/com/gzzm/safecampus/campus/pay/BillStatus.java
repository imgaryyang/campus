package com.gzzm.safecampus.campus.pay;

/**
 * 账单状态
 * @author yuanfang
 * @date 18-02-06 18:17
 */

public enum BillStatus
{
    /**
     * 等待
     */
    Wait,
    /**
     * 已完成
     */
    Finnish,
    /**
     * 失败
     */
    Fail,
    /**
     * 过期
     */
    Expired,
    /**
     * 取消
     */
    Cancel

}
