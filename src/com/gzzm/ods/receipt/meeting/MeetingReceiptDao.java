package com.gzzm.ods.receipt.meeting;

import com.gzzm.platform.organ.User;
import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.List;

/**
 * @author camel
 * @date 12-4-8
 */
public abstract class MeetingReceiptDao extends GeneralDao
{
    public MeetingReceiptDao()
    {
    }

    public ReceiptMeeting getMeeting(Long receiptId) throws Exception
    {
        return load(ReceiptMeeting.class, receiptId);
    }

    @GetByField({"receiptId", "deptId", "userId"})
    public abstract ReceiptMeetingItem getItemByUserId(Long receiptId, Integer deptId, Integer userId) throws Exception;

    @GetByField({"receiptId", "deptId"})
    public abstract List<ReceiptMeetingItem> getItems(Long receiptId, Integer deptId) throws Exception;

    public User getUser(Integer userId) throws Exception
    {
        return load(User.class, userId);
    }

    public void deleteReceipt(Long receiptId) throws Exception
    {
        delete(ReceiptMeeting.class, receiptId);
    }

    @OQLUpdate("update ReceiptMeetingItem set replied=1 where receiptId=:1 and deptId=:2")
    public abstract void makeItemReplied(Long receiptId, Integer deptId) throws Exception;
}
