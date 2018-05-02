package com.gzzm.portal.inquiry.in;

/**
 * 图片附件信息
 * @author sjy
 * @date 2017/6/6
 */
public class ImgAttachmentInfo
{
    private String attachmentName;

    //已转base64的附件
    private String attachmentContent;

    public String getAttachmentName()
    {
        return attachmentName;
    }

    public void setAttachmentName(String attachmentName)
    {
        this.attachmentName = attachmentName;
    }

    public String getAttachmentContent()
    {
        return attachmentContent;
    }

    public void setAttachmentContent(String attachmentContent)
    {
        this.attachmentContent = attachmentContent;
    }
}
