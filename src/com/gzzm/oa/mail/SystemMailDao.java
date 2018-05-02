package com.gzzm.oa.mail;

import com.gzzm.platform.attachment.*;
import com.gzzm.platform.commons.*;
import com.gzzm.platform.commons.event.Events;
import com.gzzm.platform.organ.User;
import com.gzzm.platform.receiver.Receiver;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.*;
import net.cyan.commons.util.io.Mime;
import net.cyan.mail.*;
import net.cyan.nest.annotation.Inject;
import net.cyan.thunwind.annotation.*;

import java.util.*;

/**
 * 实现net.cyan.mail.MailDao接口，以通过cyan.mail包实现外部邮件的收费功能
 *
 * @author camel
 * @date 2010-3-16
 */
public abstract class SystemMailDao extends MailDao implements net.cyan.mail.MailDao
{
    @Inject
    private static Provider<GlobalConfig> configProvider;

    @Inject
    private static Provider<MailService> serviceProvider;

    @Inject
    private static Provider<AttachmentService> attachmentServiceProvider;

    public SystemMailDao()
    {
    }

    /**
     * 将接收者列表中的本地用户转化为对外的邮箱地址，即将local换成实际的域名，将用户ID换成用户登录名
     *
     * @param receivers 接收者列表，多个用;或者逗号隔开
     * @return 转化后的接收者列表
     * @throws Exception 数据库查询用户错误
     */
    private String localToRemote(String receivers) throws Exception
    {
        if (StringUtils.isEmpty(receivers))
            return null;

        String domain = null;

        StringBuilder buffer = new StringBuilder();
        for (Receiver receiver : Receiver.parseReceiverList(receivers))
        {
            if (buffer.length() > 0)
                buffer.append(";");

            if (receiver.getId() == null)
            {
                if (receiver.getName().equals(receiver.getValue()))
                {
                    buffer.append(receiver.getValue());
                }
                else
                {
                    buffer.append("\"").append(receiver.getName()).append("\"");
                    buffer.append("<").append(receiver.getValue()).append(">");
                }
            }
            else
            {
                if (domain == null)
                    domain = MailContext.getContext().getDomain();

                User user = get(User.class, Integer.valueOf(receiver.getId()));

                buffer.append("\"").append(user.getUserName()).append("\"");
                buffer.append("<").append(user.getLoginName()).append("@").append(domain).append(">");
            }
        }

        return buffer.toString();
    }

    /**
     * 将接收者列表中的属于本系统的邮箱转化为本地邮箱地址，即将域名换成local，将用户登录名换成用户id
     *
     * @param receivers 接收者列表，多个用;或者逗号隔开
     * @return 转化后的接收者列表
     * @throws Exception 数据库查询用户错误
     */
    private String remoteToLocal(String receivers) throws Exception
    {
        if (StringUtils.isEmpty(receivers))
            return null;

        MailContext context = MailContext.getContext();

        StringBuilder buffer = new StringBuilder();
        for (Receiver receiver : Receiver.parseReceiverList(receivers))
        {
            if (buffer.length() > 0)
                buffer.append(";");

            String address = receiver.getValue();
            int index = address.indexOf('@');
            String server = address.substring(index + 1).trim();

            if (context.getServer().containsDomain(server))
            {
                String loginName = address.substring(0, index).trim();
                buffer.append(MailUtils.getFrom(getUserByLoginName(loginName)));
            }
            else
            {
                if (receiver.getName().equals(receiver.getValue()))
                {
                    buffer.append(receiver.getValue());
                }
                else
                {
                    buffer.append("\"").append(receiver.getName()).append("\"");
                    buffer.append("<").append(receiver.getValue()).append(">");
                }
            }
        }

        return buffer.toString();
    }

