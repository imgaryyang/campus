package com.gzzm.ods.receipt.meeting;

import com.gzzm.ods.receipt.*;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

/**
 * @author camel
 * @date 12-4-8
 */
public class MeetingReceiptComponent implements ReceiptComponent
{
    @Inject
    private static Provider<MeetingReceiptDao> daoProvider;

    public MeetingReceiptComponent()
    {
    }

    public String getType()
    {
        return "meeting";
    }

    public String getEditUrl(Receipt receipt)
    {
        return "/ods/receipt/meeting/" + receipt.getReceiptId();
    }

    public String getFillUrl(Receipt receipt, Integer deptId, boolean readOnly)
    {
        String s = "/ods/receipt/meeting/" + receipt.getReceiptId() + "/reply?deptId=" + deptId;

        if (readOnly)
            s += "&readOnly=true";

        return s;
    }

    public String getFillUrl(Long documentId, Integer deptId)
    {
        return "/ods/receipt/meeting/reply?documentId=" + documentId + "&deptId=" + deptId;
    }

    public String getTrackUrl(Receipt receipt)
    {
        return "/ods/receipt/meeting/items/query?receiptId=" + receipt.getReceiptId();
    }

    public void delete(Long receiptId) throws Exception
    {
        daoProvider.get().deleteReceipt(receiptId);
    }
}
