package com.gzzm.portal.inquiry.in;

import com.gzzm.in.*;

import java.util.*;

/**
 * @author sjy
 * @date 2017/5/11
 */
public class QueryInquiryResult extends InterfaceResult
{

    private String code;

    private String title;

    private Integer state;

    private String replyContent;

    private Date endTime;

    private String orgCode;

    private String attachmentUrl;

    public String getAttachmentUrl()
    {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl)
    {
        this.attachmentUrl = attachmentUrl;
    }

    public String getCode ()
    {
        return code;
    }

    public void setCode (String code)
    {
        this.code = code;
    }

    public String getTitle ()
    {
        return title;
    }

    public void setTitle (String title)
    {
        this.title = title;
    }

    public Integer getState ()
    {
        return state;
    }

    public void setState (Integer state)
    {
        this.state = state;
    }

    public String getReplyContent ()
    {
        return replyContent;
    }

    public void setReplyContent (String replyContent)
    {
        this.replyContent = replyContent;
    }

    public Date getEndTime ()
    {
        return endTime;
    }

    public void setEndTime (Date endTime)
    {
        this.endTime = endTime;
    }

    public String getOrgCode ()
    {
        return orgCode;
    }

    public void setOrgCode (String orgCode)
    {
        this.orgCode = orgCode;
    }
}
