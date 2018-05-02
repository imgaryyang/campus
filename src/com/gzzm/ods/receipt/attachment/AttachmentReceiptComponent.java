package com.gzzm.ods.receipt.attachment;

import com.gzzm.ods.receipt.*;

/**
 * @author camel
 * @date 13-1-23
 */
public class AttachmentReceiptComponent implements ReceiptComponent
{
    public AttachmentReceiptComponent()
    {
    }

    public String getType()
    {
        return "attachment";
    }

    public String getEditUrl(Receipt receipt)
    {
        return null;
    }

    public String getFillUrl(Receipt receipt, Integer deptId, boolean readOnly) throws Exception
    {
        return "/ods/receipt/attachment?receiptId=" + receipt.getReceiptId() + "&deptId=" + deptId +
                "&readOnly=" + readOnly;
    }

    public String getFillUrl(Long documentId, Integer deptId) throws Exception
    {
        return null;
    }

    public String getTrackUrl(Receipt receipt) throws Exception
    {
        return "/ods/receipt/attachment?receiptId=" + receipt.getReceiptId();
    }

    public void delete(Long receiptId) throws Exception
    {
    }
}
