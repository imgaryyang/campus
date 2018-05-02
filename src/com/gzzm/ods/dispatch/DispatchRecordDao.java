package com.gzzm.ods.dispatch;

import com.gzzm.ods.exchange.*;
import com.gzzm.ods.receipt.*;
import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.List;

/**
 * @author LDP
 * @date 2017/7/24
 */
public abstract class DispatchRecordDao extends GeneralDao
{
    public DispatchRecordDao()
    {
    }

    @GetByField("sendId")
    public abstract DispatchRecord getRecordBySendId(Long sendId) throws Exception;

    @OQL("select s from Send s where s.state = 0 and s.documentId in(select r.documentId from Receipt r where r.type='attachment') and s.sendId not in(select r.sendId from DispatchRecord r where r.sendId is not null and r.finishTag = 1)")
    public abstract List<Send> getNotFinishRecord() throws Exception;

    @OQL("select s from Send s where s.state = 1 and s.sendId in(select r.sendId from DispatchRecord r where r.sendId is not null)")
    public abstract List<Send> getUselessRecord() throws Exception;

    @OQL("select r from Receipt r where r.type='attachment' and r.documentId = ?1")
    public abstract Receipt getReceiptByDocumentId(Long documentId) throws Exception;

    @OQL("select r from ReceiveBase r where r.documentId = ?1 and r.type = 0")
    public abstract List<ReceiveBase> getReceiveByDocumentId(Long documentId) throws Exception;

    @OQL("select r from ReceiveBase r where r.documentId = ?1 and r.deptId = ?2 and r.type = 0")
    public abstract ReceiveBase getReceive(Long documentId, Integer deptId) throws Exception;

    @GetByField({"recordId", "deptId"})
    public abstract DispatchItem getItem(Long recordId, Integer deptId) throws Exception;

    @OQL("select i from DispatchItem i where i.recordId = ?1")
    public abstract List<DispatchItem> getItems(Long recordId) throws Exception;

    @OQL("select i from DispatchItem i where i.recordId = ?1 and i.deptId not in ?2")
    public abstract List<DispatchItem> getItems(Long recordId, List<Integer> exceptDeptIds) throws Exception;

    @GetByField({"receiptId", "deptId"})
    public abstract ReceiptReply getReply(Long receiptId, Integer deptId) throws Exception;

    @OQL("select d from DispatchRecord d where d.sendId is null and (d.finishTag is null or d.finishTag = 0) and sysdate() > d.limitDate")
    public abstract List<DispatchRecord> getOverTimeRecord() throws Exception;

    @OQL("select d from DispatchRecord d where d.sendId is not null and (d.warningNotified is null or d.warningNotified = 0) and sysdate() > d.warningDate")
    public abstract List<DispatchRecord> getNotWarningNotify() throws Exception;

    @OQL("select d from DispatchRecord d where d.sendId is not null and d.redWarnTag = 1 and (d.redNotified is null or d.redNotified = 0)")
    public abstract List<DispatchRecord> getNotRedNotify() throws Exception;

    @OQL("select count(1) from DispatchRecord d where d.sendNumber=?1 and d.recordId not in ?2")
    public abstract Integer checkSendNumber(String number, Long recordId) ;
}
