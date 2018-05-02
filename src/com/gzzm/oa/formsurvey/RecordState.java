package com.gzzm.oa.formsurvey;

/**
 * @author camel
 * @date 13-4-23
 */
public enum RecordState
{
    /**
     * 未保存
     */
    NOSAVED,

    /**
     * 未提交
     */
    NOSUBMITED,

    /**
     * 已提交，未审核
     */
    SUBMITED,

    /**
     * 已审核通过
     */
    PASSED,

    /**
     * 审核不通过
     */
    NOPASSED
}
