package com.gzzm.portal.inquiry.in;

import net.cyan.commons.annotation.*;

import java.util.*;

/**
 * @author lfx
 * @date 17-4-21
 */
@ElementName("InquiryInfo")
public class InquiryInfo
{
    private String code;

    private String title;

    private String inquirerName;

    private String realName;

    private String orgCode;

    private Date sendTime;

    private String content;

    private Integer catalogId;

    private String phone;

    private String address;

    private String email;

    private String postcode;

    private ImgAttachmentInfo[] attachmentInfoList;

    public InquiryInfo()
    {
    }

    public ImgAttachmentInfo[] getAttachmentInfoList()
    {
        return attachmentInfoList;
    }

    public void setAttachmentInfoList(ImgAttachmentInfo[] attachmentInfoList)
    {
        this.attachmentInfoList = attachmentInfoList;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getInquirerName()
    {
        return inquirerName;
    }

    public void setInquirerName(String inquirerName)
    {
        this.inquirerName = inquirerName;
    }

    public String getRealName()
    {
        return realName;
    }

    public void setRealName(String realName)
    {
        this.realName = realName;
    }

    public String getOrgCode()
    {
        return orgCode;
    }

    public void setOrgCode(String orgCode)
    {
        this.orgCode = orgCode;
    }

    public Date getSendTime()
    {
        return sendTime;
    }

    public void setSendTime(Date sendTime)
    {
        this.sendTime = sendTime;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public Integer getCatalogId()
    {
        return catalogId;
    }

    public void setCatalogId(Integer catalogId)
    {
        this.catalogId = catalogId;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getPostcode()
    {
        return postcode;
    }

    public void setPostcode(String postcode)
    {
        this.postcode = postcode;
    }
}
