package com.gzzm.oa.mail;

/**
 * 邮件通知方式
 *
 * @author camel
 * @date 13-10-16
 */
public enum MailNotifyType
{
    /**
     * 不短信通知
     */
    NONOTIFY,

    /**
     * 强制短信通知
     */
    NOTIFY,

    /**
     * 根据用户的设置和在线状态决定要不要短信通知
     */
    AUTO
}
