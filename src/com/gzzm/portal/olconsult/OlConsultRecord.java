package com.gzzm.portal.olconsult;

import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * User: wym
 * Date: 13-5-31
 * Time: 上午10:53
 * 咨询聊天记录
 */
@Entity(table = "PLOLCONSULTRECORD", keys = "recordId")
public class OlConsultRecord
{
    @Generatable(length = 9)
    private Integer recordId;

    private Integer consultId;

    @NotSerialized
    private OlConsult consult;

    @ColumnDescription(type = "varchar(500)")
    private String content;

    private Date chatTime;

    /**
     * 0表示咨询者，1表示回答者
     */
    @ColumnDescription(type = "number(1)")
    private Integer flag;

    public OlConsultRecord()
    {
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

    public OlConsult getConsult()
    {
        return consult;
    }

    public void setConsult(OlConsult consult)
    {
        this.consult = consult;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof OlConsultRecord))
            return false;

        OlConsultRecord that = (OlConsultRecord) o;

        return recordId.equals(that.recordId);
    }

    @Override
    public int hashCode()
    {
        return recordId.hashCode();
    }
}
