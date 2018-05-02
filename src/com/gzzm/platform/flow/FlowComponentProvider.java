package com.gzzm.platform.flow;

/**
 * @author camel
 * @date 2015/3/26
 */
public interface FlowComponentProvider
{
    public FlowComponent getComponent(FlowComponentContext context) throws Exception;
}