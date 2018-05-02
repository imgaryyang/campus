package com.gzzm.ods.receipt.normal;

import com.gzzm.ods.receipt.Receipt;
import com.gzzm.platform.attachment.Attachment;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.*;

import java.util.SortedSet;

/**
 * 基本的回执，包括一个名单和一个附件列表
 *
 * @author camel
 * @date 12-4-8
 */
@Entity(table = "ODNORMALRECEIPT", keys = "receiptId")
public class NormalReceipt
{
    @ColumnDescription(type = "number(11)")
    private Long receiptId;

    /**
     * 关联回执对象
     */
    @NotSerialized
    private Receipt receipt;

    private Long attachmentId;

    /**
     * 附件列表
     *
     * @see com.gzzm.platform.attachment.Attachment
     */
    @NotSerialized
    @EntityList(column = "ATTACHMENTID")
    private SortedSet<Attachment> attachments;

    public NormalReceipt()
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

        if (!(o instanceof NormalReceipt))
            return false;

        NormalReceipt that = (NormalReceipt) o;

        return receiptId.equals(that.receiptId);
    }

    @Override
    public int hashCode()
    {
        return receiptId.hashCode();
    }
}
