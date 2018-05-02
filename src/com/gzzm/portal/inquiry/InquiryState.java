package com.gzzm.portal.inquiry;

/**
 * 处理状态
 *
 * @author camel
 * @date 12-11-6
 */
public enum InquiryState
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
     * 已拒绝
     */
    REJECTED,
}
