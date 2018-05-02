package com.gzzm.ods.urge;

import com.gzzm.ods.exchange.*;
import com.gzzm.ods.flow.OdFlowDao;

/**
 * @author camel
 * @date 11-11-17
 */
public abstract class CollectUrgeDao extends OdFlowDao
{
    public CollectUrgeDao()
    {
    }

    public CollectUrge getUrge(Long receiveId) throws Exception
    {
        return load(CollectUrge.class, receiveId);
    }

    public Collect getCollect(Long receiveId) throws Exception
    {
        return load(Collect.class, receiveId);
    }

    public ReceiveBase getReceiveBase(Long receiveId) throws Exception
    {
        return load(ReceiveBase.class, receiveId);
    }
}
