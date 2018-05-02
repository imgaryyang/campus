package com.gzzm.ods.exchange;

/**
 * 收文状态
 *
 * @author camel
 * @date 2010-9-9
 */
public enum ReceiveState
{
    /**
     * 未接收
     */
    noAccepted,

    /**
     * 已接收
     */
    accepted,

    /**
     * 流转中
     */
    flowing,

    /**
     * 处理完毕
     */
    end,

    /**
     * 已经退回
     */
    backed,

    /**
     * 被撤回
     */
    withdrawn,

    /**
     * 已作废
     */
    canceled,
}
