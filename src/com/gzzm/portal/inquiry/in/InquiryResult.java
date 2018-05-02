package com.gzzm.portal.inquiry.in;

import com.gzzm.in.*;

import java.util.*;

/**
 * @author lfx
 * @date 17-4-21
 */
public class InquiryResult extends InterfaceResult
{
    private List<InquiryReply> inquiryReplys;

    public InquiryResult()
    {
    }

    public List<InquiryReply> getInquiryReplys()
    {
        return inquiryReplys;
    }

    public void setInquiryReplys(List<InquiryReply> inquiryReplys)
    {
        this.inquiryReplys = inquiryReplys;
    }
}
