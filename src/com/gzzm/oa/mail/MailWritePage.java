package com.gzzm.oa.mail;

import com.gzzm.platform.annotation.AuthDeptIds;
import com.gzzm.platform.attachment.*;
import com.gzzm.platform.commons.*;
import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.platform.menu.MenuItem;
import com.gzzm.platform.organ.OrganDao;
import com.gzzm.platform.receiver.Receiver;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.arachne.exts.ParameterCheck;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 写邮件的服务
 *
 * @author camel
 * @date 2010-4-12
 */
@Service
public class MailWritePage
{
    static
    {
        ParameterCheck.addNoCheckURL("/oa/mail/*");
    }

    @Inject
    private static Provider<AttachmentService> attachmentServiceProvider;

    @Inject
    private static Provider<FileUploadService> uploadServiceProvider;

    /**
     * 注入有权限访问的部门ID，用于控制用户有权限访问哪些部门的用户
     */
    @AuthDeptIds
    private Collection<Integer> authDeptIds;

    @Inject
    private MailService service;

    @Inject
    private OrganDao organDao;

    @Inject
    private UserOnlineInfo userOnlineInfo;

    /**
     * 邮件ID，如果是新邮件则为null
     */
    private Long mailId;

    @NotSerialized
    private MailBody body;

    @NotSerialized
    private PageAttachmentList attachments;

    private String mailTo;

    private Long referenceId;

    private boolean notify;

    private boolean autoNotify = true;

    @NotSerialized
    private List<MailSign> signs;

    public MailWritePage()
    {
    }

    public Long getMailId()
    {
        return mailId;
    }

    public void setMailId(Long mailId)
    {
        this.mailId = mailId;
    }

    public MailBody getBody()
    {
        return body;
    }

    public void setBody(MailBody body)
    {
        this.body = body;
    }

    public PageAttachmentList getAttachments()
    {
        return attachments;
    }

    public void setAttachments(PageAttachmentList attachments)
    {
        this.attachments = attachments;
    }

    public boolean hasCc()
    {
        return body != null && !StringUtils.isEmpty(body.getCc());
    }

    public boolean hasSc()
    {
        return body != null && !StringUtils.isEmpty(body.getSc());
    }

    public String getMailTo()
    {
        return mailTo;
    }

    public void setMailTo(String mailTo)
    {
        this.mailTo = mailTo;
    }

    public Long getReferenceId()
    {
        return referenceId;
    }

    public void setReferenceId(Long referenceId)
    {
        this.referenceId = referenceId;
    }

    public boolean isNotify()
    {
        return notify;
    }

    public void setNotify(boolean notify)
    {
        this.notify = notify;
    }

    public boolean isAutoNotify()
    {
        return autoNotify;
    }

    public void setAutoNotify(boolean autoNotify)
    {
        this.autoNotify = autoNotify;
    }

    private void checkCapacity() throws Exception
    {
        if (service.isCapacityFull(userOnlineInfo.getUserId()))
            throw new NoErrorException("oa.mail.capacityfull");
    }

    public List<MailSign> getSigns() throws Exception
    {
        if (signs == null)
        {
            MailDao dao = service.getDao();
            signs = dao.getSignsForUser(userOnlineInfo.getUserId());

            for (Integer deptId : userOnlineInfo.getParentDeptIds())
            {
                List<MailSign> signs1 = dao.getSignsForDept(deptId);

                for (MailSign sign1 : signs1)
                {
                    boolean exists = false;
                    for (MailSign sign : signs)
                    {
                        if (sign.getTitle().equals(sign1.getTitle()))
                        {
                            exists = true;
                            break;
                        }
                    }

                    if (!exists)
                        signs.add(sign1);
                }
            }
        }

        return signs;
    }

    /**
     * 转到新建邮件的页面
     *
     * @return 转向写邮件页面/oa/mail/write.ptl
     * @throws Exception 邮箱容量超出限制，不允许写新邮件，抛出异常
     */
    @Service(url = "/oa/mail/new")
    @Forward(page = "/oa/mail/write.ptl")
    public String newMail() throws Exception
    {
        checkCapacity();

        if (mailTo != null)
        {
            body = new MailBody();
            body.setMailTo(mailTo);
        }

        attachments = new PageAttachmentList();
        return null;
    }

