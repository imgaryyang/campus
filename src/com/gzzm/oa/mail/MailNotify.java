package com.gzzm.oa.mail;

import com.gzzm.platform.annotation.Listener;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.message.*;
import com.gzzm.platform.message.sms.SmsMessageSender;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

/**
 * 邮件通知
 *
 * @author camel
 * @date 2011-4-25
 */
@Listener
public class MailNotify
{
    @Inject
    private static Provider<MailDao> daoProvider;

    public MailNotify()
    {
    }

    /**
     * 收到邮件时的通知
     *
     * @param mail 要通知的邮件
     * @throws Exception 邮件通知错误
     */
    @Listener("receive")
    public void notify(Mail mail) throws Exception
    {
        MailNotifyType notifyType = mail.getBody().getNotifyType();

        Message message = new Message();

        message.setApp("mail");
        message.setSender(mail.getSender());
        message.setUserId(mail.getUserId());
        message.setContent(Tools.getMessage("oa.mail.notify", mail));

        if (mail.getCatalogId() == null)
        {
            message.setUrls("/oa/mail/list?type=received", "/oa/mail/list?type=received&showReply=false");
        }
        else
        {
            message.setUrl("/oa/mail/list");
        }

        if (notifyType == MailNotifyType.NONOTIFY)
        {
            message.setMethods(ImMessageSender.IM);
        }
        else if (notifyType == MailNotifyType.NOTIFY)
        {
            message.setMethods(ImMessageSender.IM, SmsMessageSender.SMS);
            message.setForce(true);
        }

        message.send();

        Mail mail1 = new Mail();
        mail1.setMailId(mail.getMailId());
        mail1.setNotified(true);
        daoProvider.get().update(mail1);
    }
}
