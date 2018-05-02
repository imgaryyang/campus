package com.gzzm.portal.inquiry.in;

import net.cyan.thunwind.annotation.*;

import java.util.*;

/**
 * @author lfx
 * @date 17-4-21
 */
@Entity(table = "PLINQUIRY_IN", keys = "inquiryId")
public class InquiryInterface
{
    private Long inquiryId;

    private String code;

    private Date sendTime;

    private Integer deptId;

    @ColumnDescription(defaultValue = "0")
    private boolean receive;

    private Date receiveTime;

    public InquiryInterface()
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

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public Date getSendTime()
    {
        return sendTime;
    }

    public void setSendTime(Date sendTime)
    {
        this.sendTime = sendTime;
    }

    public boolean isReceive()
    {
        return receive;
    }

    public void setReceive(boolean receive)
    {
        this.receive = receive;
    }

    public Date getReceiveTime()
    {
        return receiveTime;
    }

    public void setReceiveTime(Date receiveTime)
    {
        this.receiveTime = receiveTime;
    }

    @Override
    public boolean equals(Object o)
    {

        if (this == o) return true;
        if (!(o instanceof InquiryInterface)) return false;

        InquiryInterface that = (InquiryInterface) o;

        if (inquiryId != null ? !inquiryId.equals(that.inquiryId) : that.inquiryId != null) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        return inquiryId != null ? inquiryId.hashCode() : 0;
    }
}