    @Transactional
    public void saveMail(MailContent content) throws Exception
    {
        MailBody mailBody = new MailBody();

        //保存邮件基本信息
        mailBody.setMailTo(remoteToLocal(content.getTo()));
        mailBody.setCc(remoteToLocal(content.getCc()));
        mailBody.setSc(remoteToLocal(content.getSc()));

        String messageId = content.getMessageId();
        if (messageId == null)
        {
            messageId = Tools.getUUID();
            MailContext context = MailContext.getContext();
            if (context != null)
            {
                String domain = context.getDomain();
                if (domain != null)
                    messageId += "@" + domain;
            }
        }
        mailBody.setMessageId(messageId);
        mailBody.setTitle(content.getSubject());
        if (StringUtils.isBlank(mailBody.getTitle()))
            mailBody.setTitle("无标题");

        mailBody.setUrgent(content.isUrgent());
        mailBody.setMailSize(content.getSize());
        mailBody.setSendTime(content.getSendTime());
        mailBody.setUpdateTime(new Date());
        mailBody.setMailReferences(content.getReferences());
        mailBody.setInReplyTo(content.getInReplyTo());
        mailBody.setNotificationTo(content.getNotificationTo());
        mailBody.setReceipt(content.isReceipt());

        if (content.getInReplyTo() != null)
        {
            mailBody.setRefBodyId(getBodyIdByMessageId(content.getInReplyTo()));
        }
        else
        {
            List<String> referenceList = content.getReferenceList();
            if (referenceList != null && referenceList.size() > 0)
                mailBody.setRefBodyId(getBodyIdByMessageId(referenceList.get(0)));
        }

        //设置mailfrom，如果是本地用户发送的邮件，则设置creator
        Receiver receiver = new Receiver(content.getFrom());
        String address = receiver.getValue();
        int index = address.indexOf('@');
        String server = address.substring(index + 1).trim();
        if (MailContext.getContext().getServer().containsDomain(server))
        {
            //本地邮件，设置creator
            String loginName = address.substring(0, index).trim();
            mailBody.setCreator(getUserByLoginName(loginName).getUserId());
        }
        else
        {
            mailBody.setMailFrom(content.getFrom());
        }

        final AttachmentService attachmentService = attachmentServiceProvider.get();

        final MailEntity suitableEntity = content.getSuitableEntity(Mime.HTML);
        if (suitableEntity != null)
        {
            final MailText mailText = suitableEntity.getText();

            String s = mailText.getContent();
            final Map<String, String> cidToUrl = new HashMap<String, String>();
            if (!StringUtils.isEmpty(s))
            {
                HtmlUtils.URLMapper urlMapper = new HtmlUtils.URLMapper()
                {
                    @Override
                    public String map(String url) throws Exception
                    {
                        if (StringUtils.startsWithIgnoreCase(url, "cid:"))
                        {
                            String contentId = url.substring(4);
                            String s = cidToUrl.get(contentId);

                            if (s == null)
                            {
                                MailEmbed mailEmbed = suitableEntity.getMailEmbed(contentId);

                                if (mailEmbed != null)
                                {
                                    Attachment attachment = new Attachment();
                                    attachment.setAttachmentName(mailEmbed.getFileName());
                                    attachment.setInputable(mailEmbed.getContent());
                                    attachment.setUuid(CommonUtils.uuid());

                                    attachmentService.save(Collections.singleton(attachment), "file", null, null);

                                    s = "/attachment/" + attachment.getUuid() + "/0";
                                    String extName = IOUtils.getExtName(mailEmbed.getFileName());
                                    if (!StringUtils.isEmpty(extName))
                                        s += "." + extName;

                                    cidToUrl.put(contentId, s);
                                }
                            }

                            return s;
                        }

                        return null;
                    }
                };

                s = HtmlUtils.changeImgURL(s, urlMapper);
                s = HtmlUtils.changeHrefURL(s, urlMapper);

                mailBody.setContent(s.toCharArray());
            }

            mailBody.setContentType(mailText.getContentType());
        }


        MailPart entity = content.getEntity();

        //保存附件
        if (entity instanceof MailMultipart)
        {
            List<Attachment> attachments = null;
            for (MailPart part : ((MailMultipart) entity).getParts())
            {
                if (part instanceof MailAttachment)
                {
                    if (attachments == null)
                        attachments = new ArrayList<Attachment>();

                    MailAttachment mailAttachment = (MailAttachment) part;
                    Attachment attachment = new Attachment();
                    attachment.setAttachmentName(mailAttachment.getFileName());
                    attachment.setFileName(mailAttachment.getFileName());
                    attachment.setInputable(mailAttachment.getContent());
                    attachment.setContentType(mailAttachment.getContentType());
                    attachments.add(attachment);
                }
            }

            if (attachments != null)
            {
                mailBody.setAttachmentId(attachmentService.save(attachments, "mail", null, null));
            }
        }

        add(mailBody);

        content.setMailId(mailBody.getBodyId().toString());

        if (content.getMailContent() != null)
        {
            MailData mailData = new MailData();
            mailData.setBodyId(mailBody.getBodyId());
            mailData.setData(content.getMailContent().getInputStream());

            add(mailData);
        }
    }