    /**
     * 打开草稿箱中的一份邮件
     *
     * @return 转向写邮件页面/oa/mail/write.ptl
     * @throws Exception 从数据库加载邮件数据失败
     */
    @Service(url = "/oa/mail/write/{mailId}")
    @Forward(page = "/oa/mail/write.ptl")
    public String openMail() throws Exception
    {
        Mail mail = service.getMail(mailId);

        mailId = getMailId();
        body = mail.getBody();

        if (body.getNotifyType() == MailNotifyType.NOTIFY)
            notify = true;

        attachments = new PageAttachmentList();
        attachments.setAttachmentId(body.getAttachmentId());

        return null;
    }

    /**
     * 回复邮件给所有人，包括发件人和所有收件人
     *
     * @param mailId 原邮件体id
     * @return 转向写邮件的页面write.ptl
     * @throws Exception 获取原邮件信息错误
     */
    @Service(url = "/oa/mail/{$0}/replyAll")
    @Forward(page = "/oa/mail/write.ptl")
    public String replyAll(Long mailId) throws Exception
    {
        checkCapacity();

        Mail mail = service.getMail(mailId);

        reply(mail);

        MailBody oBody = mail.getBody();

        String userId = userOnlineInfo.getUserId().toString();

        String mailFrom = oBody.getFrom();
        StringBuilder buffer = new StringBuilder();

        if (!StringUtils.isEmpty(mailFrom))
        {
            Receiver receiver = new Receiver(mailFrom);
            if (receiver.getId() == null || !receiver.getId().equals(userId))
            {
                buffer.append(receiver.getReceiver());
            }
        }

        //回复给所有人
        String mailTo = oBody.getMailTo();
        if (!StringUtils.isEmpty(mailTo))
        {
            List<Receiver> receivers = Receiver.parseReceiverList(mailTo);
            for (Receiver receiver : receivers)
            {
                if (receiver.getId() == null || !receiver.getId().equals(userId))
                {
                    if (buffer.length() > 0)
                        buffer.append(";");

                    buffer.append(receiver.getReceiver());
                }
            }
        }


        mailTo = buffer.toString();

        body.setMailTo(mailTo);

        String cc = oBody.getCc();
        if (!StringUtils.isEmpty(cc))
        {
            buffer = new StringBuilder();
            List<Receiver> receivers = Receiver.parseReceiverList(cc);
            for (Receiver receiver : receivers)
            {
                if (receiver.getId() == null || !receiver.getId().equals(userId))
                {
                    if (buffer.length() > 0)
                        buffer.append(";");

                    buffer.append(receiver.getReceiver());
                }
            }

            cc = buffer.toString();
            body.setCc(cc);
        }

        return null;
    }

    /**
     * 回复给发件人
     *
     * @param mailId 原邮件id
     * @return 转向写邮件的页面write.ptl
     * @throws Exception 获取原邮件信息错误
     */
    @Service(url = "/oa/mail/{$0}/reply")
    @Forward(page = "/oa/mail/write.ptl")
    public String reply(Long mailId) throws Exception
    {
        checkCapacity();

        Mail mail = service.getMail(mailId);

        reply(mail);

        if (!StringUtils.isEmpty(mailTo))
            body.setMailTo(mailTo);

        return null;
    }

    /**
     * 回复某个邮件，将原邮件的相关信息复制到新邮件中
     *
     * @param mail 原邮件体
     */
    private void reply(Mail mail)
    {
        body = MailUtils.reply(mail);

        referenceId = body.getReferenceId();

        attachments = new PageAttachmentList();
    }

    /**
     * 转发邮件给其他人
     *
     * @param bodyId 原邮件体id
     * @return 转向打开自动保存在草稿箱的邮件
     * @throws Exception 获取原邮件信息错误
     */
    @Service(url = "/oa/mail/{$0}/forward")
    @Redirect
    @Transactional
    public String forward(Long bodyId) throws Exception
    {
        checkCapacity();

        MailBody oBody = service.getBody(bodyId);

        body = MailUtils.forward(oBody);

        //复制附件
        if (oBody.getAttachmentId() != null)
        {
            body.setAttachmentId(attachmentServiceProvider.get()
                    .clone(oBody.getAttachmentId(), "mail", userOnlineInfo.getUserId(), null));
        }

        return MenuItem.formatUrl("/oa/mail/write/" + service.createMail(body, userOnlineInfo.getUserId()));
    }

