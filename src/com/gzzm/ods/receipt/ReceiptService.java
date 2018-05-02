package com.gzzm.ods.receipt;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.message.Message;
import net.cyan.nest.annotation.Inject;

import java.util.Date;

/**
 * @author camel
 * @date 2016/6/14
 */
public class ReceiptService
{
    @Inject
    private ReceiptDao dao;

    public ReceiptService()
    {
    }

    public ReceiptDao getDao()
    {
        return dao;
    }

    public void reply(Long receiptId, Integer deptId) throws Exception
    {
        ReceiptReply reply = new ReceiptReply();
        reply.setReceiptId(receiptId);
        reply.setDeptId(deptId);
        reply.setReplied(true);
        reply.setReplyTime(new Date());
        reply.setReaded(false);

        dao.save(reply);

        Receipt receipt = dao.getReceipt(receiptId);

        Message message = new Message();
        message.setFromDeptId(deptId);
        message.setToDeptId(receipt.getDeptId());
        message.setUserId(receipt.getCreator());
        message.setApp("od_receipt");
        message.setUrl("/ods/receipt/track/" + receiptId);
        message.setMessage(Tools.getMessage("ods.receipt.reply", dao.getReceiptReply(receiptId, deptId)));

        message.send();
    }
}
