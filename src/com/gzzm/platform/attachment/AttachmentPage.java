package com.gzzm.platform.attachment;

import com.gzzm.platform.commons.archive.ArchiveUtils;
import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.activex.OfficeUtils;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.*;
import net.cyan.commons.util.io.DownloadFile;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 附件相关的访问入口
 *
 * @author camel
 * @date 2010-4-1
 */
@Service
public class AttachmentPage
{
    @Inject
    private AttachmentService service;

    @Inject
    private UserOnlineInfo userOnlineInfo;

    /**
     * 要上传的文件
     */
    private InputFile file;

    private FileType fileType;

    public AttachmentPage()
    {
    }

    public InputFile getFile()
    {
        return file;
    }

    public void setFile(InputFile file)
    {
        this.file = file;
    }

    public FileType getFileType()
    {
        return fileType;
    }

    public void setFileType(FileType fileType)
    {
        this.fileType = fileType;
    }

    @Service(url = "/attachments/{$0}")
    public List<Attachment> getAttachments(String encodedId) throws Exception
    {
        return service.getAttachments(Attachment.decodeId(encodedId));
    }

    /**
     * 下载一个文件
     *
     * @param encodedId    编码加密后的附件ID，或者uuid
     * @param attachmentNo 文件号
     * @return 文件信息
     * @throws Exception 从数据库获取数据失败或者读取文件错误
     */
    @Service(url = {"/attachment/{$0}/{$1}", "/attachment/{$0}/{$1}.{ext}", "/attachment/{$0}/{$1}/{name}.{ext}"})
    public InputFile down(String encodedId, Integer attachmentNo, String contentType, Boolean show) throws Exception
    {
        Attachment attachment = service.getAttachmentByUUID(encodedId);

        if (attachment == null)
            attachment = service.getAttachment(Attachment.decodeId(encodedId), attachmentNo);

        return attachment.getInputFile(contentType, show == null ? null : !show);
    }

    @Service(url = {"/attachment/thumb/{$0}/{$1}/{name}.{ext}", "/attachment/{$0}/{$1}/thumb"})
    public InputFile thumb(String encodedId, Integer attachmentNo) throws Exception
    {
        Attachment attachment = service.getAttachmentByUUID(encodedId);

        if (attachment == null)
            attachment = service.getAttachment(Attachment.decodeId(encodedId), attachmentNo);


        return new DownloadFile(service.getAttachmentThumb(attachment), attachment.getFileName(), false);
    }

    /**
     * 将整个文件列表打包下载
     *
     * @param encodedId 编码加密后的附件ID
     * @param name      文件名称
     * @return 打包后的zip文件信息
     * @throws Exception 从数据库获取数据失败，或者读取文件错误，或者文件打包错误
     */
    @Service(url = {"/attachment/{$0}?name={$1}", "/attachment/{$0}"})
    public InputFile downAll(String encodedId, String name) throws Exception
    {
        if (name == null)
            name = "attachments";

        return service.zip(Attachment.decodeId(encodedId), name);
    }

    /**
     * 将文件以html格式显示
     *
     * @param encodedId    编码加密后的附件ID
     * @param attachmentNo 文件号
     * @return 文件信息
     * @throws Exception 从数据库获取数据失败或者读取文件错误
     */
    @Redirect
    @Service(url = "/attachment/{$0}/{$1}/html")
    public String html(String encodedId, Integer attachmentNo) throws Exception
    {
        Attachment attachment = service.getAttachment(Attachment.decodeId(encodedId), attachmentNo);

        String ext = IOUtils.getExtName(attachment.getFileName());

        if (!StringUtils.isEmpty(ext))
        {
            if (OfficeUtils.canChangeToHtml(ext))
            {
                return OfficeUtils.toHtml(attachment.getInputable(), ext);
            }
            else if (ArchiveUtils.isArchive(ext))
            {
                return ArchiveUtils.archive(attachment.getInputFile());
            }
        }

        return null;
    }

    /**
     * 将一个文件转存储到其他地方
     *
     * @param encodedId    编码过的附件ID
     * @param attachmentNo 文件号
     * @param target       目标
     * @param source       来源
     * @throws Exception 数据库读写数据失败或者读取文件错误
     */
    @ObjectResult
    @Service(url = "/attachment/{$0}/{$1}/storeTo/{$2}?source={$3}", method = HttpMethod.post)
    public void storeTo(String encodedId, Integer attachmentNo, String target, String source) throws Exception
    {
        service.storeTo(Attachment.decodeId(encodedId), attachmentNo, target, userOnlineInfo.getUserId(),
                userOnlineInfo.getDeptId(), source);
    }

    /**
     * 将整个文件列表存储到其他地方
     *
     * @param encodedId 编码过的附件ID
     * @param target    目标
     * @param source    来源
     * @throws Exception 数据库读写数据失败或者读取文件错误
     */
    @ObjectResult
    @Service(url = "/attachment/{$0}/storeTo/{$1}?source={$2}", method = HttpMethod.post)
    public void storeAllTo(String encodedId, String target, String source) throws Exception
    {
        service.storeAllTo(Attachment.decodeId(encodedId), target, userOnlineInfo.getUserId(),
                userOnlineInfo.getDeptId(), source);
    }

    /**
     * 保存上传的文件
     *
     * @return 文件的uuid
     * @throws Exception io异常或者数据库异常
     */
    @Service(method = HttpMethod.post, url = "/attachments/save")
    @ObjectResult
    public String save() throws Exception
    {
        Attachment attachment = new Attachment();
        attachment.setAttachmentName(file.getName());
        attachment.setInputable(file.getInputable());
        attachment.setFileType(fileType);
        attachment.setUuid(CommonUtils.uuid());

        service.save(Collections.singleton(attachment), "file", userOnlineInfo.getUserId(), userOnlineInfo.getDeptId());

        return attachment.getUuid();
    }

    /**
     * 打开文件上传的页面
     *
     * @return 文件上传的页面
     */
    @Service(url = "/attachments/upload")
    public String upload()
    {
        if (fileType == null)
            fileType = FileType.attachment;
        return "upload";
    }

    /**
     * 打开上传图片的页面
     *
     * @return 上传图片的页面
     */
    @Service(url = "/attachments/image")
    public String image()
    {
        fileType = FileType.image;
        return "image";
    }

    /**
     * 打开上传视频的页面
     *
     * @return 上传图片的页面
     */
    @Service(url = "/attachments/video")
    public String video()
    {
        fileType = FileType.video;
        return "video";
    }
}
