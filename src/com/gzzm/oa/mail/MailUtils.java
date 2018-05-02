package com.gzzm.oa.mail;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.organ.*;
import com.gzzm.platform.receiver.Receiver;
import net.cyan.commons.util.*;
import net.cyan.commons.util.io.Mime;

import java.util.Date;

/**
 * 邮件相关的工具类
 *
 * @author camel
 * @date 2010-4-23
 */
public final class MailUtils
{
    private MailUtils()
    {
    }

    public static String getFrom(MailBody body)
    {
        return getFrom(body.getMailFrom(), body.getCreateUser());
    }

    public static String getFrom(Mail mail)
    {
        return getFrom(mail.getMailFrom(), mail.getSenderUser());
    }

    private static String getFrom(String from, User user)
    {
        if (user != null)
            return getFrom(user);

        return from;
    }

    public static String getFrom(User user)
    {
        return "\"" + user.getUserName() + "\"<" + user.getUserId() + "@local" + ">";
    }

    public static MailBody reply(Mail mail)
    {
        MailBody body = mail.getBody();
        String from = getFrom(body);

        MailBody newBody = new MailBody();
        newBody.setMailTo(from);
        newBody.setTitle("回复:" + body.getTitle());
        newBody.setContentType(Mime.getHtmlContentType("UTF-8"));

        String replyTitle = Tools.getMessage("oa.mail.replytitle", DateUtils.toString(body.getSendTime()),
                makeMailTo(getFrom(body)));

        StringBuilder buffer = new StringBuilder("&nbsp;<BR><BR>").append(replyTitle);
        buffer.append(
                "<BR>\n<BLOCKQUOTE style=\"padding:1ex 0 1ex 1ex;margin:0 0 0 0.8ex;border-left:#ccc 1px solid\">");
        if (body.getContent() != null)
            buffer.append(body.getContent());
        buffer.append("</BLOCKQUOTE>");

        newBody.setContent(StringUtils.toCharArray(buffer));

        newBody.setReferenceId(mail.getMailId());
        newBody.setRefBodyId(body.getBodyId());

        return newBody;
    }

    public static MailBody forward(MailBody body)
    {
        MailBody newBody = new MailBody();

        newBody.setTitle("Fw:" + body.getTitle());
        newBody.setUrgent(body.isUrgent());
        newBody.setContentType(Mime.getHtmlContentType("UTF-8"));

        StringBuilder buffer = new StringBuilder();
        buffer.append("<BR>----------").append(Tools.getMessage("oa.mail.turntitle")).append("----------")
                .append("<BR>\n");

        buffer.append(Tools.getMessage("oa.mail.from")).append(":").append(makeMailTo(getFrom(body))).append("<BR>\n");

        if (!StringUtils.isEmpty(body.getMailTo()))
            buffer.append(Tools.getMessage("oa.mail.to")).append(":").append(makeMailTo(body.getMailTo()))
                    .append("<BR>\n");

        if (!StringUtils.isEmpty(body.getCc()))
            buffer.append(Tools.getMessage("oa.mail.cc")).append(":").append(makeMailTo(body.getCc()))
                    .append("<BR>\n");

        buffer.append(Tools.getMessage("oa.mail.sent")).append(":").append(DateUtils.toString(body.getSendTime()))
                .append("<BR>\n");

        buffer.append(Tools.getMessage("oa.mail.subject")).append(":").append(body.getTitle()).append("<BR>\n");

        buffer.append("<BR>");

        buffer.append(body.getContent());

        newBody.setContent(StringUtils.toCharArray(buffer));

        return newBody;
    }

    public static String makeMailTo(String addressList)
    {
        StringBuilder buffer = new StringBuilder();
        for (Receiver receiver : Receiver.parseReceiverList(addressList))
        {
            if (buffer.length() > 0)
                buffer.append("&nbsp;");

            buffer.append("<a href=\"mailto:");

            if (receiver.getName().equals(receiver.getValue()))
                buffer.append(receiver.getValue());
            else
                buffer.append(receiver.getName()).append("<").append(receiver.getValue()).append(">");

            buffer.append("\">").append(receiver.getName()).append("</a>");
        }

        return buffer.toString();
    }

    public static MailBody notice(Mail mail, MailAccount account)
    {
        MailBody body = mail.getBody();

        if (!StringUtils.isEmpty(body.getNotificationTo()))
        {
            MailBody newBody = new MailBody();
            newBody.setMailTo(body.getNotificationTo());
            newBody.setTitle(Tools.getMessage("oa.mail.notification_title", body.getTitle()));
            newBody.setContentType("text/plain; charset=UTF-8");
            newBody.setReceipt(true);

            String mailTo;

            if (account == null)
            {
                User user = mail.getUser();
                mailTo = user.getUserName() + "<" + user.getLoginName() + "@" + MailContext.getContext().getDomain() +
                        ">";
            }
            else
            {
                String name = account.getNickName();
                if (StringUtils.isEmpty(name))
                    name = mail.getUser().getUserName();

                mailTo = name + "<" + account.getAddress() + ">";

                newBody.setAccountId(account.getAccountId());
            }

            String sendTime = DateUtils.toString(body.getSendTime());
            String readTime = DateUtils.toString(new Date());
            String title = body.getTitle();

            newBody.setContent(
                    Tools.getMessage("oa.mail.notification", title, sendTime, mailTo, readTime).toCharArray());

            newBody.setReferenceId(mail.getMailId());

            return newBody;
        }

        return null;
    }

    public static void sign(MailBody body, UserInfo userInfo)
    {
        if (userInfo != null && !Null.isNull(body.getSignId()))
        {
            MailSign sign = body.getSign();
            String signContent = sign.getContent();

            if (!StringUtils.isEmpty(signContent))
            {
                signContent = Tools.getMessage(signContent, userInfo);
                signContent = HtmlUtils.escapeHtml(signContent);

                body.setContent(
                        (new String(body.getContent()) +
                                "<br><br><br><div style=\"color:gray;font-size:12px;line-height:18px\">---------------------------------------------<br>" +
                                signContent + "<br>----------------------------------------------</div>")
                                .toCharArray());
            }
        }
    }
}
