package com.gzzm.oa.mail;

/**
 * 邮件的发送状态
 *
 * @author camel
 * @date 2010-6-18
 */
public enum MailState
{
    /**
     * 未发送
     */
    notSended,

    /**
     * 已发送
     */
    sended,

    /**
     * 已阅读
     */
    readed,

    /**
     * 已回复
     */
    replyed
}
