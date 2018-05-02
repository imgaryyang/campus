package com.gzzm.ods.timeout;

import com.gzzm.ods.exchange.ReceiveBase;
import com.gzzm.ods.flow.OdFlowInstance;
import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.List;

/**
 * @author camel
 * @date 2014/5/28
 */
public abstract class OdTimeoutDao extends GeneralDao
{
    public OdTimeoutDao()
    {
    }

    public OdFlowInstance getOdFlowInstance(Long instanceId) throws Exception
    {
        return load(OdFlowInstance.class, instanceId);
    }

    public ReceiveBase getReceiveBase(Long receiveId) throws Exception
    {
        return load(ReceiveBase.class, receiveId);
    }

    @OQL("select i from OdFlowInstance i where state=0 and (deadline is not null or timeoutChecked=1)")
    public abstract List<OdFlowInstance> getShouldCheckTimeoutInstances() throws Exception;

    @OQL("select r from ReceiveBase r where state<3 and deadline is not null")
    public abstract List<ReceiveBase> getShouldCheckTimeoutReceives() throws Exception;
}