    public void saveItem(MailItem item, MailContent content) throws Exception
    {
        if (item.getType() == MailItemType.sended && content.isReceipt())
            return;

        Mail mail = new Mail();

        if (item.getItemId() != null)
            mail.setMailId(Long.valueOf(item.getItemId()));
        mail.setBodyId(Long.valueOf(content.getMailId()));
        mail.setUserId(Integer.valueOf(item.getUserId()));
        mail.setAcceptTime(item.getTime());
        if (item.getType() == MailItemType.sended)
            mail.setReadTime(item.getTime());

        mail.setType(MailType.valueOf(item.getType().toString()));

        mail.setTitle(content.getSubject());
        if (StringUtils.isBlank(mail.getTitle()))
            mail.setTitle("无标题");
        mail.setMailFrom(remoteToLocal(content.getFrom()));
        mail.setSendTime(content.getSendTime());
        mail.setMailSize(content.getSize());
        mail.setDeleted(false);
        mail.setUrgent(content.isUrgent());
        mail.setAttachment(content.hasAttachment());

        mail.setPop3(item.getPop3());
        mail.setUidl(item.getUidl());

        if (!StringUtils.isEmpty(item.getPop3()) && !StringUtils.isEmpty(item.getUidl()))
        {
            //从pop3收下的邮件，记录pop3接收记录，防止重复接收
            MailPopRecrod record = new MailPopRecrod();
            record.setPop3(item.getPop3());
            record.setUidl(item.getUidl());
            record.setUserId(mail.getUserId());

            try
            {
                add(record);
            }
            catch (Throwable ex)
            {
                //记录pop3记录错误，跳过
                Tools.log(ex);
            }
        }

        MailBody mailBody = load(MailBody.class, item.getMailId());
        mail.setSender(mailBody.getCreator());

        autoCatalog(mail);

        add(mail);

        if (item.getItemId() == null)
            item.setMailId(mail.getMailId().toString());

        if (item.getType() == MailItemType.received || item.getType() == MailItemType.receipt)
        {
            String from = new Receiver(mail.getFrom()).getValue();

            //保存邮件关系
            MailRelationShip relationShip = new MailRelationShip();
            relationShip.setMailId(mail.getMailId());
            relationShip.setUserId(mail.getUserId());
            relationShip.setRefAddress(from);
            add(relationShip);

            String inReplyTo = content.getInReplyTo();
            if (inReplyTo == null)
            {
                List<String> referenceList = content.getReferenceList();
                if (referenceList != null && referenceList.size() > 0)
                {
                    inReplyTo = referenceList.get(0);
                }
            }

            if (inReplyTo != null)
            {
                //回复的邮件，状态
                if (from.endsWith("@local"))
                {
                    //本地用户发送的邮件，更新邮件状态
                    Integer userId = new Integer(from.substring(0, from.length() - 6));
                    for (Mail mail1 : getMailsByMessageId(inReplyTo, userId))
                    {
                        if (mail1.getReadTime() == null)
                            mail1.setReadTime(item.getTime());

                        if (item.getType() == MailItemType.received &&
                                (mail1.isReplyed() == null || !mail1.isReplyed()))
                            mail1.setReplyed(true);

                        update(mail1);

                        if (mailBody.getReferenceId() == null)
                        {
                            mailBody.setReferenceId(mail1.getMailId());
                            update(mailBody);
                        }
                    }
                }
                else
                {
                    //外部用户发过来的邮件，更新smtp发送状态
                    for (MailSmtpRecord record : getRecordsByMessageId(inReplyTo))
                    {
                        if (from.equals(new Receiver(record.getMailTo()).getValue()))
                        {
                            MailState state = record.getState();
                            if (item.getType() == MailItemType.receipt)
                            {
                                //已阅回执，将状态表示为已阅读
                                if (state == null || state == MailState.notSended ||
                                        state == MailState.sended)
                                    record.setState(MailState.readed);
                            }
                            else
                            {
                                //回信，将状态表示为已回复
                                if (state == null || state != MailState.replyed)
                                    record.setState(MailState.replyed);
                            }

                            if (record.getReadTime() == null)
                                record.setReadTime(item.getTime());

                            update(record);
                            break;
                        }
                    }
                }
            }

            if (item.getType() == MailItemType.received)
                Events.invoke(get(Mail.class, mail.getMailId()), "receive");
        }
        else if (item.getType() == MailItemType.sended)
        {
            String s = null;

            if (!StringUtils.isEmpty(content.getTo()))
            {
                s = content.getTo();
            }

            if (!StringUtils.isEmpty(content.getCc()))
            {
                if (s != null)
                    s += ";" + content.getCc();
                else
                    s = content.getCc();
            }

            if (!StringUtils.isEmpty(content.getSc()))
            {
                if (s != null)
                    s += ";" + content.getSc();
                else
                    s = content.getSc();
            }

            MailContext context = MailContext.getContext();

            //保存邮件关系
            Set<String> addressSet = new HashSet<String>();
            for (Receiver receiver : Receiver.parseReceiverList(s))
            {
                String address = receiver.getValue();
                int index = address.indexOf('@');
                String server = address.substring(index + 1).trim();

                if (context.getServer().containsDomain(server))
                {
                    String loginName = address.substring(0, index).trim();
                    address = getUserByLoginName(loginName).getUserId() + "@local";
                }

                if (!addressSet.contains(address))
                {
                    //当接收者出现多次时只保存一次
                    addressSet.add(address);

                    MailRelationShip relationShip = new MailRelationShip();
                    relationShip.setMailId(mail.getMailId());
                    relationShip.setUserId(mail.getUserId());
                    relationShip.setRefAddress(address);
                    add(relationShip);
                }
            }

            String inReplyTo = content.getInReplyTo();
            if (inReplyTo == null)
            {
                List<String> referenceList = content.getReferenceList();
                if (referenceList != null && referenceList.size() > 0)
                {
                    inReplyTo = referenceList.get(0);
                }
            }

            if (inReplyTo != null)
            {
                //本地用户发送的邮件，更新邮件状态
                for (Mail mail1 : getMailsByMessageId(inReplyTo, mail.getUserId()))
                {
                    if (mail1.getReadTime() == null)
                        mail1.setReadTime(item.getTime());

                    if (item.getType() == MailItemType.received &&
                            (mail1.isReplyed() == null || !mail1.isReplyed()))
                        mail1.setReplyed(true);

                    update(mail1);
                }
            }
        }
    }

