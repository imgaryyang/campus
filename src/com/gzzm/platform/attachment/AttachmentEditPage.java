package com.gzzm.platform.attachment;

import com.gzzm.platform.annotation.UserId;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 附件编辑的页面
 *
 * @author camel
 * @date 13-11-17
 */
@Service
public class AttachmentEditPage
{
    @Inject
    private AttachmentDao dao;

    private Long attachmentId;

    private String encodedId;

    private Integer attachmentNo;

    @NotSerialized
    private Attachment attachment;

    /**
     * 接收客户端传过来的文件
     */
    @NotSerialized
    protected Inputable content;

    @UserId
    private Integer userId;

    private boolean restoreable = true;

    public AttachmentEditPage()
    {
    }

    public Long getAttachmentId() throws Exception
    {
        if (attachmentId == null)
            attachmentId = Attachment.decodeId(encodedId);

        return attachmentId;
    }

    public String getEncodedId()
    {
        return encodedId;
    }

    public void setEncodedId(String encodedId)
    {
        this.encodedId = encodedId;
    }

    public Integer getAttachmentNo()
    {
        return attachmentNo;
    }

    public void setAttachmentNo(Integer attachmentNo)
    {
        this.attachmentNo = attachmentNo;
    }

    public Inputable getContent()
    {
        return content;
    }

    public void setContent(Inputable content)
    {
        this.content = content;
    }

    public boolean isRestoreable(AttachmentBak bak)
    {
        if (!restoreable)
            return false;

        //遇到第一个不是自己保存的文件，后面的文件不允许恢复
        if (!bak.getUserId().equals(userId))
            restoreable = false;

        return restoreable;
    }

    public Attachment getAttachment() throws Exception
    {
        if (attachment == null)
            attachment = dao.getAttachment(getAttachmentId(), getAttachmentNo());

        return attachment;
    }

    public String getFileName() throws Exception
    {
        return getAttachment().getFileName();
    }

    public String getFileType() throws Exception
    {
        return IOUtils.getExtName(getFileName());
    }

    @NotSerialized
    public List<AttachmentBak> getBaks() throws Exception
    {
        return dao.getBaks(getAttachmentId(), getAttachmentNo());
    }

    @Service(url = "/attachment/{encodedId}/{attachmentNo}/edit")
    public String show() throws Exception
    {
        return "edit";
    }

    @Service(url = "/attachment/{encodedId}/{attachmentNo}/save", method = HttpMethod.post)
    @ObjectResult
    @Transactional
    public void save() throws Exception
    {
        Attachment attachment = getAttachment();

        AttachmentBak bak = new AttachmentBak();
        bak.setAttachmentId(getAttachmentId());
        bak.setAttachmentNo(getAttachmentNo());
        bak.setFileName(attachment.getFileName());
        bak.setFileSize(attachment.getFileSize());
        bak.setTag(attachment.getTag());
        bak.setUserId(userId);
        bak.setSaveTime(new Date());
        bak.setContent(attachment.getInputable());

        dao.add(bak);

        String fileName = attachment.getFileName();
        int index = fileName.lastIndexOf('.');
        if (index > 0)
        {
            String extName = fileName.substring(index + 1);
            String extName1 = null;

            if (extName.equalsIgnoreCase("docx"))
            {
                extName1 = "doc";
            }
            else if (extName.equalsIgnoreCase("xlsx"))
            {
                extName1 = "xls";
            }

            if (extName1 != null)
            {
                fileName = fileName.substring(0, index + 1) + extName1;
                attachment.setFileName(fileName);
            }
        }

//        attachment.setUserId(userId);
//        attachment.setUploadTime(new Date());
        attachment.setFileSize(content.size());
        attachment.setInputable(content);
        attachment.save();

        dao.update(attachment);
    }

    @Service(url = "/attachment/{encodedId}/{attachmentNo}/baks")
    public String showBaks() throws Exception
    {
        return "baks";
    }

    @Service(url = "/attachment/{encodedId}/{attachmentNo}/bak/{$0}")
    public InputFile downBak(Long bakId) throws Exception
    {
        Long attachmentId = getAttachmentId();

        AttachmentBak bak = dao.getBak(bakId);

        if (!attachmentId.equals(bak.getAttachmentId()))
            return null;

        return new InputFile(bak.getContent(), bak.getFileName());
    }
}
