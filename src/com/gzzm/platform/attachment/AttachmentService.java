package com.gzzm.platform.attachment;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.filestore.FileStoreService;
import net.cyan.commons.ext.CompressUtils;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.*;
import net.cyan.commons.util.io.CacheData;
import net.cyan.image.ImageZoomer;
import net.cyan.nest.annotation.Inject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

/**
 * 附件列表相关的service
 *
 * @author camel
 * @date 2010-4-22
 */
public class AttachmentService
{
    @Inject
    private static Provider<FileStoreService> fileStoreServiceProvider;

    @Inject
    private AttachmentDao dao;

    public AttachmentService()
    {
    }

    public Attachment getAttachment(Long attachmentId, Integer attachmentNo) throws Exception
    {
        return dao.getAttachment(attachmentId, attachmentNo);
    }

    public List<Attachment> getAttachments(Long attachmentId) throws Exception
    {
        return dao.getAttachments(attachmentId);
    }

    public Long save(Collection<Attachment> attachments) throws Exception
    {
        return dao.save(attachments);
    }

    public Long save(Collection<Attachment> attachments, String tag, Integer userId, Integer deptId)
            throws Exception
    {
        for (Attachment attachment : attachments)
        {
            if (tag != null)
                attachment.setTag(tag);
            if (userId != null)
                attachment.setUserId(userId);
            if (deptId != null)
                attachment.setDeptId(deptId);
        }

        return dao.save(attachments);
    }

    public Long clone(Long attachmentId) throws Exception
    {
        return clone(attachmentId, null, null, null);
    }

    /**
     * 复制附件
     *
     * @param attachmentId 附件列表ID
     * @param tag          新附件的类型
     * @param userId       当前操作的用户ID
     * @param deptId       当前操作的部门ID
     * @return 新的附件列表ID
     * @throws Exception 复制意见错误
     */
    public Long clone(Long attachmentId, String tag, Integer userId, Integer deptId) throws Exception
    {
        List<Attachment> olds = dao.getAttachments(attachmentId);

        if (olds.size() > 0)
        {
            String uuid = null;

            List<Attachment> news = new ArrayList<Attachment>(olds.size());
            for (Attachment oldAttachment : olds)
            {
                Attachment newAttachment = oldAttachment.cloneAttachment();

                if (tag != null)
                    newAttachment.setTag(tag);

                if (userId != null)
                    newAttachment.setUserId(userId);

                if (deptId != null)
                    newAttachment.setUserId(deptId);

                if (!StringUtils.isEmpty(newAttachment.getUuid()))
                {
                    if (uuid == null)
                        uuid = CommonUtils.uuid();
                    newAttachment.setUuid(uuid);
                }

                news.add(newAttachment);
            }

            return dao.save(news);
        }

        return null;
    }

    public void delete(Long attachmentId) throws Exception
    {
        for (Attachment attachment : dao.getAttachments(attachmentId))
        {
            attachment.delete();
            dao.delete(attachment);
        }
    }

    public long getSize(Long attachmentId) throws Exception
    {
        return dao.getSize(attachmentId);
    }

    public int getCount(Long attachmentId) throws Exception
    {
        return dao.getCount(attachmentId);
    }

    /**
     * 压缩附件列表，将整个列表中的附件打包到一个zip文件中
     *
     * @param attachmentId 附件列表ID
     * @param name         压缩包的名称
     * @return 压缩文件异常
     * @throws Exception 从数据库读取数据失败或者压缩文件失败
     */
    public InputFile zip(Long attachmentId, String name) throws Exception
    {
        List<Attachment> attachments = dao.getAttachments(attachmentId);
        if (attachments.size() == 1)
            return attachments.get(0).getInputFile();

        CacheData cache = new CacheData();

        try
        {
            CompressUtils.Compress zip = CompressUtils.createCompress("zip", cache);


            for (Attachment attachment : attachments)
                zip.addFile(attachment.getFileName(), attachment.getInputStream(), attachment.getUploadTime().getTime(),
                        attachment.getRemark());

            zip.close();

            return new InputFile(cache, name + ".zip");
        }
        catch (Exception ex)
        {
            //出错的时候清除缓存的文件
            try
            {
                cache.clear();
            }
            catch (Exception ex1)
            {
                //释放资源
            }

            throw ex;
        }
    }

