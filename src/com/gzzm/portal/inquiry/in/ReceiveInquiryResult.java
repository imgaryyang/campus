package com.gzzm.portal.inquiry.in;

import com.gzzm.in.*;

/**
 * @author sjy
 * @date 2017/5/11
 */
public class ReceiveInquiryResult extends BooleanResult
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
