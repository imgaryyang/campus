package com.gzzm.portal.olconsult;

import java.io.Serializable;
import java.util.Date;

/**
 * 在线咨询消息
 *
 * @author camel
 * @date 13-6-4
 */
public class ConsultRecordMessage implements Serializable
{
    private Integer recordId;

    private Integer consultId;

    private String content;

    private Date chatTime;

    private Integer flag;

    public ConsultRecordMessage()
    {
    }

    public ConsultRecordMessage(OlConsultRecord record)
    {
        this.recordId = record.getRecordId();
        this.consultId = record.getConsultId();
        this.content = record.getContent();
        this.chatTime = record.getChatTime();
        this.flag = record.getFlag();
    }

    public Integer getRecordId()
    {
        return recordId;
    }

    public void setRecordId(Integer recordId)
    {
        this.recordId = recordId;
    }

    public Integer getConsultId()
    {
        return consultId;
    }

    public void setConsultId(Integer consultId)
    {
        this.consultId = consultId;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public Date getChatTime()
    {
        return chatTime;
    }

    public void setChatTime(Date chatTime)
    {
        this.chatTime = chatTime;
    }

    public Integer getFlag()
    {
        return flag;
    }

    public void setFlag(Integer flag)
    {
        this.flag = flag;
    }
}
