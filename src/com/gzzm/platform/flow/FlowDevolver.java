package com.gzzm.platform.flow;

import com.gzzm.platform.devolve.Devolver;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 流程工作移交
 *
 * @author camel
 * @date 13-1-7
 */
public class FlowDevolver implements Devolver
{
    @Inject
    private static Provider<FlowDevolveDao> daoProvider;

    private List<String> flowTags;

    private List<Integer> states;

    public FlowDevolver()
    {
    }

    public List<String> getFlowTags()
    {
        return flowTags;
    }

    public void setFlowTags(List<String> flowTags)
    {
        this.flowTags = flowTags;
    }

    public List<Integer> getStates()
    {
        return states;
    }

    public void setStates(List<Integer> states)
    {
        this.states = states;
    }

    public void devolve(Integer fromUserId, Integer toUserId, Date startTime, Date endTime) throws Exception
    {
        daoProvider.get().devolve(fromUserId.toString(), toUserId.toString(), flowTags, states, startTime, endTime);
    }
}
