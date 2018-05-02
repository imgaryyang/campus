package com.gzzm.ods.dispatch;

import com.gzzm.ods.exchange.*;
import com.gzzm.ods.receipt.*;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.message.Message;
import com.gzzm.platform.organ.Dept;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.CollectionUtils;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * @author LDP
 * @date 2017/7/24
 */
@Service
public class DispatchRecordService
{
    @Inject
    private DispatchRecordDao dao;

    public DispatchRecordService()
    {
    }

    @Service(url = "/ods/dispatch/sendtodispatch")
    public void sendToDispatchRecord() throws Exception
    {
        //先清除已撤销的记录
        List<Send> uselessSendList = dao.getUselessRecord();
        if(CollectionUtils.isNotEmpty(uselessSendList))
        {
            for (Send s : uselessSendList)
            {
                deleteBySendId(s.getSendId());
            }
        }

        List<Send> sends = dao.getNotFinishRecord();
        if(CollectionUtils.isEmpty(sends)) return;

        for (Send s : sends)
        {
            Receipt receipt = dao.getReceiptByDocumentId(s.getDocumentId());

            DispatchRecord record = dao.getRecordBySendId(s.getSendId());
            if(record == null)
            {
                record = new DispatchRecord();
                record.setSendId(s.getSendId());
                record.setSendNumber(s.getDocument().getSendNumber());
                record.setTitle(s.getDocument().getTitle());
                record.setDeptName(s.getDept().getDeptName());
                record.setSendTime(s.getSendTime());
                record.setCreatorId(s.getCreator());
                record.setCreatorName(s.getCreatorUser().getUserName());
                record.setCreateDeptId(s.getDeptId());
                record.setLimitDate(receipt.getDeadline());
                record.setWarningDate(receipt.getWarningTime());
                record.setReceiptId(receipt.getReceiptId());

                dao.add(record);
            }

            List<Dept> receiptDepts = receipt.getReceiptDepts();

            List<Integer> deptIds = new ArrayList<Integer>();
            if(CollectionUtils.isNotEmpty(receiptDepts))
            {
                for (Dept d : receiptDepts)
                {
                    DispatchItem item = dao.getItem(record.getRecordId(), d.getDeptId());

                    ReceiveBase receiveBase = dao.getReceive(s.getDocumentId(), d.getDeptId());
                    if(receiveBase == null || receiveBase.getState().ordinal() > 3)
                    {
                        if(item != null) dao.delete(item);

                        continue;
                    }

                    deptIds.add(d.getDeptId());

                    if(item == null)
                    {
                        item = new DispatchItem();
                        item.setRecordId(record.getRecordId());
                        item.setDeptId(d.getDeptId());
                    }

                    item.setDeptName(d.getDeptName());

                    ReceiptReply reply = dao.getReply(receipt.getReceiptId(), d.getDeptId());
                    if(reply != null && reply.getReplyTime() != null)
                    {
                        item.setUserName(receiveBase.getReceiverName());
                        item.setPhone(receiveBase.getReceiverPhone());
                        item.setReplayTime(reply.getReplyTime());
                    }

                    dao.save(item);
                }
            }

            List<DispatchItem> items = dao.getItems(record.getRecordId(), deptIds);
            if(CollectionUtils.isNotEmpty(items))
            {
                for (DispatchItem item : items)
                {
                    ReceiveBase receiveBase = dao.getReceive(s.getDocumentId(), item.getDeptId());
                    if(receiveBase == null || receiveBase.getState().ordinal() > 3)
                    {
                        dao.delete(item);
                        continue;
                    }

                    ReceiptReply reply = dao.getReply(receipt.getReceiptId(), item.getDeptId());
                    if(reply != null && reply.getReplyTime() != null)
                    {
                        item.setUserName(receiveBase.getReceiverName());
                        item.setPhone(receiveBase.getReceiverPhone());
                        item.setReplayTime(reply.getReplyTime());
                    }

                    dao.update(item);
                }
            }

            boolean finish = true;
            boolean red = false;
            for (DispatchItem item : dao.getItems(record.getRecordId()))
            {
                if(item.getReplayTime() == null)
                {
                    finish = false;
                    if(record.getLimitDate() != null && record.getLimitDate().before(new Date()))
                    {
                        red = true;
                        break;
                    }
                }
                else
                {
                    if(record.getLimitDate() != null && record.getLimitDate().before(item.getReplayTime())) red = true;
                }
            }

            record.setRedWarnTag(red);
            record.setFinishTag(finish);
            dao.update(record);
        }
    }

