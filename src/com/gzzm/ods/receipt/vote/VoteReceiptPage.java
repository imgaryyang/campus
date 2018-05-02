package com.gzzm.ods.receipt.vote;

import com.gzzm.oa.vote.*;
import com.gzzm.ods.receipt.ReceiptBasePage;
import net.cyan.arachne.annotation.*;
import net.cyan.nest.annotation.Inject;

import java.util.List;

/**
 * 投票回执
 *
 * @author camel
 * @date 12-4-8
 */
@Service
public class VoteReceiptPage extends ReceiptBasePage
{
    @Inject
    private VoteDao voteDao;

    public VoteReceiptPage()
    {
    }

    @NotSerialized
    @Select(field = "receipt.linkId")
    public List<Vote> getVotes() throws Exception
    {
        if (receipt != null)
            return voteDao.getValidVotes(receipt.getDeptId(), null);

        return null;
    }

    @Service(url = "/ods/receipt/vote/{receiptId}")
    public String show() throws Exception
    {
        initDeptId();

        loadReceipt();

        return "votereceipt";
    }
}
