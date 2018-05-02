package com.gzzm.ods.receipt.meeting;

import com.gzzm.ods.receipt.Receipt;
import com.gzzm.platform.attachment.Attachment;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.*;
import net.cyan.thunwind.annotation.*;

import java.util.*;

/**
 * 会议回执的基本信息，由发文部门填写
 *
 * @author camel
 * @date 12-4-8
 */
@Entity(table = "ODRECEIPTMEETING", keys = "receiptId")
public class ReceiptMeeting
{
    @ColumnDescription(type = "number(11)")
    private Long receiptId;

    /**
     * 关联回执对象
     */
    @NotSerialized
    private Receipt receipt;

    /**
     * 会议名称
     */
    @Require
    @ColumnDescription(type = "varchar(250)")
    private String meetingName;

    /**
     * 会议地点
     */
    @Require
    @ColumnDescription(type = "varchar(500)")
    private String location;

    /**
     * 会议开始时间
     */
    private Date startTime;

    /**
     * 会议结束时间
     */
    @LargerThan("startTime")
    private Date endTime;

    private Long attachmentId;

    /**
     * 附件列表
     *
     * @see com.gzzm.platform.attachment.Attachment
     */
    @NotSerialized
    @EntityList(column = "ATTACHMENTID")
    private SortedSet<Attachment> attachments;

    public ReceiptMeeting()
    {
    }

    public Long getReceiptId()
    {
        return receiptId;
    }

    public void setReceiptId(Long receiptId)
    {
        this.receiptId = receiptId;
    }

    public Receipt getReceipt()
    {
        return receipt;
    }

    public void setReceipt(Receipt receipt)
    {
        this.receipt = receipt;
    }

    public String getMeetingName()
    {
        return meetingName;
    }

    public void setMeetingName(String meetingName)
    {
        this.meetingName = meetingName;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    public Date getStartTime()
    {
        return startTime;
    }

    public void setStartTime(Date startTime)
    {
        this.startTime = startTime;
    }

    public Date getEndTime()
    {
        return endTime;
    }

    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
    }

    public Long getAttachmentId()
    {
        return attachmentId;
    }

    public void setAttachmentId(Long attachmentId)
    {
        this.attachmentId = attachmentId;
    }

    public SortedSet<Attachment> getAttachments()
    {
        return attachments;
    }

    public void setAttachments(SortedSet<Attachment> attachments)
    {
        this.attachments = attachments;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof ReceiptMeeting))
            return false;

        ReceiptMeeting that = (ReceiptMeeting) o;

        return receiptId.equals(that.receiptId);
    }

    @Override
    public int hashCode()
    {
        return receiptId.hashCode();
    }

    @Override
    public String toString()
    {
        return meetingName;
    }
}
