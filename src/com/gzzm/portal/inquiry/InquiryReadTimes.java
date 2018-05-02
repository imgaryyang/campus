package com.gzzm.portal.inquiry;

import net.cyan.thunwind.annotation.Entity;

/**
 * @author camel
 * @date 2016/5/23
 */
@Entity(table = "PLINQUIRYREADTIMES", keys = "inquiryId")
public class InquiryReadTimes
{
    private Long inquiryId;

    /**
     * 阅读次数
     */
    private Integer readTimes;

    public InquiryReadTimes()
    {
    }

    public Long getInquiryId()
    {
        return inquiryId;
    }

    public void setInquiryId(Long inquiryId)
    {
        this.inquiryId = inquiryId;
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

        if (!(o instanceof InquiryReadTimes))
            return false;

        InquiryReadTimes that = (InquiryReadTimes) o;

        return inquiryId.equals(that.inquiryId);
    }

    @Override
    public int hashCode()
    {
        return inquiryId.hashCode();
    }
}
