package com.gzzm.platform.consignation;

/**
 * 委托状态
 *
 * @author camel
 * @date 2010-8-29
 */
public enum ConsignationState
{
    /**
     * 未接受的
     */
    noAccepted,

    /**
     * 有效的，即已接受但未终止
     */
    available,

    /**
     * 终止
     */
    stopped,

    /**
     * 被拒绝
     */
    rejected,

    /**
     * 委托已结束
     */
    end,

    /**
     * 未开始
     */
    notStarted
}
