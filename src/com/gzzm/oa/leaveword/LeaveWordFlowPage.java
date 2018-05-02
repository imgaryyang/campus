package com.gzzm.oa.leaveword;

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
public class LeaveWordFlowPage
{
    @Inject
    private UserOnlineInfo userOnlineInfo;

    @Inject
    private OdFlowDao odFlowDao;

    private Integer leaveWordId;

    private Integer businessId;

    public LeaveWordFlowPage()
    {
    }

    public Integer getLeaveWordId()
    {
        return leaveWordId;
    }

    public void setLeaveWordId(Integer leaveWordId)
    {
        this.leaveWordId = leaveWordId;
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
    @Service(url = "/oa/leaveword/flow/start")
    public String start() throws Exception
    {
        LeaveWordFlowStartContext startContext = LeaveWordFlowStartContext.create();

        startContext.setLeaveWordId(leaveWordId);

        BusinessContext businessContext = new BusinessContext();
        businessContext.setUser(userOnlineInfo);
        businessContext.put("ip", userOnlineInfo.getIp());

        startContext.setBusinessContext(businessContext);
        startContext.setBusinessId(businessId);

        startContext.start();

        return OdFlowService.getStepUrl(startContext.getStepId(), startContext.getFlowTag());
    }

    @Redirect
    @Service(url = "/oa/leaveword/flow/track")
    public String track() throws Exception
    {
        Long instanceId = null;
        for (OdFlowInstance instance : odFlowDao
                .getOdFlowInstancesByLinkId(leaveWordId.toString(), LeaveWordFlowComponent.class.getName()))
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
    @Service(url = "/oa/leaveword/flow/cancel")
    public void cancel() throws Exception
    {
        for (OdFlowInstance instance : odFlowDao
                .getOdFlowInstancesByLinkId(leaveWordId.toString(), LeaveWordFlowComponent.class.getName()))
        {
            if (instance.getState() == OdFlowInstanceState.unclosed)
            {
                FlowApi.getController(instance.getInstanceId(), OdSystemFlowDao.getInstance()).stopInstance();
            }
        }

        LeaveWord leaveWord = new LeaveWord();
        leaveWord.setLeaveWordId(leaveWordId);
        leaveWord.setState(LeaveWordState.noAccepted);
        odFlowDao.update(leaveWord);
    }
}
