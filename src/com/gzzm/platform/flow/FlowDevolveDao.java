package com.gzzm.platform.flow;

import net.cyan.thunwind.dao.GeneralDao;

import java.util.*;

/**
 * @author camel
 * @date 13-1-7
 */
public abstract class FlowDevolveDao extends GeneralDao
{
    public FlowDevolveDao()
    {
    }

    public void devolve(String fromUserId, String toUserId, Collection<String> flowTags, Collection<Integer> states,
                        Date startTime, Date endTime) throws Exception
    {
        StringBuilder buffer = new StringBuilder("update SystemFlowStep s set receiver=:2 where receiver=:1");

        if (flowTags != null)
        {
            buffer.append(" and first(select flowTag from SystemFlowInstance i where i.instanceId=s.instanceId) in :3");
        }

        if (states != null)
        {
            buffer.append(" and s.state in :4");
        }

        if (startTime != null)
        {
            buffer.append(" and receiveTime>=:5");
        }

        if (endTime != null)
        {
            buffer.append(" and receiveTime<:6");
        }

        executeOql(buffer.toString(), fromUserId, toUserId, flowTags, states, startTime, endTime);
    }
}
