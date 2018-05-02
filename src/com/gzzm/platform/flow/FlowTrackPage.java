package com.gzzm.platform.flow;

import com.gzzm.platform.commons.*;
import com.gzzm.platform.form.*;
import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.platform.workday.WorkDayService;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.StringUtils;
import net.cyan.commons.util.io.WebUtils;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.nest.annotation.Inject;
import net.cyan.valmiki.flow.*;
import net.cyan.valmiki.form.*;
import net.cyan.valmiki.form.components.*;

import java.util.*;

/**
 * 流程跟踪
 *
 * @author camel
 * @date 11-10-20
 */
@Service(url = "/flow/track")
public class FlowTrackPage
{
    public static class Opinion
    {
        private String text;

        private Date time;

        private String image;

        private String thumb1;

        private String thumb2;

        public Opinion(String text, Date time)
        {
            this.text = text;
            this.time = time;
        }

        public String getText()
        {
            return text;
        }

        public Date getTime()
        {
            return time;
        }

        public String getImage()
        {
            return image;
        }

        public void setImage(String image)
        {
            this.image = image;
        }

        public String getThumb1()
        {
            return thumb1;
        }

        public void setThumb1(String thumb1)
        {
            this.thumb1 = thumb1;
        }

        public String getThumb2()
        {
            return thumb2;
        }

        public void setThumb2(String thumb2)
        {
            this.thumb2 = thumb2;
        }
    }

    private SystemFlowDao dao;

    @Inject
    private WorkDayService workDayService;

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

    private SystemFormContext formContext;

    private List<Opinion> opinions;

    private String lastStepId;

    /**
     * 当前正在处理的groupId，处理的内容包括发消息等
     */
    private Long groupId;

    /**
     * 要发送的消息
     */
    @Require
    private String message;

    private boolean print;

    protected int year;

    public FlowTrackPage()
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

    public Long getGroupId()
    {
        return groupId;
    }

