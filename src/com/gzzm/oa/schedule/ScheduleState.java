package com.gzzm.oa.schedule;

/**
 * 日程状态枚举
 * 各状态在前台的显示方式在/WEB-INF/resources/oa/schedule_zh-cn.properties中定义
 *
 * @author czf
 * @date 2010-3-10
 * @see com.gzzm.oa.schedule.Schedule#state
 */
public enum ScheduleState
{
    /**
     * 未开始
     */
    notStarted,

    /**
     * 进行中
     */
    going,

    /**
     * 已结束
     */
    closed,

    /**
     * 已经取消
     */
    canceled
}