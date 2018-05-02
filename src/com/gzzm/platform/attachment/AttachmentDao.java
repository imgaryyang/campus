package com.gzzm.platform.attachment;

import net.cyan.commons.transaction.Transactional;
import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.*;

/**
 * 附件相关的操作
 *
 * @author camel
 * @date 2010-3-16
 */
public abstract class AttachmentDao extends GeneralDao
{
    public AttachmentDao()
    {
    }

    /**
     * 从序列化里获得一个附件ID
     *
     * @return 附件ID
     * @throws Exception 读取数据库异常
     */
    public Long getAttachmentId() throws Exception
    {
        return getId("PFATTACHMENTID", 12, Long.class);
    }

    /**
     * 从数据库里获取Attachment对象
     *
     * @param attachmentId 附件ID
     * @param attachmentNo 附件编号
     * @return Attachment对象
     * @throws Exception 读取数据库异常
     */
    public Attachment getAttachment(Long attachmentId, Integer attachmentNo) throws Exception
    {
        return load(Attachment.class, attachmentId, attachmentNo);
    }

    /**
     * 从数据库里加载同一附件ID的所有附件
     *
     * @param attachmentId 附件ID
     * @return 附件列表
     * @throws Exception 读取数据库异常
     */
    @OQL("select a from Attachment a where attachmentId=:1 order by orderId,attachmentNo")
    public abstract List<Attachment> getAttachments(Long attachmentId) throws Exception;

    @OQL("select a from Attachment a where attachmentId=:1 and deptId=:2 order by orderId,attachmentNo")
    public abstract List<Attachment> getAttachmentsByDept(Long attachmentId, Integer deptId) throws Exception;

    /**
     * 从数据库里加载同一附件ID的所有附件
     *
     * @param attachmentId  附件ID
     * @param attachmentNos 附件编号列表，只加载这些编号的附件，可以为null，表示加载所有附件
     * @return 附件列表
     * @throws Exception 读取数据库异常
     */
    @OQL("select a from Attachment a where attachmentId=:1 and attachmentNo in ?2")
    public abstract List<Attachment> getAttachments(Long attachmentId, Integer... attachmentNos) throws Exception;

    /**
     * 保存附件列表到数据库中
     *
     * @param attachmentId 附件ID，可以传入null，则新生成一个附件ID
     * @param attachments  附件列表
     * @return 如果attachmentId为null，则新生成一个一个附件ID，并返回，否返回原来的attachmentId
     * @throws Exception 读取数据库异常
     */
    @Transactional
    public Long save(Long attachmentId, Collection<Attachment> attachments) throws Exception
    {
        if (attachments != null && attachments.size() > 0)
        {
            int no = 0;
            if (attachmentId == null)
            {
                attachmentId = getAttachmentId();
            }
            else
            {
                no = getMaxAttachmentNo(attachmentId);
            }

            Date date = new Date();

            for (Attachment attachment : attachments)
            {
                attachment.setAttachmentId(attachmentId);
                attachment.setAttachmentNo(++no);
                attachment.setFileSize(attachment.getInputable().size());
                if (attachment.getUploadTime() == null)
                    attachment.setUploadTime(date);

                if (attachment.getFileName() == null)
                    attachment.setFileName(attachment.getAttachmentName());

                attachment.save();
                add(attachment);
            }
        }

        return attachmentId;
    }

    /**
     * 保存附件列表到数据库中，并自动生成一个附件ID
     *
     * @param attachments 附件列表
     * @return 新生成的附件ID
     * @throws Exception 异常
     */
    public Long save(Collection<Attachment> attachments) throws Exception
    {
        return save(null, attachments);
    }

    @OQL("select max(attachmentNo) from Attachment where attachmentId=:1")
    public abstract int getMaxAttachmentNo(Long attachmentId) throws Exception;

    /**
     * 获得某个附件列表的总大小
     *
     * @param attachmentId 附件ID
     * @return 附件列表中所有附件的大小之和
     * @throws Exception 异常
     */
    @OQL("select sum(fileSize) from Attachment a where attachmentId=:1")
    public abstract long getSize(Long attachmentId) throws Exception;

    @OQL("select count(*) from Attachment a where attachmentId=:1")
    public abstract int getCount(Long attachmentId) throws Exception;

    public void deleteAttachment(Long attachmentId, Integer attachmentNo) throws Exception
    {
        delete(Attachment.class, attachmentId, attachmentNo);
    }

    @GetByField("uuid")
    public abstract Attachment getAttachmentByUUID(String uuid) throws Exception;

    @OQL("select b from AttachmentBak b where attachmentId=:1 and attachmentNo=:2 order by saveTime desc")
    public abstract List<AttachmentBak> getBaks(Long attachmentId, Integer attachmentNo) throws Exception;

    public AttachmentBak getBak(Long bakId) throws Exception
    {
        return load(AttachmentBak.class, bakId);
    }
}
