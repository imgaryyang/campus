package com.gzzm.portal.cms.information;

import net.cyan.thunwind.annotation.*;

/**
 * 记录每个信息的阅读次数
 *
 * @author camel
 * @date 13-12-12
 */
@Entity(table = "PLINFORMATIONREADTIMES", keys = "informationId")
public class InformationReadTimes
{
    /**
     * 信息id
     */
    private Long informationId;

    /**
     * 阅读次数
     */
    private Integer readTimes;

    public InformationReadTimes()
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

    public Integer getReadTimes()
    {
        return readTimes;
    }

    public void setReadTimes(Integer readTimes)
    {
        this.readTimes = readTimes;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof InformationReadTimes))
            return false;

        InformationReadTimes that = (InformationReadTimes) o;

        return informationId.equals(that.informationId);
    }

    @Override
    public int hashCode()
    {
        return informationId.hashCode();
    }
}