    @OQL("select bodyId from MailBody where messageId=:1")
    protected abstract Long getBodyIdByMessageId(String messageId);

    @OQL("select m from Mail m where body.messageId=:1 and userId=:2 and type=2")
    protected abstract List<Mail> getMailsByMessageId(String messageId, Integer userId);

    @OQL("select r from MailSmtpRecord r where body.messageId=:1")
    protected abstract List<MailSmtpRecord> getRecordsByMessageId(String messageId);

    @OQL("exists User where loginName=:1")
    public abstract boolean isUserExists(String userName) throws Exception;

    public boolean checkPassword(String userName, String password) throws Exception
    {
        User user = getUserByLoginName(userName);

        return user != null && PasswordUtils.checkPassword(password, user.getPassword(), user.getUserId());
    }

    @OQL("select userId from User where loginName=:1")
    public abstract String getUserId(String userName) throws Exception;

    @OQL("select userName from User where userId=:1")
    public abstract String getUserName(String userId) throws Exception;

    @GetByField("loginName")
    public abstract User getUserByLoginName(String loginName) throws Exception;

    @Override
    public MailContent getMailContent(String mailId, int top) throws Exception
    {
        Long bodyId = Long.valueOf(mailId);
        MailBody mailBody = load(MailBody.class, bodyId);

        boolean receipt = mailBody.isReceipt() != null && mailBody.isReceipt();

        //设置邮件基本信息
        MailContent content = new MailContent();
        content.setMailId(mailId);
        content.setTo(localToRemote(mailBody.getMailTo()));
        content.setCc(localToRemote(mailBody.getCc()));
        content.setSc(localToRemote(mailBody.getSc()));
        content.setMessageId(mailBody.getMessageId());
        content.setSize(mailBody.getMailSize() == null ? 0 : mailBody.getMailSize());
        if (mailBody.isUrgent() != null && mailBody.isUrgent())
            content.setUrgent();
        content.setSubject(mailBody.getTitle());
        content.setReferences(mailBody.getMailReferences());
        content.setInReplyTo(mailBody.getInReplyTo());
        content.setReceipt(receipt);

        User creator = mailBody.getCreateUser();
        if (creator != null)
        {
            MailAccount account = mailBody.getAccount();
            String address;

            String name = null;
            if (account == null)
            {
                address = creator.getLoginName() + "@" + MailContext.getContext().getDomain();
            }
            else
            {
                address = account.getAddress();
                name = account.getNickName();
            }

            if (StringUtils.isEmpty(name))
                name = creator.getUserName();
            content.setFrom("\"" + name + "\"<" + address + ">");

            if (!content.isReceipt())
                content.setNotificationTo(address);
        }
        else
        {
            content.setFrom(mailBody.getMailFrom());
            if (!content.isReceipt())
                content.setNotificationTo(mailBody.getNotificationTo());
        }

        Mail mail = getMailByBodyId(bodyId);
        content.setSendTime(mail.getSendTime());

        if (top > 0)
        {
            String s = HtmlUtils.clearHtmlTag(new String(mailBody.getContent()));

            String[] ss = s.split("\\s");

            if (top < ss.length)
            {
                StringBuilder buffer = new StringBuilder();
                for (int i = 0; i < top; i++)
                {
                    buffer.append(ss[i]).append(" ");
                }
                s = buffer.toString();
            }

            MailText mailText = new MailText(s, Mime.PLAIN);
            content.setEntity(mailText);
        }
        else
        {
            MailData data = load(MailData.class, bodyId);
            if (data != null)
            {
                content.setMailContent(new Inputable.StreamInput(data.getData()));
            }
            else
            {
                content.setEntity(createEntity(mailBody, receipt));
            }
        }

        return content;
    }

