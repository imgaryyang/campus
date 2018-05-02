package com.gzzm.portal.cms.in;

import com.gzzm.portal.cms.information.InformationType;

import java.util.Date;

/**
 * @author Xrd
 * @date 2017/7/4 18:38
 */

public class ReceiveInformation
{
    private Long informationId;

    private byte[] photo;

    private String extName;

    private String title;

    private String content;

    private Integer channelId;

    private InformationType type;

    private String linkUrl;

    private String keywords;

    private String source;

    private String fileCode;

    private String subject;

    private String summary;

    private String orgCode;

    private String orgName;

    private Integer catalogId;

    private Date publishTime;

    private Integer deptId;

    private java.sql.Date validTime;

    private String lang;

    public ReceiveInformation()
    {
    }

    public Long getInformationId()
    {
        return informationId;
    }

    public void setInformationId(Long informationId)
    {
        this.informationId = informationId;
    }

    public byte[] getPhoto()
    {
        return photo;
    }

    public void setPhoto(byte[] photo)
    {
        this.photo = photo;
    }

    public String getExtName()
    {
        return extName;
    }

    public void setExtName(String extName)
    {
        this.extName = extName;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public Integer getChannelId()
    {
        return channelId;
    }

    public void setChannelId(Integer channelId)
    {
        this.channelId = channelId;
    }

    public InformationType getType()
    {
        return type;
    }

    public void setType(InformationType type)
    {
        this.type = type;
    }

    public String getLinkUrl()
    {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl)
    {
        this.linkUrl = linkUrl;
    }

    public String getKeywords()
    {
        return keywords;
    }

    public void setKeywords(String keywords)
    {
        this.keywords = keywords;
    }

    public String getSource()
    {
        return source;
    }

    public void setSource(String source)
    {
        this.source = source;
    }

    public String getFileCode()
    {
        return fileCode;
    }

    public void setFileCode(String fileCode)
    {
        this.fileCode = fileCode;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public String getSummary()
    {
        return summary;
    }

    public void setSummary(String summary)
    {
        this.summary = summary;
    }

    public String getOrgCode()
    {
        return orgCode;
    }

    public void setOrgCode(String orgCode)
    {
        this.orgCode = orgCode;
    }

    public String getOrgName()
    {
        return orgName;
    }

    public void setOrgName(String orgName)
    {
        this.orgName = orgName;
    }

    public Integer getCatalogId()
    {
        return catalogId;
    }

    public void setCatalogId(Integer catalogId)
    {
        this.catalogId = catalogId;
    }

    public Date getPublishTime()
    {
        return publishTime;
    }

    public void setPublishTime(Date publishTime)
    {
        this.publishTime = publishTime;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public java.sql.Date getValidTime()
    {
        return validTime;
    }

    public void setValidTime(java.sql.Date validTime)
    {
        this.validTime = validTime;
    }

    public String getLang()
    {
        return lang;
    }

    public void setLang(String lang)
    {
        this.lang = lang;
    }
}
