package com.gzzm.portal.inquiry.in;

import java.util.*;

/**
 * @author sjy
 * @date 2017/6/6
 */
public class UploadInfo
{
    private Long inquiryId;

    private List<ImgAttachmentInfo> attachmentInfoList;

    public Long getInquiryId()
    {
        return inquiryId;
    }

    public void setInquiryId(Long inquiryId)
    {
        this.inquiryId = inquiryId;
    }

    public List<ImgAttachmentInfo> getAttachmentInfoList()
    {
        return attachmentInfoList;
    }

    public void setAttachmentInfoList(List<ImgAttachmentInfo> attachmentInfoList)
    {
        this.attachmentInfoList = attachmentInfoList;
    }
}
