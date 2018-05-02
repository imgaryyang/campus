package com.gzzm.platform.flow;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.StringUtils;
import net.cyan.nest.annotation.Inject;
import net.cyan.valmiki.flow.*;

/**
 * 流程管理的page类，提供流程环节管理的功能
 *
 * @author camel
 * @date 12-11-1
 */
@Service
public abstract class FlowControlPage
{
    private SystemFlowDao dao;

    @Inject
    private UserOnlineInfo userOnlineInfo;

    /**
     * 跟踪的步骤ID
     */
    private Long stepId;

    private Long instanceId;

    private FlowStep step;

    private FlowController controller;

    @NotSerialized
    private InstanceStruct struct;

    public FlowControlPage()
    {
    }

    protected SystemFlowDao getDao() throws Exception
    {
        if (dao == null)
            dao = createDao();

        return dao;
    }

    protected SystemFlowDao createDao() throws Exception
    {
        return DefaultSystemFlowDao.getInstance();
    }

    public Long getStepId()
    {
        return stepId;
    }

    public void setStepId(Long stepId)
    {
        this.stepId = stepId;
    }

    public Long getInstanceId() throws Exception
    {
        if (instanceId == null && stepId != null)
            instanceId = Long.valueOf(getStep().getInstanceId());

        return instanceId;
    }

    public void setInstanceId(Long instanceId)
    {
        this.instanceId = instanceId;
    }

    protected FlowStep getStep() throws Exception
    {
        if (step == null)
            step = getDao().loadStep(stepId.toString());

        return step;
    }

    protected FlowController getController() throws Exception
    {
        if (controller == null)
            controller = FlowApi.getController(getInstanceId(), getDao());

        return controller;
    }

    @NotSerialized
    public InstanceStruct getStruct() throws Exception
    {
        if (struct == null)
            struct = getController().getInstanceStruct();

        return struct;
    }

    public String getFlowTag() throws Exception
    {
        return getStruct().getInstance().getFlowTag();
    }

    public String getStateName(int state)
    {
        return FlowStep.getStateName(state);
    }

    public String getStateString(int state)
    {
        return FlowStep.getStateString(getStateName(state));
    }

    @Service(url = {"/{@class}/step/{stepId}", "/{@class}/instance/{instanceId}"})
    public String show()
    {
        return "/platform/flow/control.ptl";
    }

    public boolean isCancelable(String stepId, int state) throws Exception
    {
        return (state == FlowStep.DEALED ||
                state == FlowStep.DEALED_REPLYED | state == FlowStep.DEALED_REPLYED_NOACCEPT) &&
                getController().isCancelSendable(stepId);
    }

    @Service
    @ObjectResult
    @Transactional
    public void cancelSend(String stepId) throws Exception
    {
        getController().cancelSend(stepId);

        Class<? extends FlowPage> c = getFlowPageClass(controller.getInstance().getFlowTag());
        if (c != null && c != FlowPage.class)
        {
            FlowPage flowPage = Tools.getBean(c);
            flowPage.setStepId(Long.valueOf(stepId));

            flowPage.afterCancelSend(null);
        }
    }

    @Service
    @ObjectResult
    @Transactional
    public void back(String stepId) throws Exception
    {
        getController().back(stepId);

        Class<? extends FlowPage> c = getFlowPageClass(controller.getInstance().getFlowTag());
        if (c != null && c != FlowPage.class)
        {
            FlowPage flowPage = Tools.getBean(c);
            flowPage.setStepId(Long.valueOf(stepId));

            flowPage.afterBack(null, null, null);
        }
    }

    @Service
    @ObjectResult
    @Transactional
    public void deleteStep(String stepId) throws Exception
    {
        getController().deleteStep(stepId);
    }

    protected Class<? extends FlowPage> getFlowPageClass(String flowTag) throws Exception
    {
        return FlowPage.class;
    }

    @Service
    @ObjectResult
    @Transactional
    public void stopStep(String stepId) throws Exception
    {
        FlowController controller = getController();
        if (controller.stopStep(stepId))
        {
            Class<? extends FlowPage> c = getFlowPageClass(controller.getInstance().getFlowTag());

            if (c != null && c != FlowPage.class)
            {
                FlowPage flowPage = Tools.getBean(c);
                flowPage.setStepId(Long.valueOf(stepId));

                flowPage.afterStop(null);
            }
        }
    }

    @Service
    @ObjectResult
    @Transactional
    public void deleteStepGroup(String groupId) throws Exception
    {
        getController().deleteStepGroup(groupId);
    }

    @Service
    @ObjectResult
    @Transactional
    public void changeReceiver(String stepId, Integer userId) throws Exception
    {
        FlowStep step = getDao().loadStep(stepId);

        User user = getDao().getUser(userId);
        Integer deptId = step.getProperty("deptId");
        boolean b = false;
        if (deptId != null)
        {
            for (Dept dept : user.getDepts())
            {
                if (dept.getDeptId().equals(deptId))
                {
                    b = true;
                    break;
                }
            }
        }

        if (!b)
            step.setProperty("deptId", user.getDepts().get(0).getDeptId());

        step.setStepId(stepId);
        step.setReceiver(userId.toString());
        step.setReceiverName(user.getUserName());
        step.setDealerName(user.getUserName());

        getController().getDao().updateStep(step);
    }

    @NotSerialized
    public String getCopyNodeName() throws Exception
    {
        FlowNode node = controller.getFlow().getNode(FlowNode.COPY);

        if (node == null || StringUtils.isEmpty(node.getNodeName()))
            return Tools.getMessage("cyan.valmiki.flow.action.copy.name");
        else
            return node.getNodeName();
    }

    @NotSerialized
    public String getPassNodeName() throws Exception
    {
        FlowNode node = controller.getFlow().getNode(FlowNode.PASS);

        if (node == null || StringUtils.isEmpty(node.getNodeName()))
            return Tools.getMessage("cyan.valmiki.flow.action.pass.name");
        else
            return node.getNodeName();
    }

    @Service
    @ObjectResult
    public void refreshSteps() throws Exception
    {
        getController().refreshSteps();
    }

    @ObjectResult
    @Service
    public void restore() throws Exception
    {
        getController().restoreInstance();
    }

    @NotSerialized
    public boolean isRestoreable() throws Exception
    {
        for (StepStruct step : getStruct().getSteps())
        {
            if (step.getState() == FlowStep.STOPED)
                return true;
        }

        return false;
    }
}
