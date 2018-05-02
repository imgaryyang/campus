package com.gzzm.ods.flow;

import com.gzzm.platform.flow.AbstractFlowComponent;

/**
 * @author camel
 * @date 11-12-6
 */
public abstract class AbstractOdFlowComponent extends AbstractFlowComponent<OdFlowContext> implements OdFlowComponent
{
    public AbstractOdFlowComponent()
    {
    }

    public void deleteFlow(OdFlowInstance instance) throws Exception
    {
    }

    @Override
    public void stopFlow(OdFlowInstance instance) throws Exception
    {
    }
}
