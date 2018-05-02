package com.gzzm.ods.exchange;

import net.cyan.thunwind.annotation.*;

import java.util.List;

/**
 * 和收文相关的数据库操作
 *
 * @author camel
 * @date 11-10-11
 */
public abstract class ExchangeReceiveDao extends ExchangeDao
{
    public ExchangeReceiveDao()
    {
    }

    @OQL("select r from ReceiveBase r where r.receiveId in :1")
    public abstract List<ReceiveBase> getReceiveBases(List<Long> receiveIds) throws Exception;

    /**
     * 获得某分公文收文已接收的数量
     *
     * @param documentId 公文ID
     * @return 此公文的收文被接收的数量
     * @throws Exception 数据库查询错误
     */
    @OQL("select count(*) from ReceiveBase r where r.documentId=:1 and r.type in (0,5) and r.state>0 and r.state<4")
    public abstract Integer getAcceptedReceiveCount(Long documentId) throws Exception;

    /**
     * 获得某分公文收文未接收的数量
     *
     * @param documentId 公文ID
     * @return 此公文的收文未被接收的数量
     * @throws Exception 数据库查询错误
     */
    @OQL("select count(*) from ReceiveBase r where r.documentId=:1 and r.type in (0,5) and r.state=0")
    public abstract Integer getNoAcceptedReceiveCount(Long documentId) throws Exception;

    /**
     * 获得某分公文收文未接收的收文id列表
     *
     * @param documentId 公文ID
     * @return 此公文的收文未被接收的收文id列表
     * @throws Exception 数据库查询错误
     */
    @OQL("select receiveId from ReceiveBase r where r.documentId=:1 and r.type in (0,5) and r.state=0")
    public abstract List<Long> getNoAcceptedReceiveIds(Long documentId) throws Exception;

    /**
     * 获得某分公文收文未接收的收文id列表
     *
     * @param documentId 公文ID
     * @return 此公文的收文未被接收列表
     * @throws Exception 数据库查询错误
     */
    @OQL("select r from ReceiveBase r where r.documentId=:1 and r.type in (0,5) and r.state=0")
    public abstract List<ReceiveBase> getNoAcceptedReceives(Long documentId) throws Exception;

    /**
     * 获得某分公文收文的收文id列表(撤回的和退回的除外)
     *
     * @param documentId 公文ID
     * @return 此公文的收文的收文id列表
     * @throws Exception 数据库查询错误
     */
    @OQL("select receiveId from ReceiveBase r where r.documentId=:1 and r.type in (0,5) and r.state<4")
    public abstract List<Long> getReceiveIds(Long documentId) throws Exception;

    /**
     * 获得某分公文收文的收文id列表(撤回的和退回的除外)
     *
     * @param documentId 公文ID
     * @return 此公文的收文列表
     * @throws Exception 数据库查询错误
     */
    @OQL("select r from ReceiveBase r where r.documentId=:1 and r.type in (0,5) and r.state<4")
    public abstract List<ReceiveBase> getReceives(Long documentId) throws Exception;

    @OQL("select a from ReceiveAttribute a")
    public abstract List<ReceiveAttribute> getReceiveAttributes() throws Exception;

    @OQL("select r from Receive r where receiveId in :1")
    public abstract List<Receive> getReceives(List<Long> receiveId);

    @OQL("select r from ReceiveBase r where document is not null and datediff(sysdate(),sendTime)<=1 and notified=0 limit 30")
    public abstract List<ReceiveBase> getNoNotifiedReceives() throws Exception;

    @OQL("select b from Back b where datediff(sysdate(),backTime)<=1 and notified=0 limit 30")
    public abstract List<Back> getNoNotifiedBacks() throws Exception;

    @OQL("select r from Receive r where r.receiveBase.deptId=:1 and r.serial=:2 and r.receiveBase.state in (1,2,3) order by receiveBase.acceptTime limit 1")
    public abstract Receive getReceiveBySerial(Integer deptId, String serial) throws Exception;

    @GetByField("receiveId")
    public abstract Back getBackByReceiveId(Long receiveId) throws Exception;
}
