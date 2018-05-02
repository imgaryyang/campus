package com.gzzm.oa.mail;

import com.gzzm.platform.attachment.*;
import com.gzzm.platform.organ.User;
import com.gzzm.platform.receiver.Receiver;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;
import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.*;

/**
 * 邮件相关的数据操作
 *
 * @author camel
 * @date 2010-3-17
 */
public abstract class MailDao extends GeneralDao
{
    @Inject
    private static Provider<AttachmentService> attachhmentServiceProvider;

    public MailDao()
    {
    }

    /**
     * 获得某个用户的油箱配置
     *
     * @param userId 用户ID
     * @return 邮箱容量
     * @throws Exception 数据库读取数据异常
     */
    public MailConfig getConfig(Integer userId) throws Exception
    {
        return load(MailConfig.class, userId);
    }

    /**
     * 获得某个用户邮件所占有的容量
     *
     * @param userId 用户id
     * @return 邮件总容量
     * @throws Exception 数据库查询异常
     */
    @OQL("select sum(mailSize) from Mail where userId=:1")
    public abstract long getWastage(Integer userId) throws Exception;


    /**
     * 根据邮件ID，获得某个邮件
     *
     * @param mailId 邮件Id
     * @return 邮件
     * @throws Exception 数据库读取数据异常
     */
    public Mail getMail(Long mailId) throws Exception
    {
        return load(Mail.class, mailId);
    }

    public MailBody getBody(Long bodyId) throws Exception
    {
        return load(MailBody.class, bodyId);
    }

    public void deleteBody(Long bodyId) throws Exception
    {
        delete(MailBody.class, bodyId);
    }

    public MailMark getMailMark(Long markId) throws Exception
    {
        return load(MailMark.class, markId);
    }

    private void initMailSize(MailBody body) throws Exception
    {
        long size = 0;
        if (body.getContent() != null)
            size += new String(body.getContent()).getBytes("UTF-8").length;

        if (body.getAttachments() != null)
        {
            Collection<Attachment> attachments = body.getAttachments();

            if (body.getAttachmentId() == null)
            {
                body.setAttachmentId(
                        attachhmentServiceProvider.get().save(attachments, "mail", body.getCreator(), null));
                body.setAttachments(null);
            }

            for (Attachment attachment : attachments)
                size += attachment.getSize();
        }
        else
        {
            Long attachmentId = body.getAttachmentId();
            if (attachmentId == null && body.getBodyId() != null)
                attachmentId = load(MailBody.class, body.getBodyId()).getAttachmentId();

            if (attachmentId != null)
            {
                AttachmentService attachmentService = attachhmentServiceProvider.get();
                long attachmentSize = attachmentService.getSize(attachmentId);
                if (attachmentSize == 0)
                {
                    if (attachmentService.getCount(attachmentId) == 0)
                        attachmentId = null;
                }
                else
                {
                    size += attachmentSize;
                }
            }

            body.setAttachmentId(attachmentId);
        }

        body.setMailSize(size);
    }

    public Long createMail(MailBody body, Integer userId) throws Exception
    {
        return createMail(body, userId, null);
    }


    /**
     * 创建一份邮件并保存到草稿箱中
     *
     * @param body   邮件体
     * @param userId 用户id
     * @param type   邮件的类型，可选的值为MailType.draft或MailType.notice
     * @return 草稿箱中的邮件的mailId
     * @throws Exception 数据库错误
     */
    @Transactional
    public Long createMail(MailBody body, Integer userId, MailType type) throws Exception
    {
        if (type == null)
            type = body.isReceipt() != null && body.isReceipt() ? MailType.notice : MailType.draft;

        body.setCreator(userId);

        initMailSize(body);

        add(body);

        Mail mail = new Mail();
        Date date = new Date();
        mail.setAcceptTime(date);
        mail.setReadTime(date);

        mail.setUserId(userId);
        mail.setSender(userId);
        mail.setTitle(body.getTitle());
        mail.setType(type);
        mail.setBodyId(body.getBodyId());
        mail.setUrgent(body.isUrgent());
        mail.setDeleted(false);
        mail.setMailSize(body.getMailSize());
        mail.setAttachment(body.getAttachmentId() != null);

        add(mail);

        return mail.getMailId();
    }

