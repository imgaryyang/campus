package com.gzzm.oa.schedule;

/**
 * 日程提醒类型枚举
 *
 * @author czf
 * @date 2010-3-10
 * @see com.gzzm.oa.schedule.Schedule#remindType
 */
public enum ScheduleRemindType
{
    /**
     * 即时消息提醒，对应数据库中字段的第一位
     */
    im,

    /**
     * 短信提醒，对应数据库中字段的第一位
     */
    sms
}