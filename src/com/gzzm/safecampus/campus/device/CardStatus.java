package com.gzzm.safecampus.campus.device;

/**
 * 招行小白卡状态
 *
 * @author yiuman
 * @date 2018/3/14
 */
public enum CardStatus
{

    /**
     * 未激活
     */
    UNACTIVATED,

    /**
     * 已激活
     */
    ACTIVE,
    /**
     * 已停用、挂失后为此状态
     */
    DISABLED,
    /**
     * 已退卡、
     */
    RETIRED,
    /**
     * 已补办、补卡后。老卡状态为已补办
     */
    REFUNDED,
    /**
     * 已过期
     */
    EXPIRED
}
