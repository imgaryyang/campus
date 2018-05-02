package com.gzzm.oa.mail;

/**
 * smtp发送状态，包括未发送，已发送，发送中
 *
 * @author camel
 * @date 2010-6-14
 */
public enum SmtpSendState
{
    /**
     * 未发送的状态
     */
    notSent,

    /**
     * 正在发送中
     */
    sending,

    /**
     * 已发送的状态
     */
    sent
}
