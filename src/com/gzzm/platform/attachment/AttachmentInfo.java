package com.gzzm.platform.attachment;

/**
 * @author camel
 * @date 13-10-16
 */
public class AttachmentInfo
{
    private Long attachmentId;

    private Integer attachmentNo;

    private String attachmentName;

    private String fileName;

    private String remark;

    private Long fileSize;

    public AttachmentInfo()
    {
    }

    public AttachmentInfo(Attachment attachment)
    {
        attachmentId = attachment.getAttachmentId();
        attachmentNo = attachment.getAttachmentNo();
        attachmentName = attachment.getAttachmentName();
        fileName = attachment.getFileName();
        remark = attachment.getRemark();
        fileSize = attachment.getFileSize();
    }

    public Long getAttachmentId()
    {
        return attachmentId;
    }

    public void setAttachmentId(Long attachmentId)
    {
        this.attachmentId = attachmentId;
    }

    public Integer getAttachmentNo()
    {
        return attachmentNo;
    }

    public void setAttachmentNo(Integer attachmentNo)
    {
        this.attachmentNo = attachmentNo;
    }

    public String getAttachmentName()
    {
        return attachmentName;
    }

    public void setAttachmentName(String attachmentName)
    {
        this.attachmentName = attachmentName;
    }

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public Long getFileSize()
    {
        return fileSize;
    }

    public void setFileSize(Long fileSize)
    {
        this.fileSize = fileSize;
    }

    public String getEncodedId()
    {
        return Attachment.encodeId(attachmentId);
    }
}
