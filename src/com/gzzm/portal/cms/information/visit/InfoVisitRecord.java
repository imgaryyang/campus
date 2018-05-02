package com.gzzm.portal.cms.information.visit;

import net.cyan.thunwind.annotation.*;

import java.util.*;

/**
 * @author sjy
 * @date 2018/3/2
 */
@Entity(table = "PLINFOVISITRECORD", keys = "recordId")
public class InfoVisitRecord
{
    @Generatable(length = 11)
    private Long recordId;

    @ColumnDescription(type = "number(11)")
    private Long informationId;

    private Date visitTime;

    @ColumnDescription(type = "varchar(20)")
    private String ip;

    public Long getInformationId()
    {
        return informationId;
    }

    public void setInformationId(Long informationId)
    {
        this.informationId = informationId;
    }

    public Date getVisitTime()
    {
        return visitTime;
    }

    public void setVisitTime(Date visitTime)
    {
        this.visitTime = visitTime;
    }

    public String getIp()
    {
        return ip;
    }

    public void setIp(String ip)
    {
        this.ip = ip;
    }

    public Long getRecordId()
    {
        return recordId;
    }

    public void setRecordId(Long recordId)
    {
        this.recordId = recordId;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof InfoVisitRecord))
        {
            return false;
        }

        InfoVisitRecord that = (InfoVisitRecord) o;

        return informationId.equals(that.informationId);

    }

    @Override
    public int hashCode()
    {
        return informationId.hashCode();
    }
}
