package com.gzzm.ods.flow;

import com.gzzm.ods.exchange.ExchangeSendDao;
import net.cyan.thunwind.annotation.OQL;

import java.util.List;

/**
 * @author camel
 * @date 12-12-15
 */
public abstract class SendRecordDao extends ExchangeSendDao
{
    public SendRecordDao()
    {
    }

    public OdFlowInstance getOdFlowInstance(Long instanceId) throws Exception
    {
        return load(OdFlowInstance.class, instanceId);
    }

    public SendFlowInstance getSendFlowInstance(Long instanceId) throws Exception
    {
        return load(SendFlowInstance.class, instanceId);
    }

    @OQL("select instanceId from SendFlowInstance where saveRecorded=0 limit 8")
    public abstract List<Long> getNoRecordInstanceIds() throws Exception;
}
