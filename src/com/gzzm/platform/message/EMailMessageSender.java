package com.gzzm.platform.message;

import com.gzzm.platform.commons.Tools;
import net.cyan.commons.util.*;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.*;

/**
 * 通过邮件发送消息的消息发送接口
 *
 * @author camel
 * @date 2010-6-16
 */
public class EMailMessageSender implements MessageSender
{
    /**
     * 短信
     */
    public static final String EMAIL = "email";

    @Inject
    private static Provider<EMailConfig> configProvider;

    public EMailMessageSender()
    {
    }

    public void send(final Message message) throws Exception
    {
        //邮件发送另外启用一个线程，不要由于网络问题影响正常的逻辑
        final String email = message.getEmail();

        if (!StringUtils.isEmpty(email))
        {
            Jobs.run(new Runnable()
            {
                public void run()
                {
                    try
                    {
                        sendMail(message, email);
                    }
                    catch (Throwable ex)
                    {
                        Tools.log("send sms fail," + message, ex);
                    }
                }
            });
        }
    }

    public void sendMail(Message message, String email) throws Exception
    {
        EMailConfig config = configProvider.get();

        if (config != null && !StringUtils.isEmpty(config.getSmtphost()))
        {
            Transport transport = null;

            Properties properties = new Properties();
            properties.put("mail.smtp.host", config.getSmtphost());
            properties.put("mail.smtp.auth", "true");

            Session session = Session.getDefaultInstance(properties);

            try
            {
                MimeMessage mimeMessage = new MimeMessage(session);
                mimeMessage.setFrom(new InternetAddress(config.getFrom()));
                mimeMessage.setRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(email));
                mimeMessage.setSubject(message.getTitle());
                mimeMessage.setText(message.getContent());
                mimeMessage.setSentDate(new Date());
                mimeMessage.saveChanges();

                transport = session.getTransport("smtp");
                transport.connect(config.getSmtphost(), config.getUserName(), config.getPassword());
                transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
                transport.close();
            }
            catch (Throwable ex)
            {
                //发送邮件失败，在控制台提示
                ExceptionUtils.logException("failed to send mail", ex);
            }
            finally
            {
                if (transport != null)
                {
                    try
                    {
                        transport.close();
                    }
                    catch (Throwable ex)
                    {
                        //释放资源
                    }
                }
            }
        }
    }

    public String getType()
    {
        return EMAIL;
    }

}