    public void deleteBySendId(Long sendId) throws Exception
    {
        DispatchRecord record = dao.getRecordBySendId(sendId);
        if(record == null) return;

        List<DispatchItem> items = record.getItems();
        if(CollectionUtils.isEmpty(items))
        {
            dao.deleteEntities(items);
        }

        dao.delete(record);
    }

    public void checkRedWarm() throws Exception
    {
        List<DispatchRecord> records = dao.getOverTimeRecord();
        if(CollectionUtils.isEmpty(records)) return;
        for (DispatchRecord r : records)
        {
            r.setRedWarnTag(true);
            dao.update(r);
        }
    }

    /**
     * 预警提醒
     */
    public void warningNotify() throws Exception
    {
        overtimeNotify(0);
    }

    /**
     * 预警提醒
     */
    public void redNotify() throws Exception
    {
        overtimeNotify(1);
    }

    private void overtimeNotify(Integer type) throws Exception
    {
        if(type == null) return;

        List<DispatchRecord> records = null;
        if(type == 0) records = dao.getNotWarningNotify();
        else if(type == 1) records = dao.getNotRedNotify();

        if(CollectionUtils.isEmpty(records)) return;

        for (DispatchRecord r : records)
        {
            List<DispatchItem> items = r.getItems();
            if(CollectionUtils.isEmpty(items)) continue;

            for (DispatchItem item : items)
            {
                if(item.getReplayTime() == null)
                {
                    ReceiveBase receive = dao.getReceive(r.getSend().getDocumentId(), item.getDeptId());
                    if(receive == null) continue;

                    ReceiptReply reply = dao.getReply(r.getReceiptId(), item.getDeptId());
                    if(reply == null || reply.getReplyTime() == null)
                    {
                        //还没回复的才提醒
                        Message message = new Message();
                        message.setApp("odexchange");
                        message.setMessage(
                                Tools.getMessage(("ods.dispatch.receipt." + (type == 0 ? "warningMsg" : "redNsg")), r));
                        message.setSender(1);
                        message.setFromDeptId(r.getCreateDeptId());

                        if(receive.getState() == ReceiveState.noAccepted)
                        {
                            message.setUrls(new String[]{"/ods/receivelist?state=noAccepted&type=receive",
                                    "/ods/receivelist?state=noAccepted&state=accepted&type=receive",
                                    "/ods/receivelist?state=noAccepted",
                                    "/ods/receivelist?state=noAccepted&state=accepted",
                                    "/ods/receivelist?type=receive",
                                    "/ods/receivelist"});
                        }
                        else
                        {
                            message.setUrls(new String[]{"/ods/receivelist?state=accepted&type=receive",
                                    "/ods/receivelist?state=flowing&state=accepted&type=receive",
                                    "/ods/receivelist?state=accepted",
                                    "/ods/receivelist?state=flowing&state=accepted",
                                    "/ods/receivelist?type=receive",
                                    "/ods/receivelist"});
                        }

                        if(receive.getReceiver() != null)
                        {
                            message.setUserId(receive.getReceiver());
                            message.send();
                        }
                        else
                        {
                            message.sendTo("od_exchange_notify", receive.getDeptId());
                        }
                    }
                }
            }

            if(type == 0) r.setWarningNotified(true);
            else if(type == 1) r.setRedNotified(true);

            dao.update(r);
        }
    }
}
