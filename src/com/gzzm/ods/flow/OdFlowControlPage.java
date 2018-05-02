package com.gzzm.ods.flow;

import com.gzzm.platform.flow.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.transaction.Transactional;

/**
 * @author camel
 * @date 2017/7/25
 */
@Service(url = "/ods/flow/control")
public class OdFlowControlPage extends FlowControlPage
{
    public OdFlowControlPage()
    {
    }

    @Override
    protected Class<? extends FlowPage> getFlowPageClass(String flowTag) throws Exception
    {
        return OdFlowService.getFlowPageClass(flowTag);
    }

    @Override
    @Transactional
    public void deleteStep(String stepId) throws Exception
    {
        super.deleteStep(stepId);

        ((OdSystemFlowDao) getDao()).refreshStepQ(getInstanceId());
    }

    @Override
    @Transactional
    public void deleteStepGroup(String groupId) throws Exception
    {
        super.deleteStepGroup(groupId);

        ((OdSystemFlowDao) getDao()).refreshStepQ(getInstanceId());
    }

    @Override
    @Transactional
    public void changeReceiver(String stepId, Integer userId) throws Exception
    {
        super.changeReceiver(stepId, userId);

        ((OdSystemFlowDao) getDao()).refreshStepQ(getInstanceId());
    }

    @Override
    protected SystemFlowDao createDao() throws Exception
    {
        return OdSystemFlowDao.getInstance();
    }
}
