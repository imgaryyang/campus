package com.gzzm.platform.flow;

import net.cyan.arachne.annotation.Service;

/**
 * @author camel
 * @date 2014/9/2
 */
@Service(url = "/flow/instance/query")
public class DefaultFlowInstanceQuery extends FlowInstanceQuery<SystemFlowInstance>
{
    public DefaultFlowInstanceQuery()
    {
    }
}
