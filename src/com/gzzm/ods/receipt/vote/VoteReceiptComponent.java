package com.gzzm.ods.receipt.vote;

import com.gzzm.ods.receipt.*;

/**
 * @author camel
 * @date 12-4-8
 */
public class VoteReceiptComponent implements ReceiptComponent
{
    public VoteReceiptComponent()
    {
    }

    public String getType()
    {
        return "vote";
    }

    public String getEditUrl(Receipt receipt)
    {
        return "/ods/receipt/vote/" + receipt.getReceiptId();
    }

    public String getFillUrl(Receipt receipt, Integer deptId, boolean readOnly)
    {
        String s = "/oa/vote/VotePage?voteId=" + receipt.getLinkId() + "&deptId=" + deptId;

        if (readOnly)
            s += "&readOnly=true";

        return s;
    }

    public String getFillUrl(Long documentId, Integer deptId)
    {
        return null;
    }

    public String getTrackUrl(Receipt receipt)
    {
        return "/oa/vote/items?voteId=" + receipt.getLinkId();
    }

    public void delete(Long receiptId) throws Exception
    {
    }
}
