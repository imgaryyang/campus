package com.gzzm.safecampus.campus.wx.message;

/**
 * 模板消息类型
 *
 * @author Neo
 * @date 2018/4/2 13:52
 */
public enum TemplateMessageType
{
    /**
     * 考勤模板消息
     */
    ATTENDANCE,

    /**
     * 待缴账单模板消息
     */
    UNPAIDBILL,

    /**
     * 已缴账单模板消息
     */
    PAIDBILL,

    /**
     * 成绩模板消息
     */
    SCORE

}
