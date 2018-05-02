package com.gzzm.ods.flow;

import com.gzzm.platform.commons.BusinessContext;
import com.gzzm.platform.flow.SystemFlowInstance;
import com.gzzm.platform.organ.UserInfo;
import net.cyan.commons.transaction.Transactional;
import net.cyan.nest.annotation.Inject;
import net.cyan.valmiki.flow.*;

/**
 * @author camel
 * @date 2017/4/5
 */
public class OdFlowApiService
{
    @Inject
    private OdFlowDao odFlowDao;

    @Inject
    private OdSystemFlowDao systemFlowDao;

    public OdFlowApiService()
    {
    }

    @Transactional
    public void startFlow(Integer businessId, Long sourceInstanceId, Integer userId, Integer deptId) throws Exception
    {
        OdFlowInstance odFlowInstance = odFlowDao.getOdFlowInstance(sourceInstanceId);
        SystemFlowInstance systemFlowInstance = systemFlowDao.getFlowInstance(sourceInstanceId);

        if (userId == null)
        {
            userId = odFlowInstance.getCreator();

            if (deptId == null)
                deptId = odFlowInstance.getCreateDeptId();
        }

        BusinessContext businessContext = new BusinessContext();
        businessContext.setUser(new UserInfo(userId, deptId));
        businessContext.setBusinessDeptId(deptId);

        OdFlowStartContext context = OdFlowStartContext.create();
        context.setBusinessId(businessId);
        context.setSourceDocumentId(odFlowInstance.getDocumentId());
        context.setCloneBodyId(systemFlowInstance.getBodyId());
        context.setBusinessContext(businessContext);
        context.start();

        context.getOdFlowInstance().setState(OdFlowInstanceState.unclosed);
        odFlowDao.update(context.getOdFlowInstance());

        FlowContext flowContext = context.getFlowContext();
        flowContext.getInstance().setState(0);
        systemFlowDao.updateInstance(flowContext.getInstance());

        flowContext.getStep().setState(FlowStep.NOACCEPT);
        systemFlowDao.updateStep(flowContext.getStep());
    }
}
