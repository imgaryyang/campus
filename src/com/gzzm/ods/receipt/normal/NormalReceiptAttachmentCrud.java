package com.gzzm.ods.receipt.normal;

import com.gzzm.ods.receipt.ReceiptAttachmentCrud;
import net.cyan.arachne.annotation.Service;
import net.cyan.nest.annotation.Inject;

/**
 * @author camel
 * @date 2016/6/14
 */
@Service(url = "/ods/receipt/normal/attachment")
public class NormalReceiptAttachmentCrud extends ReceiptAttachmentCrud
{
    @Inject
    private NormalReceiptDao normalReceiptDao;

    public NormalReceiptAttachmentCrud()
    {
    }

    @Override
    protected Long loadAttachmentId() throws Exception
    {
        NormalReceipt receipt = normalReceiptDao.getNormalReceipt(getReceiptId());

        return receipt.getAttachmentId();
    }
}
