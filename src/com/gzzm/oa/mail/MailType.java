package com.gzzm.oa.mail;

/**
 * 邮件列表所属的类型，包括草稿箱，收件箱，发件箱
 *
 * @author camel
 * @date 2010-3-15
 */
public enum MailType
{
    /**
     * 草稿箱
     */
    draft,

    /**
     * 发件箱
     */
    sended,

    /**
     * 收件箱
     */
    received,

    /**
     * 阅读回执，保存当外部邮件被阅读是返回的回执
     */
    receipt,

    /**
     * 已阅通知，当收到一个要求已阅回执的外部邮件时，发送的已阅通知
     */
    notice
}