    public void setGroupId(Long groupId)
    {
        this.groupId = groupId;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public int getYear()
    {
        return year;
    }

    public void setYear(int year)
    {
        this.year = year;
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

    public boolean isPrint()
    {
        return print;
    }

    public void setPrint(boolean print)
    {
        this.print = print;
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

    public String getStateName(int state)
    {
        return FlowStep.getStateName(state);
    }

    public String getStateString(int state)
    {
        return FlowStep.getStateString(getStateName(state));
    }

    protected SystemFormContext getFormContext() throws Exception
    {
        if (formContext == null)
        {
            Long bodyId = FlowApi.getBodyId(getStruct().getInstance());

            if (bodyId != null)
            {
                BusinessContext businessContext = new BusinessContext();
                businessContext.setUser(userOnlineInfo);

                formContext = new SystemFormContext(null, bodyId);
                formContext.setBusinessContext(businessContext);

                FormApi.loadFormContext(formContext);
            }
        }

        return formContext;
    }

    /**
     * 获得某个环节填写的全部意见
     *
     * @param stepId 填写意见的步骤ID
     * @return 意见列表
     * @throws Exception 加载表单数据错误
     */
    public List<Opinion> getOpinions(String stepId) throws Exception
    {
        if (lastStepId != null && stepId.equals(lastStepId))
            return opinions;

        lastStepId = stepId;
        opinions = null;

        SystemFormContext formContext = getFormContext();

        if (formContext != null)
        {
            for (ComponentData data : formContext.getFormData())
            {
                if (data instanceof ParallelTextData)
                {
                    ParallelTextData parallelTextData = (ParallelTextData) data;

                    boolean b = true;
                    FormComponent component = formContext.getForm().getComponent(parallelTextData.getFullName());
                    if (component != null)
                    {
                        Object shareType = component.getProperty("shareType");
                        if (shareType != null && !"".equals(shareType))
                        {
                            b = false;
                        }
                    }

                    if (b)
                    {
                        for (ParallelTextItem item : parallelTextData.getItems())
                        {
                            if (stepId.equals(item.getOperationId()))
                            {
                                boolean exists = false;
                                if (opinions == null)
                                {
                                    opinions = new ArrayList<Opinion>();
                                }
                                else
                                {
                                    for (Opinion opinion : opinions)
                                    {
                                        if (opinion.getText().equals(item.getText()) &&
                                                opinion.getTime().equals(item.getTime()))
                                        {
                                            exists = true;
                                            break;
                                        }
                                    }
                                }

                                if (!exists)
                                {
                                    Opinion opinion = new Opinion(item.getText(), item.getTime());
                                    if (!StringUtils.isEmpty(item.getImage()))
                                    {
                                        String flowTag = getStruct().getInstance().getFlowTag();
                                        opinion.setImage("/ods/flow/" + flowTag + "/" + stepId + "/component/" +
                                                WebUtils.encode(data.getFullName()) + "/" + item.getId() + "/image");
                                        opinion.setThumb1("/ods/flow/" + flowTag + "/" + stepId + "/component/" +
                                                WebUtils.encode(data.getFullName()) + "/" + item.getId() + "/thumb1");
                                        opinion.setThumb2("/ods/flow/" + flowTag + "/" + stepId + "/component/" +
                                                WebUtils.encode(data.getFullName()) + "/" + item.getId() + "/thumb2");
                                    }
                                    opinions.add(opinion);
                                }
                            }
                        }
                    }
                }
            }
        }

        for (FlowStepBack back : getDao().getBacks(Long.valueOf(stepId)))
        {
            if (opinions == null)
                opinions = new ArrayList<Opinion>();
            opinions.add(new Opinion(back.getRemark(), back.getBackTime()));
        }

        return opinions;
    }

    public boolean isNotifyable(int state) throws Exception
    {
        return state == FlowStep.NOACCEPT || state == FlowStep.NODEAL || state == FlowStep.BACKNOACCEPT ||
                state == FlowStep.BACKNODEAL || state == FlowStep.NODEAL_REPLYED;
    }

    @Service(url = {"/{@class}/step/{stepId}", "/{@class}/instance/{instanceId}"})
    public String show()
    {
        if (print)
            return "/platform/flow/track_print.ptl";
        else
            return "/platform/flow/track.ptl";
    }

    @Service(url = {"/{@class}/step/group/{$0}/message"})
    public String message(Long groupId) throws Exception
    {
        this.groupId = groupId;

        InstanceStruct instanceStruct = getStruct();
        TrackInfo trackInfo = new TrackInfo(instanceStruct, groupId, userOnlineInfo);
        String nodeId = trackInfo.getStep().getNodeId();

        String message = null;
        if (!StringUtils.isEmpty(nodeId))
        {
            String s = "platform.flow." + nodeId + ".trackMessage";
            message = Tools.getMessage(s, trackInfo);

            if (message.equals(s))
                message = null;
        }

        if (message == null)
            message = Tools.getMessage("platform.flow.trackMessage", trackInfo);

        this.message = message;

        return "/platform/flow/trackmessage.ptl";
    }

    @Service(method = HttpMethod.post)
    @ObjectResult
    public void sendMessage() throws Exception
    {
        FlowApi.sendMessageToGroup(message, groupId, getPageUrl(getDao().getFlowInstance(instanceId).getFlowTag()),
                getDao());
    }

    public String getPageUrl(String tag) throws Exception
    {
        return null;
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
    public void deleteStep(String stepId) throws Exception
    {
        getController().deleteStep(stepId);
    }

    public Integer getWorkDay(StepStruct step) throws Exception
    {
        int state = step.getState();

        Date receiveTime = step.getReceiveTime();
        Date endTime = null;

        if (state == FlowStep.DEALED || state == FlowStep.BACKED || state == FlowStep.COPYDEALED ||
                state == FlowStep.PASSREPLYED || state == FlowStep.DEALED_REPLYED)
            endTime = step.getDisposeTime();

        if (endTime == null)
            endTime = new Date();


        return workDayService.diff(receiveTime, endTime, FlowApi.getDeptId(getStruct().getInstance()));
    }
}
