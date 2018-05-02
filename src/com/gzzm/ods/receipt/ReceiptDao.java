package com.gzzm.ods.receipt;

import com.gzzm.platform.organ.Dept;
import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.GeneralDao;

/**
 * 回执dao
 *
 * @author camel
 * @date 12-4-8
 */
public abstract class ReceiptDao extends GeneralDao
{
    public ReceiptDao()
    {
    }

    @LoadByKey
    public abstract Receipt getReceipt(Long receiptId) throws Exception;

    @GetByField("documentId")
    public abstract Receipt getReceiptByDocumentId(Long documentId) throws Exception;

    public void deleteReceipt(Long receiptId) throws Exception
    {
        delete(Receipt.class, receiptId);
    }

    @GetByKey
    public abstract ReceiptReply getReceiptReply(Long receiptId, Integer deptId) throws Exception;

    @GetByKey
    public abstract Dept getDept(Integer deptId) throws Exception;

    @OQL("select deptName from Dept where deptId=:1")
    public abstract String getDeptName(Integer deptId) throws Exception;

    @OQLUpdate("update ReceiptReply r set readed=1 where receiptId=:1")
    public abstract void setReplyReaded(Long receiptId) throws Exception;
}
