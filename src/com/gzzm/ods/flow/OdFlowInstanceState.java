package com.gzzm.ods.flow;

/**
 * 公文流程实例状态
 *
 * @author camel
 * @date 11-9-22
 */
public enum OdFlowInstanceState
{
    /**
     * 未结束
     */
    unclosed,

    /**
     * 已结束
     */
    closed,

    /**
     * 已撤销
     */
    canceled,

    /**
     * 未开始的
     */
    notStarted,

    /**
     * 已删除
     */
    deleted
}
