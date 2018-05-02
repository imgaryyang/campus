package com.gzzm.ods.receipt;

import com.gzzm.platform.attachment.AttachmentCrud;
import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.util.JoinType;
import net.cyan.nest.annotation.Inject;

/**
 * @author camel
 * @date 2016/6/14
 */
@Service
public abstract class ReceiptAttachmentCrud extends AttachmentCrud
{
    @Inject
    protected ReceiptService service;

    protected Long receiptId;

    protected boolean repliable;

    public ReceiptAttachmentCrud()
    {
        setDialog(false);
    }

    @Override
    public String getAlias()
    {
        return "attachment";
    }

    @Override
    protected void initOrders()
    {
        addOrderBy("attachment.dept.leftValue");
        super.initOrders();
    }

    public Long getReceiptId()
    {
        return receiptId;
    }

    public void setReceiptId(Long receiptId)
    {
        this.receiptId = receiptId;
    }

    protected abstract Long loadAttachmentId() throws Exception;

    @Service
    @ObjectResult
    public void reply() throws Exception
    {
        service.reply(receiptId, getDeptId());
    }

    @Override
    protected void beforeQuery() throws Exception
    {
        super.beforeQuery();

        if (getDeptId() == null)
        {
            join(ReceiptReply.class, "reply",
                    "reply.receiptId=:receiptId and replied=1 and reply.deptId=attachment.deptId", JoinType.inner);
        }
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();

        setAttachmentId(loadAttachmentId());

        if (getDeptId() == null)
        {
            setReadOnly(true);
        }
        else if (getDeptId() != null)
        {
            ReceiptReply reply = service.getDao().getReceiptReply(receiptId, getDeptId());
            if (reply.getReplied() != null && reply.getReplied())
                setReadOnly(true);
        }
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = (PageTableView) super.createListView();

        if (!isReadOnly())
        {
            if (repliable)
                view.addButton("确认反馈", "reply()");

            view.addButton(Buttons.sort());
        }

        view.importJs("/ods/receipt/attachment.js");

        return view;
    }
}