    /**
     * 重新发送某份邮件
     *
     * @param bodyId 原邮件体id
     * @return 转向打开自动保存在草稿箱的邮件
     * @throws Exception 获取原邮件信息错误
     */
    @Service(url = "/oa/mail/{$0}/resend")
    @Redirect
    @Transactional
    public String resend(Long bodyId) throws Exception
    {
        checkCapacity();

        MailBody oBody = service.getBody(bodyId);

        if (body == null)
            body = new MailBody();

        //复制原来的邮件
        body.setTitle(oBody.getTitle());
        body.setContent(oBody.getContent());
        body.setMailTo(oBody.getMailTo());
        body.setCc(oBody.getCc());
        body.setSc(oBody.getSc());
        body.setUrgent(oBody.isUrgent());
        body.setAccountId(oBody.getAccountId());
        body.setNotifyType(oBody.getNotifyType());

        referenceId = oBody.getReferenceId();

        //复制附件
        if (oBody.getAttachmentId() != null)
        {
            body.setAttachmentId(attachmentServiceProvider.get()
                    .clone(oBody.getAttachmentId(), "mail", userOnlineInfo.getUserId(), null));
        }

        return MenuItem.formatUrl("/oa/mail/write/" + service.createMail(body, userOnlineInfo.getUserId()));
    }

    /**
     * 保存邮件
     *
     * @return 返回邮件ID
     * @throws Exception 保存数据到数据库错误
     */
    @Service(url = "/oa/mail/save", method = HttpMethod.post)
    @Transactional
    public Long save() throws Exception
    {
        if (StringUtils.isEmpty(body.getTitle()))
            body.setTitle("无标题");

        if (body.isUrgent() == null)
            body.setUrgent(false);

        if (referenceId != null)
            body.setReferenceId(referenceId);

        if (notify)
            body.setNotifyType(MailNotifyType.NOTIFY);
        else if (!autoNotify)
            body.setNotifyType(MailNotifyType.NONOTIFY);

        //先保存附件
        if (attachments != null)
        {
            if (mailId != null)
                attachments.setAttachmentId(service.getMail(mailId).getBody().getAttachmentId());
            body.setAttachmentId(attachments.save(userOnlineInfo.getUserId(), null, "mail"));
        }

        return service.saveMail(body, mailId, userOnlineInfo.getUserId());
    }

    /**
     * 发送邮件
     *
     * @return 返回邮件ID
     * @throws Exception 保存邮件或发送邮件失败
     */
    @Service(method = HttpMethod.post, url = "/oa/mail/send")
    @ObjectResult
    @Transactional
    public Long send() throws Exception
    {
        //先保存
        Long mailId = save();

        //发送邮件
        if (service.sendMail(mailId, userOnlineInfo))
            SmtpSendJob.send();

        return mailId;
    }

    /**
     * 获得用户定义的外部邮件帐号
     *
     * @return 用户定义的所有外部邮件帐号
     * @throws Exception 数据库查询错误
     */
    @Select(field = "body.accountId")
    @NotSerialized
    public List<MailAccount> getAccounts() throws Exception
    {
        return service.getAccountsForSmtp(userOnlineInfo.getUserId());
    }

    /**
     * 是否支持smtp服务
     *
     * @return 支持返回true，不支持返回false
     * @throws Exception 数据库查询错误
     */
    @NotSerialized
    public boolean isSmtpSupported() throws Exception
    {
        return service.getConfig(userOnlineInfo.getUserId()).isSmtp();
    }

    /**
     * 是否支持本地smtp服务，配置本地邮箱郁闷则支持smtp服务，否则不支持
     *
     * @return 支持返回true，不支持返回false
     */
    public boolean isLocalServerSupported()
    {
        MailContext context = MailContext.getContext();

        return context != null && context.getDomain() != null;
    }

    @Service
    @NotSerialized
    public List<PageAttachmentList.Item> getAttachmentItems() throws Exception
    {
        Mail mail = service.getMail(mailId);

        MailBody body = mail.getBody();

        Long attachmentId = body.getAttachmentId();
        if (attachmentId == null)
        {
            return null;
        }
        else
        {
            attachments = new PageAttachmentList();
            attachments.setAttachmentId(attachmentId);
            return attachments.getItems();
        }
    }

