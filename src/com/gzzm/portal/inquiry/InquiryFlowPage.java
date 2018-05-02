package com.gzzm.portal.inquiry;

import com.gzzm.ods.flow.*;
import com.gzzm.platform.commons.BusinessContext;
import com.gzzm.platform.flow.FlowApi;
import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.transaction.Transactional;
import net.cyan.nest.annotation.Inject;

/**
 * 和咨询投诉内部处理流程相关的入口
 *
 * @author camel
 * @date 12-11-21
 */
@Service
public class InquiryFlowPage
{
    @Inject
    private UserOnlineInfo userOnlineInfo;

    @Inject
    private OdFlowDao odFlowDao;

    private Long processId;

    private Integer businessId;

    public InquiryFlowPage()
    {
    }

    public Long getProcessId()
    {
        return processId;
    }

    public void setProcessId(Long processId)
    {
        this.processId = processId;
    }

    public Integer getBusinessId()
    {
        return businessId;
    }

    public void setBusinessId(Integer businessId)
    {
        this.businessId = businessId;
    }

    @Redirect
    @Service(url = "/portal/inquiry/flow/start")
    public String start() throws Exception
    {
        InquiryFlowStartContext startContext = InquiryFlowStartContext.create();

        startContext.setProcessId(processId);

        BusinessContext businessContext = new BusinessContext();
        businessContext.setUser(userOnlineInfo);
        businessContext.put("ip", userOnlineInfo.getIp());

        startContext.setBusinessContext(businessContext);
        startContext.setBusinessId(businessId);

        startContext.start();

        return OdFlowService.getStepUrl(startContext.getStepId(), startContext.getFlowTag());
    }

    @Redirect
    @Service(url = "/portal/inquiry/flow/track")
    public String track() throws Exception
    {
        Long instanceId = null;
        for (OdFlowInstance instance : odFlowDao
                .getOdFlowInstancesByLinkId(processId.toString(), InquiryFlowComponent.class.getName()))
        {
            instanceId = instance.getInstanceId();
            if (instance.getState() == OdFlowInstanceState.unclosed)
                break;

        }

        if (instanceId == null)
            return null;

        return "/od/flow/track/instance/" + instanceId;
    }

    @ObjectResult
    @Transactional
    @Service(url = "/portal/inquiry/flow/cancel")
    public void cancel() throws Exception
    {
        for (OdFlowInstance instance : odFlowDao
                .getOdFlowInstancesByLinkId(processId.toString(), InquiryFlowComponent.class.getName()))
        {
            if (instance.getState() == OdFlowInstanceState.unclosed)
            {
                FlowApi.getController(instance.getInstanceId(), OdSystemFlowDao.getInstance()).stopInstance();
            }
        }

        InquiryProcess process = new InquiryProcess();
        process.setProcessId(processId);
        process.setTurnInnered(false);
        odFlowDao.save(process);
    }
}
