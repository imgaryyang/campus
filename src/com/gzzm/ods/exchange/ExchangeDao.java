package com.gzzm.ods.exchange;

import com.gzzm.ods.document.OdDao;
import com.gzzm.platform.organ.*;
import net.cyan.thunwind.annotation.*;

import java.util.List;

/**
 * @author camel
 * @date 11-9-23
 */
public abstract class ExchangeDao extends OdDao
{
    public ExchangeDao()
    {
    }

    public Dept getDept(Integer deptId) throws Exception
    {
        return load(Dept.class, deptId);
    }

    public User getUser(Integer userId) throws Exception
    {
        return load(User.class, userId);
    }

    public Send getSend(Long sendId) throws Exception
    {
        return load(Send.class, sendId);
    }

    public ReceiveBase getReceiveBase(Long receiveId) throws Exception
    {
        return load(ReceiveBase.class, receiveId);
    }

    public Receive getReceive(Long receiveId) throws Exception
    {
        return load(Receive.class, receiveId);
    }

    public Union getUnion(Long receiveId) throws Exception
    {
        return load(Union.class, receiveId);
    }

    public Collect getCollect(Long receiveId) throws Exception
    {
        return load(Collect.class, receiveId);
    }

    public UnionDeal getUnionDeal(Long receiveId) throws Exception
    {
        return load(UnionDeal.class, receiveId);
    }

    public Copy getCopy(Long receiveId) throws Exception
    {
        return load(Copy.class, receiveId);
    }

    public Back getBack(Long backId) throws Exception
    {
        return load(Back.class, backId);
    }

    public void lockReceiveBase(Long receiveId) throws Exception
    {
        lock(ReceiveBase.class, receiveId);
    }

    @OQL("select s from Send s where documentId=:")
    public abstract Send getSendByDocumentId(Long documentId) throws Exception;

    @GetByField("documentId")
    public abstract List<ReceiveBase> getReceivesByDocumentId(Long documentId);

    @GetByField({"documentId", "type", "deptId"})
    public abstract List<ReceiveBase> getReceivesByDocumentIdAndDeptId(Long documentId, ReceiveType type,
                                                                       Integer deptId) throws Exception;

    public List<ReceiveBase> getReceivesByDocumentIdAndDeptId(Long documentId, Integer deptId)
            throws Exception
    {
        return getReceivesByDocumentIdAndDeptId(documentId, ReceiveType.receive, deptId);
    }

    public ReceiveBase getReceiveByDocumentIdAndDeptId(Long documentId, ReceiveType type, Integer deptId)
            throws Exception
    {
        List<ReceiveBase> receiveBases = getReceivesByDocumentIdAndDeptId(documentId, type, deptId);

        if (receiveBases.size() == 0)
            return null;

        for (ReceiveBase receiveBase : receiveBases)
        {
            if (receiveBase.getState() != ReceiveState.backed &&
                    receiveBase.getState() != ReceiveState.withdrawn && receiveBase.getState() != ReceiveState.canceled)
            {
                return receiveBase;
            }
        }

        return receiveBases.get(0);
    }


    public ReceiveBase getReceiveByDocumentIdAndDeptId(Long documentId, Integer deptId) throws Exception
    {
        return getReceiveByDocumentIdAndDeptId(documentId, ReceiveType.receive, deptId);
    }
}
