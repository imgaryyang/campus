package com.gzzm.platform.visit;

import net.cyan.thunwind.annotation.*;

/**
 * @author camel
 * @date 2015/11/2
 */
@Entity(table = "PFVISIT", keys = "visitId")
public class Visit
{
    @Generatable(length = 9)
    private Integer visitId;

    @Index(unique = true)
    @ColumnDescription(type = "varchar(250)", nullable = false)
    private String visitName;

    public Visit()
    {
    }

    public Integer getVisitId()
    {
        return visitId;
    }

    public void setVisitId(Integer visitId)
    {
        this.visitId = visitId;
    }

    public String getVisitName()
    {
        return visitName;
    }

    public void setVisitName(String visitName)
    {
        this.visitName = visitName;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof Visit))
            return false;

        Visit visit = (Visit) o;

        return visitId.equals(visit.visitId);
    }

    @Override
    public int hashCode()
    {
        return visitId.hashCode();
    }
}