    private MailPart createEntity(MailBody mailBody, boolean receipt) throws Exception
    {
        MailPart entity = null;

        if (mailBody.getContent() != null)
        {
            String s = new String(mailBody.getContent());

            final AttachmentService attachmentService = attachmentServiceProvider.get();
            final List<Attachment> attachments = new ArrayList<Attachment>();

            HtmlUtils.URLMapper urlMapper = new HtmlUtils.URLMapper()
            {
                @Override
                public String map(String url) throws Exception
                {
                    if (url.startsWith("/attachment/"))
                    {
                        url = url.substring(12);
                        int index = url.indexOf('/');
                        if (index > 0)
                        {
                            String uuid = url.substring(0, index);

                            for (Attachment attachment : attachments)
                            {
                                if (uuid.equals(attachment.getUuid()))
                                    return "cid:" + uuid;
                            }

                            Attachment attachment = attachmentService.getAttachmentByUUID(uuid);
                            if (attachment != null)
                            {
                                attachments.add(attachment);

                                return "cid:" + uuid;
                            }
                        }
                    }

                    return null;
                }
            };

            s = HtmlUtils.changeImgURL(s, urlMapper);
            s = HtmlUtils.changeHrefURL(s, urlMapper);

            MailText mailText = new MailText(s, mailBody.getContentType());

            if (attachments.size() == 0)
            {
                entity = mailText;
            }
            else
            {
                MailMultipart multipart = new MailMultipart(MultipartType.RELATED);

                multipart.addPart(mailText);

                for (Attachment attachment : attachments)
                {
                    MailEmbed mailEmbed = new MailEmbed(attachment.getFileName(), attachment,
                            attachment.getUuid(), attachment.getContentType1(), attachment.getFileSize());

                    multipart.addPart(mailEmbed);
                }

                entity = multipart;
            }
        }
        else if (!receipt)
        {
            entity = new MailText("", Mime.PLAIN);
        }

        //设置附件列表
        Collection<Attachment> attachments = mailBody.getAttachments();
        if (attachments != null)
        {
            int n = attachments.size();
            if (n > 0)
            {
                MailMultipart multipart = new MailMultipart(MultipartType.MIXED);

                multipart.addPart(entity);

                for (Attachment attachment : mailBody.getAttachments())
                {
                    MailAttachment mailAttachment = new MailAttachment(attachment.getFileName(), attachment,
                            attachment.getContentType1(), attachment.getFileSize());

                    multipart.addPart(mailAttachment);
                }

                entity = multipart;
            }
        }

        return entity;
    }

