package com.gzzm.oa.mail;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.receiver.Receiver;
import net.cyan.commons.util.DateUtils;
import net.cyan.mail.*;

import java.util.*;

/**
 * smtp邮件发送的服务
 *
 * @author camel
 * @date 2010-6-14
 */
public class SmtpSendService extends MailServiceBase
{
    public SmtpSendService()
    {
    }

    public void send() throws Exception
    {
        while (true)
        {
            Date now = new Date();
            List<Long> mailIds = dao.getMailIdsForSmtpSend(DateUtils.truncate(DateUtils.addDate(now, -7)),
                    DateUtils.addHour(now, -2));
            if (mailIds.size() == 0)
                break;

            for (final Long mailId : mailIds)
            {
                Tools.run(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            Tools.getBean(SmtpSendService.class).send(mailId);
                        }
                        catch (Throwable ex)
                        {
                            //发送一个邮件失败，不影响下一个邮件发送
                            Tools.log("send mail fail:" + mailId, ex);
                        }
                    }
                });
            }
        }
    }

    public void send(Long mailId) throws Exception
    {
        send(dao.getMail(mailId));
    }

    public void send(Mail mail) throws Exception
    {
        //标识邮件正在发送中，防止重复发送
        mail.setSmtpSendState(SmtpSendState.sending);
        dao.update(mail);

        try
        {
            List<MailSmtpRecord> records = dao.getSmtpRecordsForSend(mail.getBodyId());

            StringBuilder mailTo = new StringBuilder();
            for (MailSmtpRecord record : records)
            {
                if (mailTo.length() > 0)
                    mailTo.append(",");
                mailTo.append(record.getMailTo());
            }

            //调用cyan.mail接口发送邮件
            MailContext context = MailContext.getContext();
            MailServer server = context.getServer();

            MailContent content = server.getDao().getMailContent(mail.getBodyId().toString(), 0);
            content.setNotificationTo(null);

            MailSender sender = new MailSender();
            sender.setContent(content);
            sender.setMailTo(mailTo.toString());

            MailAccount account = mail.getBody().getAccount();
            if (account != null)
            {
                sender.setSmtpServer(account.getSmtpServer());
                sender.setUserName(account.getUserName());
                sender.setPassword(account.getPassword());
                sender.setSendFrom(account.getAddress());
            }
            else
            {
                sender.setSendFrom(mail.getUser().getLoginName() + "@" + context.getDomain());
            }

            List<MailFail> fails = sender.send(server);

            //更新smtp发送记录的状态
            boolean sent = true;
            for (MailSmtpRecord record : records)
            {
                String error = null;
                if (fails != null)
                {
                    String receiver = new Receiver(record.getMailTo()).getValue();
                    for (MailFail fail : fails)
                    {
                        if (receiver.equals(fail.getReceiver()))
                        {
                            error = fail.getError();
                            if (error == null && fail.getEx() != null)
                                error = fail.getEx().getDisplayMessage();
                            break;
                        }
                    }
                }

                if (error == null)
                {
                    //发送成功
                    record.setState(MailState.sended);
                    record.setError("");
                }
                else
                {
                    //记录发送失败错误
                    record.setError(error);
                    sent = false;
                }

                record.setSendTime(new Date());
                dao.update(record);
            }

            //发送完毕，标识发送完成
            mail.setSmtpSendState(sent ? SmtpSendState.sent : SmtpSendState.notSent);
            if (!sent)
                mail.setSmtpErrorTime(new Date());

            dao.update(mail);
        }
        catch (Throwable ex)
        {
            //发送错误，下次继续发送
            mail.setSmtpSendState(SmtpSendState.notSent);
            dao.update(mail);

            if (ex instanceof Exception)
                throw (Exception) ex;
            else if (ex instanceof Error)
                throw (Error) ex;
        }
    }
}
