package com.gzzm.ods.flow;

import com.gzzm.ods.document.OfficeDocument;
import com.gzzm.platform.flow.*;

/**
 * @author camel
 * @date 2015/8/15
 */
public interface SendableFlowExtension extends FlowExtension
{
    public void sendDocument(FlowComponentContext context, OfficeDocument document) throws Exception;

    public void checkSendDocument(FlowComponentContext context, OfficeDocument document) throws Exception;
}