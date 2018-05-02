package com.gzzm.oa.vote;

/**
 * 投票的重复周期
 *
 * @author camel
 * @date 12-3-26
 */
public enum VotePeriod
{
    /**
     * 不重复
     */
    NONE,

    /**
     * 每周
     */
    WRRK,

    /**
     * 每月
     */
    MONTH,

    /**
     * 每年
     */
    YEAR,

    /**
     * 季度
     */
    QUARTER,

    /**
     * 不定期
     */
    INDEFINITE
}
