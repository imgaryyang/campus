package com.gzzm.oa.mail;

import com.gzzm.platform.commons.*;
import com.gzzm.platform.commons.event.Events;
import com.gzzm.platform.organ.UserInfo;
import com.gzzm.platform.receiver.Receiver;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.StringUtils;
import net.cyan.commons.util.io.Mime;

import java.util.*;

/**
 * 邮件相关的一些服务API
 *
 * @author camel
 * @date 2010-3-17
 */
public class MailService extends MailServiceBase
{
    public MailService()
    {
    }

    public long getCapacity(Integer userId) throws Exception
    {
        return getConfig(userId).getCapacity();
    }

    public long getWastage(Integer userId) throws Exception
    {
        return dao.getWastage(userId);
    }

    public boolean isCapacityFull(Integer userId) throws Exception
    {
        return getCapacity(userId) <= getWastage(userId);
    }

    public Mail getMail(Long mailId) throws Exception
    {
        return dao.getMail(mailId);
    }

    public MailBody getBody(Long bodyId) throws Exception
    {
        return dao.getBody(bodyId);
    }

    public void deleteBody(Long bodyId) throws Exception
    {
        dao.deleteBody(bodyId);
    }

    public List<MailAccount> getAccounts(Integer userId) throws Exception
    {
        return dao.getAccounts(userId);
    }

    public List<MailAccount> getAccountsForSmtp(Integer userId) throws Exception
    {
        return dao.getAccountsForSmtp(userId);
    }

    public Long createMail(MailBody body, Integer userId) throws Exception
    {
        return saveMail(body, null, userId);
    }

    /**
     * 保存邮件
     *
     * @param body   邮件体
     * @param mailId 邮件ID，当新增邮件是为null
     * @param userId 用户ID
     * @return 邮件ID
     * @throws Exception 数据库错误，或者用户没有权限操作此邮件
     */
    public Long saveMail(MailBody body, Long mailId, Integer userId) throws Exception
    {
        body.setUpdateTime(new Date());

        if (mailId == null)
        {
            if (body.getContentType() == null)
                body.setContentType(Mime.getHtmlContentType("UTF-8"));

            if (body.getMessageId() == null)
            {
                String messageId = Tools.getUUID();

                MailContext context = MailContext.getContext();
                if (context != null)
                {
                    String domain = context.getDomain();
                    if (domain != null)
                        messageId += "@" + domain;
                }

                body.setMessageId(messageId);
            }

            if (body.getReferenceId() != null)
            {
                //设置邮件体的references属性
                MailBody oBody = getMail(body.getReferenceId()).getBody();

                String s = "<" + oBody.getMessageId() + ">";

                String references = oBody.getMailReferences();
                if (StringUtils.isEmpty(references))
                    references = s;
                else
                    references = s + " " + references;

                body.setMailReferences(references);
                body.setInReplyTo(oBody.getMessageId());
                body.setRefBodyId(oBody.getBodyId());
            }

            return dao.createMail(body, userId);
        }

        Mail mail = dao.getMail(mailId);
        if (!mail.getUserId().equals(userId))
        {
            //不能操作别人的邮件
            throw new SystemMessageException(Messages.NO_AUTH_RECORD,
                    "no auth," + ",mailId:" + mail + ",userId:" + userId);
        }

        dao.updateMail(body, mailId);

        return mailId;
    }

