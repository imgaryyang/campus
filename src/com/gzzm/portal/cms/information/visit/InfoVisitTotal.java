package com.gzzm.portal.cms.information.visit;

import net.cyan.thunwind.annotation.*;

/**
 * @author sjy
 * @date 2018/3/2
 */
@Entity(table = "PLINFOVISITTOTAL", keys = "informationId")
public class InfoVisitTotal
{
    @ColumnDescription(type = "number(11)")
    private Long informationId;

    @ColumnDescription(type = "number(7)")
    private Integer visitCount;

    @ColumnDescription(type = "number(7)")
    private Integer clickCount;

    public Long getInformationId()
    {
        return informationId;
    }

    public void setInformationId(Long informationId)
    {
        this.informationId = informationId;
    }

    public Integer getVisitCount()
    {
        return visitCount;
    }

    public void setVisitCount(Integer visitCount)
    {
        this.visitCount = visitCount;
    }

    public Integer getClickCount()
    {
        return clickCount;
    }

    public void setClickCount(Integer clickCount)
    {
        this.clickCount = clickCount;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof InfoVisitTotal))
        {
            return false;
        }

        InfoVisitTotal that = (InfoVisitTotal) o;

        return informationId.equals(that.informationId);

    }

    @Override
    public int hashCode()
    {
        return informationId.hashCode();
    }
}
