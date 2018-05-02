package com.gzzm.oa.schedule;

/**
 * 日程标志枚举
 * 各类型在前台的显示方式在/WEB-INF/resources/oa/schedule_zh-cn.properties中定义
 *
 * @author czf
 * @date 2010-3-10
 * @see com.gzzm.oa.schedule.Schedule#tag
 */
public enum ScheduleTag
{
    /**
     * 用户日常，表示此日常为个人所有，由个人进行维护和查看
     */
    user,

    /**
     * 部门日常，表示此日常为部门所有，由部门管理员进行维护，参与者可以查看
     */
    dept
}
