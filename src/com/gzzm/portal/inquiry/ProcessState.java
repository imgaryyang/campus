package com.gzzm.portal.inquiry;

/**
 * @author camel
 * @date 12-11-6
 */
public enum ProcessState
{
    /**
     * 未受理
     */
    NOACCEPTED,

    /**
     * 处理中
     */
    PROCESSING,

    /**
     * 已回复
     */
    REPLYED,

    /**
     * 已转给其他部门
     */
    TURNED,

    /**
     * 已拒绝
     */
    REJECTED,
}