    @Transactional
    public void updateMail(MailBody body, Long mailId) throws Exception
    {
        Mail mail = getMail(mailId);
        body.setBodyId(mail.getBodyId());
        initMailSize(body);

        update(body);

        mail.setTitle(body.getTitle());
        mail.setUrgent(body.isUrgent());
        mail.setMailSize(body.getMailSize());
        mail.setAttachment(body.getAttachmentId() != null);

        update(mail);
    }

    public MailAccount getAccount(Integer account) throws Exception
    {
        return load(MailAccount.class, account);
    }

    /**
     * 根据邮件地址获得某个用户配置的外部邮箱
     *
     * @param userId  用户ID
     * @param address 邮件地址
     * @return 外部邮箱列表
     * @throws Exception 数据库查询错误
     */
    @GetByField({"userId", "address"})
    public abstract MailAccount getAccount(Integer userId, String address) throws Exception;

    /**
     * 获得某个用户配置的所有外部邮箱
     *
     * @param userId 用户ID
     * @return 外部邮箱列表
     * @throws Exception 数据库查询错误
     */
    @OQL("select a from MailAccount a where userId=:1 order by orderId")
    public abstract List<MailAccount> getAccounts(Integer userId) throws Exception;

    /**
     * 获得某个用户配置的有smtp服务器的外部邮箱
     *
     * @param userId 用户ID
     * @return 外部邮箱列表
     * @throws Exception 数据库查询错误
     */
    @OQL("select a from MailAccount a where userId=:1 and notempty(smtpServer) order by orderId")
    public abstract List<MailAccount> getAccountsForSmtp(Integer userId) throws Exception;

    /**
     * 获得所有的外部邮箱的ID，用于POP3自动接收服务
     *
     * @return 外部邮箱ID列表
     * @throws Exception 数据库查询错误
     */
    @OQL("select accountId from MailAccount")
    public abstract List<Integer> getAllAccountIds() throws Exception;

    /**
     * 根据bodyId查询邮件，用于跟踪邮件的接收情况
     *
     * @param bodyId 邮件体ID
     * @return 邮件列表，用mailId排序，即按发送的情况排序
     * @throws Exception 数据库查询异常
     */
    @OQL("select m from Mail m where bodyId=:1 and type=2 order by mailId")
    public abstract List<Mail> getMailsByBodyId(Long bodyId) throws Exception;

    /**
     * 根据用户Id获取标记 lfx
     *
     * @param userId 用户Id
     * @return 邮件标记列表
     * @throws Exception 数据库读取数据异常
     */
    @OQL("select m from MailMark m where userId=:1 and markType=1 order by orderId")
    public abstract List<MailMark> getUserMarks(Integer userId) throws Exception;

    /**
     * 获取系统定义的标记 lfx
     *
     * @return 邮件标记列表
     * @throws Exception 数据库读取数据异常
     */
    @OQL("select m from MailMark m where markType=0 order by orderId")
    public abstract List<MailMark> getSysMarks() throws Exception;

    /**
     * 根据用户Id获取目录 lfx
     *
     * @param userId 用户Id
     * @return 个人文件列表
     * @throws Exception 数据库读取数据异常
     */
    @OQL("select c from MailCatalog c where userId=:1 order by orderId")
    public abstract List<MailCatalog> getCatalogs(Integer userId) throws Exception;

