package com.gzzm.ods.flow;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.flow.*;

/**
 * @author camel
 * @date 2014/8/27
 */
public class SendFlowExtensionFactory implements FlowExtensionFactory
{
    public SendFlowExtensionFactory()
    {
    }

    @Override
    public FlowExtension getExtension(FlowComponentContext context) throws Exception
    {
        if (!OdFlowPage.class.isInstance(context.getFlowPage()))
            return Tools.getBean(SendFlowExtension.class);

        return null;
    }
}
