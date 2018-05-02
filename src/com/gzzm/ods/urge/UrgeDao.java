package com.gzzm.ods.urge;

import com.gzzm.ods.flow.OdFlowInstance;
import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.List;

/**
 * @author camel
 * @date 11-11-17
 */
public abstract class UrgeDao extends GeneralDao
{
    public UrgeDao()
    {
    }

    public OdFlowUrge getUrge(Long instanceId) throws Exception
    {
        return load(OdFlowUrge.class, instanceId);
    }

    public OdFlowInstance getInstance(Long instanceId) throws Exception
    {
        return load(OdFlowInstance.class, instanceId);
    }

    @OQL("select urge from OdFlowInstance where state=0 and urged=1")
    public abstract List<OdFlowUrge> getShouldRemindFlowUrges() throws Exception;

    @OQL("select urge from Collect where urged=1 and receiveBase.state<3")
    public abstract List<CollectUrge> getShouldRemindCollectUrges() throws Exception;
}
