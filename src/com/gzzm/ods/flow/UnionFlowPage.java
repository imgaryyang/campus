package com.gzzm.ods.flow;

import com.gzzm.ods.document.*;
import com.gzzm.ods.exchange.*;
import com.gzzm.ods.print.*;
import com.gzzm.platform.commons.*;
import com.gzzm.platform.flow.*;
import com.gzzm.platform.form.SystemFormContext;
import com.gzzm.platform.weboffice.OfficeEditType;
import net.cyan.arachne.annotation.*;
import net.cyan.nest.annotation.*;
import net.cyan.valmiki.flow.*;
import net.cyan.valmiki.form.*;

import java.util.*;

/**
 * 联合办文流程处理
 *
 * @author camel
 * @date 11-9-30
 */
@Service(url = "/ods/flow/union")
public class UnionFlowPage extends OdFlowPage
{
    @Inject
    protected ExchangeSendService exchangeSendService;

    /**
     * 发文流程实例
     */
    @NotSerialized
    private SendFlowInstance sendFlowInstance;

    @NotSerialized
    private OdFlowInstance sendOdFlowInstance;

    private Long receiveBodyId;

    /**
     * 最后一个联合发文单位是否直接发文，在/WEB-INF/config/ods/config.properties中配置，
     * 然后在union.js的onload中写入，当点击按钮时便会自动传过来
     *
     * @see /WEB-INF/config/ods/config.properties
     */
    @Constant("autoSendWithLastUnion")
    private Boolean autoSendWithLastUnion;

    public UnionFlowPage()
    {
    }

    public SendFlowInstance getSendFlowInstance() throws Exception
    {
        if (sendFlowInstance == null)
        {
            OdFlowInstance odFlowInstance = getOdFlowInstance();

            sendFlowInstance = getDao().getSendFlowInstanceByDocumentId(odFlowInstance.getDocumentId());
        }

        return sendFlowInstance;
    }

    public OdFlowInstance getSendOdFlowInstance() throws Exception
    {
        if (sendOdFlowInstance == null)
        {
            SendFlowInstance sendFlowInstance = getSendFlowInstance();

            sendOdFlowInstance = getDao().getOdFlowInstance(sendFlowInstance.getInstanceId());
        }

        return sendOdFlowInstance;
    }

    protected Long getReceiveBodyId() throws Exception
    {
        if (receiveBodyId == null)
        {
            receiveBodyId = getSystemFlowDao().getBodyId(getSendFlowInstance().getInstanceId());
        }
        return receiveBodyId;
    }

    @Override
    protected Long getBodyId(String name) throws Exception
    {
        if ("send".equals(name))
            return getSendFlowInstance().getBodyId();

        if ("receive".equals(name))
        {
            return getReceiveBodyId();
        }

        return super.getBodyId(name);
    }

    @NotSerialized
    public SystemFormContext getSendFormContext() throws Exception
    {
        return getFormContext("send");
    }

    @Override
    protected BusinessContext getBusinessContext(SystemFormContext formContext) throws Exception
    {
        if ("send".equals(formContext.getFormName()))
        {
            BusinessContext businessContext = new BusinessContext();

            businessContext.setBusinessDeptId(getSendOdFlowInstance().getDeptId());

            initBusinessContext(businessContext);

            return businessContext;
        }

        return super.getBusinessContext(formContext);
    }

    @Override
    protected FormRole getRole(SystemFormContext formContext) throws Exception
    {
        if ("send".equals(formContext.getFormName()) || "receive".equals(formContext.getFormName()))
        {
            return formContext.getForm().getRole(Constants.Union.UNION);
        }

        return super.getRole(formContext);
    }

    @Override
    public String getActionName(Action action) throws Exception
    {
        if ((autoSendWithLastUnion == null || autoSendWithLastUnion) && action instanceof RouteGroup)
        {
            if (!"false".equals(action.getProperty("send")))
            {
                List<Route> routes = ((RouteGroup) action).getRoutes();
                if (routes.size() == 1 && FlowNode.END.equals(routes.get(0).getNextNodeId()))
                {
                    if (isTextFinal())
                    {
                        //判断是不是最后一个联合发文单位
                        List<Union> unions = exchangeSendService.getCurrentUnions(getOdFlowInstance().getDocumentId());

                        boolean send = true;
                        Long receiveId = getOdFlowInstance().getReceiveId();
                        for (Union union : unions)
                        {
                            if (!union.getReceiveId().equals(receiveId))
                            {
                                send = false;
                                break;
                            }
                        }

                        if (send)
                        {
                            //最后一个发送公文的单位，转换动作名称为发送公文

                            return Tools.getMessage("ods.flow.sendDocument");
                        }
                    }
                    else
                    {
                        //返回主办单位
                        return Tools.getMessage("ods.flow.unionBackMainDept");
                    }
                }
            }
        }

        return super.getActionName(action);
    }