    /**
     * 压缩附件列表，将整个列表中的附件打包到一个zip文件中
     *
     * @param attachmentId 附件列表ID
     * @return 压缩文件异常
     * @throws Exception 从数据库读取数据失败或者压缩文件失败
     */
    public InputStream zip(Long attachmentId) throws Exception
    {
        List<Attachment> attachments = dao.getAttachments(attachmentId);
        CacheData cache = new CacheData();

        try
        {
            CompressUtils.Compress zip = CompressUtils.createCompress("zip", cache);


            for (Attachment attachment : attachments)
                zip.addFile(attachment.getFileName(), attachment.getInputStream(), attachment.getUploadTime().getTime(),
                        attachment.getRemark());

            zip.close();

            return cache.getInputStream();
        }
        catch (Exception ex)
        {
            //出错的时候清除缓存的文件
            try
            {
                cache.clear();
            }
            catch (Exception ex1)
            {
                //释放资源
            }

            throw ex;
        }
    }

    /**
     * 将一个文件转存储到其他地方
     *
     * @param attachmentId 文件ID
     * @param attachmentNo 文件号
     * @param target       目标
     * @param userId       当前用户ID
     * @param deptId       当前部门ID
     * @param source       来源
     * @throws Exception 数据库读写数据失败或者读取文件错误
     */
    public void storeTo(Long attachmentId, Integer attachmentNo, String target, Integer userId, Integer deptId,
                        String source) throws Exception
    {
        Attachment attachment = dao.getAttachment(attachmentId, attachmentNo);

        fileStoreServiceProvider.get()
                .save(attachment.getInputFile(), userId, deptId, target, source, attachment.getRemark());
    }

    /**
     * 将整个文件列表转存储到其他地方
     *
     * @param attachmentId 文件ID
     * @param target       目标
     * @param userId       当前用户ID
     * @param deptId       当前部门ID
     * @param source       来源
     * @throws Exception 数据库读写数据失败或者读取文件错误
     */
    @Transactional
    public void storeAllTo(Long attachmentId, String target, Integer userId, Integer deptId, String source)
            throws Exception
    {
        for (Attachment attachment : dao.getAttachments(attachmentId))
        {
            fileStoreServiceProvider.get()
                    .save(attachment.getInputFile(), userId, deptId, target, source, attachment.getRemark());
        }
    }

    public Long loadForm(Long attachmentId, String[] fileIds, Integer userId, Integer deptId, String tag)
            throws Exception
    {
        List<InputFile> files = fileStoreServiceProvider.get().getFiles(fileIds, userId, deptId);

        List<Attachment> attachments = new ArrayList<Attachment>(files.size());

        for (InputFile file : files)
        {
            Attachment attachment = new Attachment();

            attachment.setAttachmentName(file.getName());
            attachment.setFileName(file.getName());
            attachment.setUserId(userId);
            attachment.setInputable(file.getInputable());
            attachment.setTag(tag);

            attachments.add(attachment);
        }

        AttachmentSaver saver = Tools.getBean(AttachmentSaver.class);

        saver.setAttachmentId(attachmentId);
        saver.setAttachments(attachments);

        saver.save();

        return saver.getAttachmentId();
    }

    public Attachment getAttachmentByUUID(String uuid) throws Exception
    {
        return dao.getAttachmentByUUID(uuid);
    }

    public InputStream getAttachmentThumb(Attachment attachment) throws Exception
    {
        String extName = IOUtils.getExtName(attachment.getFileName());
        if (IOUtils.isImage(extName))
        {
            InputStream thumb = attachment.getThumb();

            if (thumb == null)
            {
                int width = 108;
                int height = 60;

                InputStream in = attachment.getInputStream();
                byte[] bytes = IOUtils.streamToBytes(in);
                BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));

                if (image.getWidth() > width || image.getHeight() > height)
                {
                    ImageZoomer zoomer = new ImageZoomer(width, height, true, extName);
                    zoomer.setImage(image);

                    bytes = zoomer.toBytes();
                }

                Attachment attachment1 = new Attachment();
                attachment1.setAttachmentId(attachment.getAttachmentId());
                attachment1.setAttachmentNo(attachment.getAttachmentNo());
                attachment1.setThumb(new ByteArrayInputStream(bytes));
                dao.update(attachment);

                thumb = new ByteArrayInputStream(bytes);
            }

            return thumb;
        }
        else
        {
            return null;
        }
    }
}