    /**
     * 发送邮件
     *
     * @param mailId 要发送的邮件的ID
     * @return 如果全部发给内部邮箱，返回false，如果有外部邮箱，返回true
     * @throws Exception 发送邮件异常，一般是写邮件记录到数据库中错误
     */
    @Transactional
    public boolean sendMail(Long mailId, UserInfo userInfo) throws Exception
    {
        boolean result = false;

        Mail mail = dao.getMail(mailId);

        Date time = new Date();

        mail.setSendTime(time);

        if (mail.getType() == MailType.draft)
            mail.setType(MailType.sended);

        mail.setSmtpSendState(SmtpSendState.sent);

        MailBody body = mail.getBody();
        body.setSendTime(time);
        body.setUpdateTime(time);

        MailUtils.sign(body, userInfo);

        dao.update(body);

        StringBuilder receivers = new StringBuilder();

        if (!StringUtils.isEmpty(body.getMailTo()))
            receivers.append(body.getMailTo());

        if (!StringUtils.isEmpty(body.getCc()))
            StringUtils.concat(receivers, body.getCc(), ";");

        if (!StringUtils.isEmpty(body.getSc()))
            StringUtils.concat(receivers, body.getSc(), ";");

        //记录已经发送过的邮件，防止重复发送
        Set<String> sendeds = new HashSet<String>();

        for (Receiver receiver : Receiver.parseReceiverList(receivers.toString()))
        {
            if (!sendeds.contains(receiver.getValue()))
            {
                //每个接收者如果在收件人，抄送和密送中重复出现，只发送一次
                sendeds.add(receiver.getValue());

                if (receiver.getId() != null)
                {
                    //本地接收者
                    sendMailTo(body, Integer.valueOf(receiver.getId()), time);
                }
                else
                {
                    //外部接收者
                    MailSmtpRecord record = new MailSmtpRecord();
                    record.setBodyId(body.getBodyId());
                    record.setMailTo(receiver.getName().equals(receiver.getValue()) ? receiver.getValue() :
                            "\"" + receiver.getName() + "\"<" + receiver.getValue() + ">");
                    record.setState(MailState.notSended);

                    dao.add(record);

                    //有外部邮箱接收者，设置未发送smtp，等待发送任务发送之
                    mail.setSmtpSendState(SmtpSendState.notSent);

                    result = true;
                }

                //记录邮件关系，以方便往来邮件查询
                MailRelationShip relationShip = new MailRelationShip();
                relationShip.setMailId(mail.getMailId());
                relationShip.setUserId(mail.getUserId());
                relationShip.setRefAddress(receiver.getValue());
                dao.add(relationShip);
            }
        }

        dao.update(mail);

        if (body.getReferenceId() != null)
        {
            //设置邮件的已回复状态
            Mail referenceMail = dao.getMail(body.getReferenceId());

            if (referenceMail.isReplyed() == null || !referenceMail.isReplyed())
            {
                referenceMail.setReplyed(true);
                dao.update(referenceMail);
            }
        }

        return result;
    }

    private void sendMailTo(MailBody body, Integer userId, Date time) throws Exception
    {
        Mail mail = new Mail();

        mail.setBodyId(body.getBodyId());
        mail.setUserId(userId);
        mail.setAcceptTime(new Date());
        mail.setType(MailType.received);

        mail.setTitle(body.getTitle());
        mail.setMailFrom(body.getMailFrom());
        mail.setSender(body.getCreator());
        mail.setSendTime(time);
        mail.setMailSize(body.getMailSize());
        mail.setDeleted(false);
        mail.setUrgent(body.isUrgent());
        mail.setAttachment(body.getAttachmentId() != null);

        dao.autoCatalog(mail);

        dao.add(mail);

        //记录邮件关系，以方便往来邮件查询
        MailRelationShip relationShip = new MailRelationShip();
        relationShip.setMailId(mail.getMailId());
        relationShip.setUserId(userId);
        relationShip.setRefAddress(mail.getSender() + "@local");
        dao.add(relationShip);

        Events.invoke(dao.getMail(mail.getMailId()), "receive");
    }

    /**
     * 创建邮件并发送
     *
     * @param body     邮件体
     * @param userInfo 用户信息
     * @return 如果全部发给内部邮箱，返回false，如果有外部邮箱，返回true
     * @throws Exception 保存邮件错误或者发送邮件错误
     */
    @Transactional
    public boolean createAndSend(MailBody body, Integer userId, UserInfo userInfo) throws Exception
    {
        return sendMail(createMail(body, userId), userInfo);
    }

    @Transactional
    public void sendNotice(Mail mail) throws Exception
    {
        MailAccount account = null;
        if (mail.getPop3() != null)
        {
            account = dao.getAccount(mail.getUserId(), mail.getPop3());

            //邮箱帐号已经删除或者没有配置smpt服务，不发送通知
            if (account == null || StringUtils.isEmpty(account.getSmtpServer()))
                return;
        }

        MailBody body = MailUtils.notice(mail, account);

        if (body != null)
        {
            if (createAndSend(body, mail.getUserId(), null))
                SmtpSendJob.send();
        }
    }
}
