package com.gzzm.oa.notice;

/**
 * 信息发布类型枚举
 * 发布类型在前台的显示方式在/WEB-INF/resources/oa/notice_zh-cn.properties中定义
 *
 * @author czf
 * @date 2010-3-16
 * @see com.gzzm.oa.notice.Notice#type
 */
public enum InfoType
{
    /**
     * 编辑
     */
    edit,

    /**
     * URL
     */
    url,

    file
}