    /**
     * 将邮件归档
     *
     * @param catalogId 个人文件夹ID
     * @param mailIds   邮件ID
     * @throws Exception 数据库读取数据异常
     */
    @OQLUpdate("update Mail set catalogId=:1,deleted=0 where mailId in :2")
    public abstract void catalog(Long catalogId, Long... mailIds) throws Exception;

    /**
     * 标记邮件
     *
     * @param mailIds 邮件ids
     * @param markId  标记id
     * @throws Exception 数据库更新数据操作异常
     */
    @OQLUpdate("update Mail set markId=:1 where mailId in :2")
    public abstract void mark(Long markId, Long... mailIds) throws Exception;

    /**
     * 取消邮件标记
     *
     * @param mailIds 邮件ids
     * @throws Exception 数据库更新数据操作异常
     */
    @OQLUpdate("update Mail set markId=null where mailId in :1")
    public abstract void cancelMark(Long... mailIds) throws Exception;

    /**
     * 标记邮件被删除
     *
     * @param mailIds 邮件ids
     * @throws Exception 数据库数据更新异常
     */
    @OQLUpdate("update Mail set deleted=1 where mailId in :1")
    public abstract void markDeleted(Long... mailIds) throws Exception;

    /**
     * 恢复邮件
     *
     * @param mailIds 邮件ids
     * @throws Exception 数据库数据更新异常
     */
    @OQLUpdate("update Mail set deleted=0 where mailId in :1")
    public abstract void restore(Long... mailIds) throws Exception;

    /**
     * 获得某个用户邮件某个目录的扩展信息
     *
     * @param userId    用户id
     * @param catalogId 邮件目录
     * @return 某个目录所占有的容量
     * @throws Exception 数据库查询异常
     */
    @OQL("select sum(mailSize) as usedSize,count(*) as mailCount,count(readTime is null) as notReadCount " +
            "from Mail where userId=:1 and catalogId=:2 and deleted=0")
    public abstract MailCatalogInfo getCatalogInfoWithCatalog(Integer userId, Long catalogId) throws Exception;

    @OQL("select sum(mailSize) as usedSize,count(*) as mailCount,count(readTime is null) as notReadCount " +
            "from Mail where userId=:1 and type=:2 and deleted=0 and catalogId is null")
    public abstract MailCatalogInfo getCatalogInfoWithType(Integer userId, MailType type) throws Exception;

    @OQL("select sum(mailSize) as usedSize,count(*) as mailCount,count(readTime is null) as notReadCount " +
            "from Mail where userId=:1 and deleted=1")
    public abstract MailCatalogInfo getCatalogInfoWithDeleted(Integer userId) throws Exception;

    /**
     * 删除用户的某个类型（不包含归档的）的所有邮件
     *
     * @param userId 用户id
     * @param type   邮件类型
     * @throws Exception 数据库更新操作异常
     */
    @OQLUpdate("update Mail set deleted=1 where userId=:1 and type=:2  and catalogId is null and deleted=0")
    public abstract void clearMailsWithType(Integer userId, MailType type) throws Exception;

    /**
     * 删除某个用户的某个目录下的所有邮件
     *
     * @param userId    用户id
     * @param catalogId 目录id
     * @throws Exception 数据库更新操作异常
     */
    @OQLUpdate("update Mail set deleted=1 where userId=:1 and catalogId=:2 and deleted=0")
    public abstract void clearMailsWithCatalog(Integer userId, Integer catalogId) throws Exception;

    /**
     * 根据用户id彻底删除已删除邮件邮件
     *
     * @param userId 用户id
     * @throws Exception 数据库更新操作异常
     */
    @OQLUpdate("delete Mail where userId=:1 and deleted=1 ")
    public abstract void clearDeletedMails(Integer userId) throws Exception;

    /**
     * 检查所选目录下是否有邮件
     *
     * @param catalogIds 栏目
     * @return 所选目录下邮件的数据流
     * @throws Exception 数据库数据查询异常
     */
    @OQL("select count(mailId) from Mail where catalogId in :1 and deleted=0")
    public abstract boolean checkCatalogsUsed(Long... catalogIds) throws Exception;