    @NotSerialized
    public List<PageItem> getSendPageItems() throws Exception
    {
        OdFlowInstance sendOdFlowInstance = getSendOdFlowInstance();
        String sendDeptName = sendOdFlowInstance.getDept().getDeptName();

        List<PageItem> items = getPageItems("send");
        for (PageItem item : items)
        {
            item.setTitle(sendDeptName + item.getFormPage().getTitle());
        }

        return items;
    }

    /**
     * 收文的稿笺页，仅对主办单位是收文转发文或者内部公文转发文有效
     *
     * @return 收文或者内部公文的稿笺页
     * @throws Exception 访问数据错误
     */
    @NotSerialized
    public List<PageItem> getReceivePageItems() throws Exception
    {
        OdFlowInstance sendOdFlowInstance = getSendOdFlowInstance();

        if (!"send".equals(sendOdFlowInstance.getType()))
        {
            Long receiveBodyId = getReceiveBodyId();
            if (receiveBodyId != null && !receiveBodyId.equals(getSendFlowInstance().getBodyId()))
            {
                String sendDeptName = sendOdFlowInstance.getDept().getDeptName();
                List<PageItem> items = getPageItems("receive");
                for (PageItem item : items)
                {
                    item.setTitle(sendDeptName + item.getFormPage().getTitle());
                }

                return items;
            }
        }

        return null;
    }

    @NotSerialized
    public OfficeDocument getSendDocument() throws Exception
    {
        return getDocument();
    }

    @Override
    public OfficeDocument getDocument(String type) throws Exception
    {
        if ("send".equals(type))
            return getSendDocument();
        else if ("receive".equals(type))
            return getSendOdFlowInstance().getDocument();

        return null;
    }

    /**
     * 是否已经成文
     *
     * @return 已经成文返回true，未成文返回false，可以在页面和javascript中使用此方法
     * @throws Exception 数据库读取数据错误
     */
    public boolean isTextFinal() throws Exception
    {
        SendFlowInstance sendFlowInstance = getSendFlowInstance();
        return sendFlowInstance.isTextFinal() != null && sendFlowInstance.isTextFinal();
    }

    @Override
    public OfficeEditType getEditType(OfficeDocument document, String type) throws Exception
    {
        if ("send".equals(type))
        {
            //只要不是只读状态，便以跟踪方式打开正文
            return isEditable() ? OfficeEditType.track : OfficeEditType.readOnly;
        }

        return super.getEditType(document, type);
    }

    @Override
    protected List<Action> createActions() throws Exception
    {
        List<Action> actions = super.createActions();

        //添加查看正文的按钮
        boolean showTextExists = false;

        int n = actions.size();
        for (int i = 0; i < n; i++)
        {
            Action action = actions.get(i);

            String actionId = action.getActionId();
            if (SendableFlowPage.SHOW_SEND_TEXT.equals(actionId))
            {
                showTextExists = true;
            }
            else if (SendableFlowPage.EDIT_SEND_TEXT.equals(actionId))
            {
                if (!isEditable() || getOdFlowInstance().getState() == OdFlowInstanceState.closed)
                {
                    //只读状态或者流程结束状态，不允许编辑正文，把编辑正文按钮换成查看正文
                    actions.set(i, SendableFlowPage.SHOW_SEND_TEXT_ACTION);
                }

                showTextExists = true;
            }
        }

        if (!showTextExists)
        {
            //在前面加入查看正文的按钮
            actions.add(0, isEditable() && getOdFlowInstance().getState() != OdFlowInstanceState.closed &&
                    !FlowNode.PASS.equals(getNodeId()) ? SendableFlowPage.EDIT_SEND_TEXT_ACTION :
                    SendableFlowPage.SHOW_SEND_TEXT_ACTION);
        }

        if ("receive".equals(getSendOdFlowInstance().getType()))
        {
            //收文转发文发起的联合办文，添加查看来文按钮
            actions.add(0, ReceiveFlowPage.SHOW_RECEIVE_TEXT_ACTION);
        }

        return actions;
    }

