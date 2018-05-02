package com.gzzm.ods.receipt.normal;

import com.gzzm.platform.organ.User;
import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.List;

/**
 * @author camel
 * @date 12-4-8
 */
public abstract class NormalReceiptDao extends GeneralDao
{
    public NormalReceiptDao()
    {
    }

    public NormalReceipt getNormalReceipt(Long receiptId) throws Exception
    {
        return load(NormalReceipt.class, receiptId);
    }

    @GetByField({"receiptId", "deptId", "userId"})
    public abstract NormalReceiptItem getItemByUserId(Long receiptId, Integer deptId,
                                                      Integer userId) throws Exception;

    @GetByField({"receiptId", "deptId"})
    public abstract List<NormalReceiptItem> getItems(Long receiptId, Integer deptId) throws Exception;

    public User getUser(Integer userId) throws Exception
    {
        return load(User.class, userId);
    }

    public void deleteReceipt(Long receiptId) throws Exception
    {
        delete(NormalReceipt.class, receiptId);
    }

    @OQLUpdate("update NormalReceiptItem set replied=1 where receiptId=:1 and deptId=:2")
    public abstract void makeItemReplied(Long receiptId, Integer deptId) throws Exception;
}
