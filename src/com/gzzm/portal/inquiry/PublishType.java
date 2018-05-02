package com.gzzm.portal.inquiry;

/**
 * 咨询投诉处理完之后的发布方式，由后台设置
 *
 * @author camel
 * @date 12-11-6
 * @see Inquiry#publicity
 * @see Inquiry#publishType
 */
public enum PublishType
{
    /**
     * 完全公开，当咨询人选择密码查看时不能选择此项
     */
    PUBLICITY,

    /**
     * 仅对申请人可见
     */
    INQUIRER_ONLY,

    /**
     * 完全不可见，不在外网显示
     */
    HIDDEN
}
