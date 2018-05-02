package com.gzzm.platform.flow;

/**
 * @author camel
 * @date 2014/8/26
 */
public interface FlowExtensionFactory
{
    public FlowExtension getExtension(FlowComponentContext context) throws Exception;
}