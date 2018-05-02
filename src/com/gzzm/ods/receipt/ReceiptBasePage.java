package com.gzzm.ods.receipt;

import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.platform.organ.AuthDeptTreeModel;
import net.cyan.arachne.annotation.*;
import net.cyan.nest.annotation.Inject;

import java.util.Collection;

/**
 * @author camel
 * @date 12-4-8
 */
@Service
public abstract class ReceiptBasePage
{
    public static final String DEPT_SELECT_TAPP = "receipt_dept_select";

    @Inject
    protected ReceiptService service;

    /**
     * 回执接收部门，或者填写回执的部门
     */
    protected Integer deptId;

    protected Integer businessDeptId;

    private AuthDeptTreeModel deptTree;

    @Inject
    protected UserOnlineInfo userOnlineInfo;

    protected Long receiptId;

    protected Receipt receipt;

    /**
     * 文档ID
     */
    protected Long documentId;

    @NotSerialized
    protected ReceiptReply reply;

    public ReceiptBasePage()
    {
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public Integer getBusinessDeptId()
    {
        return businessDeptId;
    }

    public void setBusinessDeptId(Integer businessDeptId)
    {
        this.businessDeptId = businessDeptId;
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

    public Long getDocumentId()
    {
        return documentId;
    }

    public void setDocumentId(Long documentId)
    {
        this.documentId = documentId;
    }

    public ReceiptReply getReply() throws Exception
    {
        if (reply == null)
        {
            reply = service.getDao().getReceiptReply(receiptId, deptId);

            if (reply == null)
            {
                reply = new ReceiptReply();
                reply.setReceiptId(receiptId);
                reply.setDeptId(deptId);
                reply.setReplied(false);
            }
        }

        return reply;
    }

    protected void loadReceipt() throws Exception
    {
        if (receiptId != null)
        {
            receipt = service.getDao().getReceipt(receiptId);
            if (receipt != null)
                documentId = receipt.getDocumentId();
        }
        else if (documentId != null)
        {
            receipt = service.getDao().getReceiptByDocumentId(documentId);
            if (receipt != null)
                receiptId = receipt.getReceiptId();
        }
    }

    @Service
    @ObjectResult
    public void save() throws Exception
    {
        service.getDao().update(receipt);
    }

    protected void reply() throws Exception
    {
        service.reply(receiptId, deptId);
    }

    @Select(field = {"selectedDeptId", "receipt.deptId"})
    public AuthDeptTreeModel getDeptTree()
    {
        if (deptTree == null)
        {
            deptTree = new AuthDeptTreeModel();
            deptTree.setFull(true);
            deptTree.setAppId(DEPT_SELECT_TAPP);
        }
        return deptTree;
    }

    protected void initDeptId()
    {
        Collection<Integer> authDeptIds = userOnlineInfo.getAuthDeptIds(DEPT_SELECT_TAPP);
        if (authDeptIds != null && authDeptIds.size() == 0)
            deptId = businessDeptId;
    }
}