    /**
     * 从资料库添加文件
     *
     * @param fileIds 资料库中的文件ID
     * @return 保存后的邮件ID，如果添加附件之前邮件未保存，将此保存后邮件ID返回给客户端
     * @throws Exception 数据库错误或者IO错误
     */
    @Service(method = HttpMethod.post)
    @Transactional
    public Long addFiles(String[] fileIds) throws Exception
    {
        Long mailId = save();

        Mail mail = service.getMail(mailId);
        MailBody body = mail.getBody();
        Long attachmentId0 = body.getAttachmentId();

        Long attachmentId = attachmentServiceProvider.get()
                .loadForm(attachmentId0, fileIds, userOnlineInfo.getUserId(), userOnlineInfo.getDeptId(), "mail");

        if (attachmentId0 == null)
        {
            body.setAttachmentId(attachmentId);
            service.getDao().update(body);

            mail.setAttachment(Boolean.TRUE);
            service.getDao().update(mail);
        }

        return mailId;
    }

    /**
     * 批量添加文件
     *
     * @param filePaths 要添加的文件的临时目录
     * @return 保存后的邮件ID，如果添加附件之前邮件未保存，将此保存后邮件ID返回给客户端
     * @throws Exception 数据库错误或者IO错误
     */
    @Service(method = HttpMethod.post)
    @Transactional
    public Long uploadFiles(String[] filePaths) throws Exception
    {
        FileUploadService fileUploadService = uploadServiceProvider.get();
        try
        {
            Long mailId = save();

            List<InputFile> files = new ArrayList<InputFile>(filePaths.length);
            for (String filePath : filePaths)
            {
                InputFile file = fileUploadService.getFile(filePath);
                files.add(file);
            }

            Mail mail = service.getMail(mailId);
            MailBody body = mail.getBody();
            Long attachmentId = body.getAttachmentId();

            List<Attachment> attachments = new ArrayList<Attachment>(files.size());

            for (InputFile file : files)
            {
                Attachment attachment = new Attachment();

                attachment.setAttachmentName(file.getName());
                attachment.setFileName(file.getName());
                attachment.setUserId(userOnlineInfo.getUserId());
                attachment.setInputable(file.getInputable());
                attachment.setTag("mail");

                attachments.add(attachment);
            }

            AttachmentSaver saver = Tools.getBean(AttachmentSaver.class);

            saver.setAttachmentId(attachmentId);
            saver.setAttachments(attachments);

            saver.save();

            if (attachmentId == null)
            {
                attachmentId = saver.getAttachmentId();
                body.setAttachmentId(attachmentId);
                service.getDao().update(body);

                mail.setAttachment(Boolean.TRUE);
                service.getDao().update(mail);
            }

            return mailId;
        }
        finally
        {
            if (filePaths != null)
            {
                for (String filePath : filePaths)
                {
                    try
                    {
                        fileUploadService.deleteFile(filePath);
                    }
                    catch (Throwable ex)
                    {
                        //删除临时文件不影响主逻辑，跳过
                    }
                }
            }
        }
    }

    /**
     * 添加文件
     *
     * @return 保存后的邮件ID，如果添加附件之前邮件未保存，将此保存后邮件ID返回给客户端
     * @throws Exception 数据库错误或者IO错误
     */
    @Service(method = HttpMethod.post)
    @Transactional
    public Long uploadFile(InputFile file, String fileName) throws Exception
    {
        Long mailId = save();

        Mail mail = service.getMail(mailId);
        MailBody body = mail.getBody();
        Long attachmentId = body.getAttachmentId();


        List<Attachment> attachments = new ArrayList<Attachment>(1);
        Attachment attachment = new Attachment();

        if (StringUtils.isEmpty(fileName))
            fileName = file.getName();

        attachment.setAttachmentName(fileName);
        attachment.setFileName(fileName);
        attachment.setUserId(userOnlineInfo.getUserId());
        attachment.setInputable(file.getInputable());
        attachment.setTag("mail");

        attachments.add(attachment);

        AttachmentSaver saver = Tools.getBean(AttachmentSaver.class);

        saver.setAttachmentId(attachmentId);
        saver.setAttachments(attachments);

        saver.save();

        if (attachmentId == null)
        {
            attachmentId = saver.getAttachmentId();
            body.setAttachmentId(attachmentId);
            service.getDao().update(body);

            mail.setAttachment(Boolean.TRUE);
            service.getDao().update(mail);
        }

        return mailId;
    }
}
