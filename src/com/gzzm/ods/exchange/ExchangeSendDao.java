package com.gzzm.ods.exchange;

import net.cyan.thunwind.annotation.*;

import java.util.*;

/**
 * @author camel
 * @date 11-10-11
 */
public abstract class ExchangeSendDao extends ExchangeDao
{
    public ExchangeSendDao()
    {
    }

    /**
     * 查询某份文的联合发文记录
     *
     * @param documentId 公文ID
     * @param time       查询此时间后的联合发文，对于ReceiveType.union，取空值，对于ReceiveType.unionseal，取成文时间
     * @return 联合发文记录列表
     * @throws Exception 数据库查询错误
     */
    @OQL("select u from `Union` u where u.receiveBase.documentId=:1 and u.receiveBase.sendTime>?2")
    public abstract List<Union> getUnions(Long documentId, Date time) throws Exception;

    /**
     * 查询某份文的联合发文记录数
     *
     * @param documentId 公文ID
     * @param time       查询此时间后的联合发文，对于ReceiveType.union，取空值，对于ReceiveType.unionseal，取成文时间
     * @return 联合发文记录列表
     * @throws Exception 数据库查询错误
     */
    @OQL("select count(*) from `Union` u where u.receiveBase.documentId=:1 and u.receiveBase.sendTime>?2")
    public abstract Integer getUnionCount(Long documentId, Date time) throws Exception;

    /**
     * 查询某部门当前正在处理的某份公文联合发文记录，用于避免同时发送多分联合发文给某个部门
     *
     * @param documentId 公文ID
     * @param deptId     部门ID
     * @return 返回要查询的联合发文记录，如果没有返回空
     * @throws Exception 数据库查询错误
     */
    @OQL("select u from `Union` u where u.receiveBase.documentId=:1 and u.receiveBase.deptId=:2 and u.receiveBase.state<3")
    public abstract Union getCurrentUnion(Long documentId, Integer deptId) throws Exception;

    /**
     * 查询某份公文当前正在处理联合发文
     * <p/>
     * state<3(ReceiveState.end)时为正在处理
     *
     * @param documentId 公文ID
     * @return 当前正在处理联合发文
     * @throws Exception 数据库查询错误
     * @see ReceiveState#end
     */
    @OQL("select u from `Union` u where u.receiveBase.documentId=:1 and u.receiveBase.state<3")
    public abstract List<Union> getCurrentUnions(Long documentId) throws Exception;

    /**
     * 查询某份公文当前正在处理联合发文的部门
     * <p/>
     * state<3(ReceiveState.end)时为正在处理
     *
     * @param documentId 公文ID
     * @return 当前正在处理联合发文的部门ID
     * @throws Exception 数据库查询错误
     * @see ReceiveState#end
     */
    @OQL("select u.receiveBase.deptId from `Union` u where u.receiveBase.documentId=:1 and u.receiveBase.state<3")
    public abstract List<Integer> getCurrentUnionDeptIds(Long documentId) throws Exception;

    @GetByField("documentId")
    public abstract SendRecord getSendRecordByDocumentId(Long documentId) throws Exception;
}