    @Override
    protected void collectData() throws Exception
    {
        super.collectData();

        //采集发文表单
        lockFormBody("send");
        collectFormData(getSendFormContext());

        OdFlowInstance sendOdFlowInstance = getSendOdFlowInstance();

        if (!"send".equals(sendOdFlowInstance.getType()))
        {
            Long receiveBodyId = getReceiveBodyId();
            if (receiveBodyId != null && !receiveBodyId.equals(getSendFlowInstance().getBodyId()))
            {
                collectFormData(getFormContext("receive"));
            }
        }
    }

    @Override
    protected void saveData() throws Exception
    {
        super.saveData();

        //保存发文表单
        saveForm(getSendFormContext());

        if (!"send".equals(sendOdFlowInstance.getType()))
        {
            Long receiveBodyId = getReceiveBodyId();
            if (receiveBodyId != null && !receiveBodyId.equals(getSendFlowInstance().getBodyId()))
            {
                saveForm(getFormContext("receive"));
            }
        }
    }

    @Override
    protected Object endOdFlow(String actionId) throws Exception
    {
        ReceiverListList receiverListList = null;

        if (isTextFinal() && autoSendWithLastUnion)
        {
            Action action = getFlowContext().getNode().getAction(actionId);
            if (!"false".equals(action.getProperty("send")))
            {
                OdFlowDao dao = getDao();

                //判断是否应该发送公文
                SendFlowInstance sendFlowInstance = getSendFlowInstance();

                //锁住发文流程实例，避免重复发送
                dao.lockSendFlowInstance(sendFlowInstance.getInstanceId());

                List<Union> unions = exchangeSendService.getCurrentUnions(getOdFlowInstance().getDocumentId());

                boolean send = true;
                Long receiveId = getOdFlowInstance().getReceiveId();
                for (Union union : unions)
                {
                    if (!union.getReceiveId().equals(receiveId))
                    {
                        send = false;
                        break;
                    }
                }

                //已经成文，如果是最后一个环节，发送公文
                if (send)
                {
                    receiverListList = sendDocument();
                }
            }
        }

        Object result = super.endOdFlow(this.actionId);

        Union union = getDao().getUnion(getOdFlowInstance().getReceiveId());
        if (union.getStepId() != null)
        {
            OdSystemFlowDao systemFlowDao = (OdSystemFlowDao) getSystemFlowDao();

            //将主办部门的流程中对应步骤表示为已完成
            FlowStep step = new FlowStep();
            step.setStepId(union.getStepId().toString());
            step.setState(FlowStep.DEALED);
            step.setDisposeTime(new Date());
            systemFlowDao.updateStep(step);

            //将主办单位发起环节标识为未读状态
            Long unionInstanceId = null;
            List<FlowStep> preSteps = systemFlowDao.getPreSteps(union.getStepId().toString());
            for (FlowStep preStep : preSteps)
            {
                if (preStep.getState() == FlowStep.NODEAL)
                {
                    preStep.setState(FlowStep.NODEAL_REPLYED);
                    preStep.setShowTime(new Date());
                    systemFlowDao.updateStep(preStep);

                    if (unionInstanceId == null)
                        unionInstanceId = Long.valueOf(preStep.getInstanceId());
                }
                else if (preStep.getState() == FlowStep.DEALED)
                {
                    preStep.setState(FlowStep.DEALED_REPLYED_NOACCEPT);
                    preStep.setShowTime(new Date());
                    systemFlowDao.updateStep(preStep);

                    if (unionInstanceId == null)
                        unionInstanceId = Long.valueOf(preStep.getInstanceId());
                }
            }

            if (unionInstanceId != null)
                systemFlowDao.refreshStepQ(unionInstanceId);
        }

        //通知主办单位
        exchangeNotifyServiceProvider.get().sendEndMessage(getReceiveBase());

        return receiverListList == null ? result : receiverListList;
    }