    @OQL("select m from Mail m where bodyId=:1")
    protected abstract Mail getMailByBodyId(Long bodyId);

    public List<MailInfo> getReceiveList(String userName) throws Exception
    {
        return getReceiveList(new Integer(getUserId(userName)));
    }

    /**
     * 查询收件箱中的邮件
     *
     * @param userId yonghuID
     * @return 邮件信息列表
     * @throws Exception 数据库异常
     */
    @OQL("select mailId itemId,bodyId mailId,mailSize size from Mail " +
            "where userId=:1 and type=2 and deleted=0 and catalogId is null order by acceptTime desc limit 50")
    public abstract List<MailInfo> getReceiveList(Integer userId) throws Exception;

    @Transactional
    public void deleteMail(String itemId) throws Exception
    {
        Mail mail = load(Mail.class, Long.valueOf(itemId));
        if (mail != null)
        {
            Long bodyId = mail.getBodyId();

            //删除邮件
            delete(mail);

            if (!existsMailWithBodyId(bodyId))
            {
                //再没有邮件关联此邮件体，将邮件体删除
                MailBody body = load(MailBody.class, bodyId);

                //删除附件
                Long attachmentId = body.getAttachmentId();
                if (attachmentId != null)
                {
                    attachmentServiceProvider.get().delete(attachmentId);
                }

                //删除正文
                delete(body);
            }
        }
    }

    @OQL("exists Mail where bodyId=:1")
    protected abstract boolean existsMailWithBodyId(Long bodyId) throws Exception;

    @OQL("exists MailPopRecrod where uidl=:1 and pop3=:2 and userId=:3")
    public abstract boolean isReceived(String uidl, String pop3, String userId) throws Exception;

    public boolean accpet(String userIdString, long size) throws Exception
    {
        MailService service = serviceProvider.get();
        Integer userId = Integer.valueOf(userIdString);

        long wastage = service.getWastage(userId);
        long capacity = service.getCapacity(userId);

        return capacity - wastage >= size;
    }
}
