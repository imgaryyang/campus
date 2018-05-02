package com.gzzm.ods.flow;

import com.gzzm.ods.document.OfficeDocument;
import com.gzzm.platform.flow.*;

/**
 * 内部事务流程上下文
 *
 * @author camel
 * @date 11-12-6
 */
public interface OdFlowContext extends FlowComponentContext
{
    public OdFlowService getService() throws Exception;

    public OdFlowInstance getOdFlowInstance() throws Exception;

    public OfficeDocument getDocument() throws Exception;

    public OfficeDocument getDocument(String type) throws Exception;

    @Override
    public OdFlowPage getFlowPage() throws Exception;
}