    /**
     * 发送公文
     *
     * @return 收单位列表，用于返回给客户端提示
     * @throws Exception 发送公文错误
     */
    protected ReceiverListList sendDocument() throws Exception
    {
        OdFlowDao dao = getDao();

        SendFlowInstance sendFlowInstance = getSendFlowInstance();
        OdFlowInstance sendOdFlowInstance = getSendOdFlowInstance();

        Boolean sentOut = sendFlowInstance.getSentOut();
        if (sentOut == null || !sentOut)
        {
            Send send = new Send();

            if (sendFlowInstance.getSendDeptId() == null)
                send.setDeptId(sendOdFlowInstance.getDeptId());
            else
                send.setDeptId(sendFlowInstance.getSendDeptId());

            if (sendFlowInstance.getCreator() == null)
            {
                send.setCreator(sendOdFlowInstance.getCreator());
                send.setCreatorName(sendOdFlowInstance.getCreateUser().getUserName());
            }
            else
            {
                send.setCreator(sendFlowInstance.getCreator());
                send.setCreatorName(sendFlowInstance.getCreateUser().getUserName());
            }

            if (sendFlowInstance.getCreateDeptId() == null)
                send.setCreateDeptId(sendFlowInstance.getCreateDeptId());
            else
                send.setCreateDeptId(sendOdFlowInstance.getCreateDeptId());

            send.setSender(getBusinessContext().getUserId());
            send.setSenderName(getBusinessContext().getUserName());
            send.setDocumentId(sendFlowInstance.getDocumentId());
            send.setSendNumberId(sendFlowInstance.getSendNumberId());
            send.setRedHeadId(sendFlowInstance.getRedHeadId());
            send.setSendTime(new Date());
            send.setState(SendState.sended);
            dao.add(send);

            OfficeDocument document = getSendDocument();
            exchangeSendService.sendDocument(document);

            try
            {
                exchangeNotifyServiceProvider.get().sendMessageWithSendId(send.getSendId(), null, false);
            }
            catch (Throwable ex)
            {
                //短信发送失败不影响其他逻辑
                Tools.log(ex);
            }

            //结束发文流程
            sendOdFlowInstance.setState(OdFlowInstanceState.closed);
            dao.update(sendOdFlowInstance);

            sendFlowInstance.setSentOut(true);
            dao.update(sendFlowInstance);

            //将发文流程相应的步骤提交到结束环节
            Long unionStepId = sendFlowInstance.getUnionStepId();
            if (unionStepId != null)
            {
                FlowContext sendFlowContext = FlowApi.loadContext(unionStepId, getSystemFlowDao());

                if (sendFlowContext.isEditable())
                {
                    String actionId = null;
                    FlowNode flowNode = sendFlowContext.getNode();
                    for (Action action : flowNode.getActions())
                    {
                        if (action instanceof RouteGroup)
                        {
                            List<Route> routes = ((RouteGroup) action).getRoutes();
                            if (routes.size() == 1 && FlowNode.END.equals(routes.get(0).getNextNodeId()))
                            {
                                Object result = sendFlowContext.executeAction(action.getActionId());

                                if (result instanceof NodeReceiverSelectList)
                                {
                                    List<NodeReceiverSelect> list = ((NodeReceiverSelectList) result).getList();
                                    if (list.size() == 1 && FlowNode.END.equals(list.get(0).getNodeId()))
                                        actionId = action.getActionId();
                                }
                                break;
                            }
                        }
                    }

                    sendFlowContext.sendTo(Collections.singletonMap(FlowNode.END, (List<List<NodeReceiver>>) null),
                            actionId);
                }
            }

            for (FlowExtension extension : getExtensions().values())
            {
                if (extension instanceof SendableFlowExtension)
                {
                    ((SendableFlowExtension) extension).sendDocument(getFlowComponentContext(), document);
                }
            }

            return document.getReceiverList().getReceivers();
        }

        return null;
    }

    @Override
    protected Object beforeCancelSend() throws Exception
    {
        Object r = super.beforeCancelSend();
        if (r != null)
            return r;

        if (getOdFlowInstance().getState() == OdFlowInstanceState.closed)
        {
            return Tools.getMessage("ods.flow.unionSended");
        }

        return null;
    }

    @Override
    public List<PrintInfo> getPrintTemplates() throws Exception
    {
        List<PrintInfo> printInfos;

        if (getBodyId() != null)
            printInfos = super.getPrintTemplates();
        else
            printInfos = new ArrayList<PrintInfo>(2);

        OdFlowInstance sendOdFlowInstance = getSendOdFlowInstance();

        for (PrintTemplate template : getPrintService().getPrintTemplates(sendOdFlowInstance.getBusinessId(),
                sendOdFlowInstance.getDeptId(), sendOdFlowInstance.getType()))
        {
            if ("send".equals(sendOdFlowInstance.getType()) || template.getFormType() == FormType.send)
            {
                printInfos.add(new PrintInfo(template.getTemplateId(),
                        sendOdFlowInstance.getDept().getDeptName() + template.getShowName(), "send"));
            }
            else
            {
                printInfos.add(new PrintInfo(template.getTemplateId(),
                        sendOdFlowInstance.getDept().getDeptName() + template.getShowName(), "receive"));
            }
        }

        return printInfos;
    }
}
