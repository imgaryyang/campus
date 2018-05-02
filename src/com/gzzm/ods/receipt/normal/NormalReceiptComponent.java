package com.gzzm.ods.receipt.normal;

import com.gzzm.ods.receipt.*;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

/**
 * @author camel
 * @date 12-4-8
 */
public class NormalReceiptComponent implements ReceiptComponent
{
    @Inject
    private static Provider<NormalReceiptDao> daoProvider;

    public NormalReceiptComponent()
    {
    }

    public String getType()
    {
        return "normal";
    }

    public String getEditUrl(Receipt receipt) throws Exception
    {
        NormalReceiptDao dao = daoProvider.get();
        NormalReceipt normalReceipt = dao.getNormalReceipt(receipt.getReceiptId());

        if (normalReceipt == null)
        {
            normalReceipt = new NormalReceipt();
            normalReceipt.setReceiptId(receipt.getReceiptId());
            dao.add(normalReceipt);
        }

        return null;
    }

    public String getFillUrl(Receipt receipt, Integer deptId, boolean readOnly)
    {
        String s = "/ods/receipt/normal/" + receipt.getReceiptId() + "/reply?deptId=" + deptId;

        if (readOnly)
            s += "&readOnly=true";

        return s;
    }

    public String getFillUrl(Long documentId, Integer deptId)
    {
        return "/ods/receipt/normal/reply?documentId=" + documentId + "&deptId=" + deptId;
    }

    public String getTrackUrl(Receipt receipt)
    {
        return "/ods/receipt/normal/items/query?receiptId=" + receipt.getReceiptId();
    }

    public void delete(Long receiptId) throws Exception
    {
        daoProvider.get().deleteReceipt(receiptId);
    }
}
