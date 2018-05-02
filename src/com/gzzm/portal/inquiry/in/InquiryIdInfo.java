package com.gzzm.portal.inquiry.in;

import net.cyan.commons.annotation.*;

/**
 * @author sjy
 * @date 2017/5/11
 */
@ElementName("InquiryIdInfo")
public class InquiryIdInfo
{
    private Long inquiryId;

    public Long getInquiryId ()
    {
        return inquiryId;
    }

    public void setInquiryId (Long inquiryId)
    {
        this.inquiryId = inquiryId;
    }
}