    public User getUser(Integer userId) throws Exception
    {
        return load(User.class, userId);
    }

    @GetByField("loginName")
    public abstract User getUserByLoginName(String loginName) throws Exception;

    @GetByField("sourceMail")
    public abstract User getUserBySourceMail(String address) throws Exception;


    /**
     * 取得需要通过smtp发送的邮件
     *
     * @return 需要通过smtp发送的邮件的Id
     * @throws Exception 数据库查询异常
     */
    @OQL("select mailId from Mail m where smtpSendState=0 and type in (1,4) and" +
            " sendTime>?1 and (smtpErrorTime is null or smtpErrorTime<?2)")
    public abstract List<Long> getMailIdsForSmtpSend(Date date, Date errorTime) throws Exception;

    /**
     * 获得某份邮件的全部Smtp发送记录
     *
     * @param bodyId 邮件体Id
     * @return smtp发送记录，按recordId排序，即发送顺序排序
     * @throws Exception 数据库查询异常
     */
    @OQL("select r from MailSmtpRecord r where bodyId=:1 order by recordId")
    public abstract List<MailSmtpRecord> getAllSmtpRecords(Long bodyId) throws Exception;

    /**
     * 获得某份邮件的待发送Smtp发送记录
     * 所谓待发送的记录是指那些还没有发送过，已经发送过并且发送错误的
     *
     * @param bodyId 邮件体Id
     * @return smtp发送记录
     * @throws Exception 数据库查询异常
     */
    @OQL("select r from MailSmtpRecord r where bodyId=:1 and state=0")
    public abstract List<MailSmtpRecord> getSmtpRecordsForSend(Long bodyId) throws Exception;

    @OQLUpdate("update Mail set readTime=:2 where mailId in :1")
    public abstract void setReaded(Long[] mailIds, Date date) throws Exception;

    @OQL("select s from MailSign s where userId=:1 order by orderId")
    public abstract List<MailSign> getSignsForUser(Integer userId) throws Exception;

    @OQL("select s from MailSign s where deptId=:1 order by orderId")
    public abstract List<MailSign> getSignsForDept(Integer deptId) throws Exception;

    @LoadByKey
    public abstract MailSign getSign(Integer signId) throws Exception;

    public void autoCatalog(Mail mail) throws Exception
    {
        if (mail.getType() == MailType.received)
        {
            MailCatalogConfig config;
            if (mail.getSender() != null)
            {
                config = getCatalogConfigWithSender(mail.getUserId(), mail.getSender());
            }
            else
            {
                String mailFrom = mail.getMailFrom();

                Receiver receiver = new Receiver(mailFrom);

                config = getCatalogConfigWithMailFrom(mail.getUserId(), receiver.getValue());
                if (config == null && !StringUtils.isEmpty(receiver.getName()))
                    config = getCatalogConfigWithMailFrom(mail.getUserId(), receiver.getName());
            }

            if (config != null)
                mail.setCatalogId(config.getCatalogId());
        }
    }

    @OQL("select c from MailCatalogConfig c where userId=:1 and sender=:2 and type=0")
    public abstract MailCatalogConfig getCatalogConfigWithSender(Integer userId, Integer sender) throws Exception;

    @OQL("select c from MailCatalogConfig c where userId=:1 and mailFrom=:2 and type=1")
    public abstract MailCatalogConfig getCatalogConfigWithMailFrom(Integer userId, String mailFrom) throws Exception;

    @OQLUpdate("delete from Mail m where mailId=:1 and type=2 and readTime is null")
    public abstract void backMail(Long mailId) throws Exception;

    @OQL("select m from Mail m where notified=0 and datediff(sysdate(),sendTime)<=1 limit 30")
    public abstract List<Mail> getNoNotifiedMails() throws Exception;
}
