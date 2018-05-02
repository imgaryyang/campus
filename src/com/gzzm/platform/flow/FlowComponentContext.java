package com.gzzm.platform.flow;

import com.gzzm.platform.commons.BusinessContext;
import com.gzzm.platform.form.SystemFormContext;
import com.gzzm.platform.organ.UserInfo;
import net.cyan.valmiki.flow.FlowContext;

/**
 * @author camel
 * @date 12-8-10
 */
public interface FlowComponentContext
{
    public FlowContext getFlowContext() throws Exception;

    public SystemFormContext getFormContext() throws Exception;

    public SystemFormContext getFormContext(String name) throws Exception;

    public BusinessContext getBusinessContext() throws Exception;

    public UserInfo getUserInfo() throws Exception;

    public Integer getUserId() throws Exception;

    public Integer getBusinessDeptId() throws Exception;

    public Long getInstanceId() throws Exception;

    public Long getStepId() throws Exception;

    public Object getParameter(String name) throws Exception;

    public Object[] getParameterValues(String name) throws Exception;

    public FlowPage getFlowPage() throws Exception;

    public String getTitle() throws Exception;

    public void setTitle(String title) throws Exception;
}
