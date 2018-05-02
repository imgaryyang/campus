package com.gzzm.ods.flow;

import com.gzzm.ods.document.OfficeDocument;
import com.gzzm.ods.exchange.*;
import com.gzzm.ods.print.*;
import com.gzzm.platform.commons.*;
import com.gzzm.platform.form.SystemFormContext;
import com.gzzm.platform.weboffice.OfficeEditType;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.*;
import net.cyan.valmiki.flow.*;
import net.cyan.valmiki.form.*;
import net.cyan.valmiki.form.components.FileListData;

import java.util.*;

/**
 * 会签流程处理
 *
 * @author camel
 * @date 12-10-19
 */
@Service(url = "/ods/flow/collect")
public class CollectFlowPage extends OdFlowPage
{
    /**
     * 会签对象
     */
    @NotSerialized
    private Collect collect;

    /**
     * 收文对象
     */
    @NotSerialized
    private ReceiveBase receiveBase;

    /**
     * 发起会签的流程的实例对象，如果此会签是转发的，则包括往上一级级的流程
     */
    private List<OdFlowInstance> collectInstances;

    public CollectFlowPage()
    {
    }

    @Override
    protected void initFormContext(SystemFormContext formContext) throws Exception
    {
        super.initFormContext(formContext);

        if (formContext.getFormName() == null)
        {
            formContext.setAuthority(Constants.Document.TITLE, FormRole.READONLY);
            formContext.setAuthority(Constants.Document.TITLE1, FormRole.READONLY);
            formContext.setAuthority(Constants.Document.SOURCEDEPT, FormRole.READONLY);
            formContext.setAuthority(Constants.Document.SENDNUMBER, FormRole.READONLY);
            formContext.setAuthority(Constants.Document.SENDNUMBER1, FormRole.READONLY);
            formContext.setAuthority(Constants.Document.SUBJECT, FormRole.READONLY);
            formContext.setAuthority(Constants.Document.SECRET, FormRole.READONLY);
            formContext.setAuthority(Constants.Document.SECRET1, FormRole.READONLY);
            formContext.setAuthority(Constants.Document.SECRET2, FormRole.READONLY);
            formContext.setAuthority(Constants.Document.PRIORITY, FormRole.READONLY);
            formContext.setAuthority(Constants.Document.PRIORITY1, FormRole.READONLY);
            formContext.setAuthority(Constants.Document.PRIORITY2, FormRole.READONLY);
            formContext.setAuthority(Constants.Document.PRIORITY3, FormRole.READONLY);
            formContext.setAuthority(Constants.Document.PRIORITY4, FormRole.READONLY);
            formContext.setAuthority(Constants.Document.SENDCOUNT, FormRole.READONLY);
            formContext.setAuthority(Constants.Document.ATTACHMENT, FormRole.READONLY);
            formContext.setAuthority(Constants.Document.ATTACHMENT1, FormRole.READONLY);
            formContext.setAuthority(Constants.Receive.SENDTIME, FormRole.READONLY);

            Long attachmentId = getDocument().getAttachmentId();
            if (attachmentId != null)
            {
                FormData formData = formContext.getFormData();

                ComponentData attachmentData = formData.getData(Constants.Document.ATTACHMENT);
                if (attachmentData == null)
                    attachmentData = formData.getData(Constants.Document.ATTACHMENT1);
                if (attachmentData instanceof FileListData)
                {
                    FileListData fileListData = (FileListData) attachmentData;
                    if (StringUtils.isEmpty(fileListData.getFileListId()) || "@".equals(fileListData.getFileListId()))
                    {
                        fileListData.setFileListId(attachmentId.toString());
                        formContext.save();
                    }
                }
            }
        }
    }

    public Long getReceiveId() throws Exception
    {
        OdFlowInstance odFlowInstance = getOdFlowInstance();

        return odFlowInstance.getReceiveId();
    }

    public Collect getCollect() throws Exception
    {
        if (collect == null)
            collect = getDao().getCollect(getReceiveId());

        return collect;
    }

    public ReceiveBase getReceiveBase() throws Exception
    {
        if (receiveBase == null)
        {
            if (collect != null)
                receiveBase = collect.getReceiveBase();

            if (receiveBase == null)
            {
                Long receiveId = getReceiveId();
                if (receiveId != null)
                    receiveBase = getDao().getReceiveBase(receiveId);
            }
        }

        return receiveBase;
    }

    protected List<OdFlowInstance> getCollectInstances() throws Exception
    {
        if (collectInstances == null)
        {
            collectInstances = new ArrayList<OdFlowInstance>();
            Collect collect = getCollect();

            OdFlowInstance collectInstance = collect.getCollectInstance();
            collectInstances.add(collectInstance);

            while ("collect".equals(collectInstance.getType()))
            {
                collectInstances.add(
                        collectInstance = getDao().getCollect(collectInstance.getReceiveId()).getCollectInstance());
            }
        }

        return collectInstances;
    }

    protected OdFlowInstance getCollectInstance(Long instanceId) throws Exception
    {
        for (OdFlowInstance instance : getCollectInstances())
        {
            if (instance.getInstanceId().equals(instanceId))
                return instance;
        }

        return null;
    }

    @Override
    public Long getTopInstanceId() throws Exception
    {
        return getCollect().getTopInstanceId();
    }

    @Override
    public String getTopForm() throws Exception
    {
        return "collect_" + getTopInstanceId();
    }

    @Override
    public String getFormName(Long instanceId) throws Exception
    {
        if (instanceId.equals(getInstanceId()))
            return "";

        return "collect_" + instanceId;
    }

    @Override
    protected FormRole getRole(SystemFormContext formContext) throws Exception
    {
        if (formContext.getFormName() != null && formContext.getFormName().startsWith("collect_"))
        {
            return formContext.getForm().getRole(Constants.Collect.COLLECT);
        }

        return super.getRole(formContext);
    }

    @Override
    protected Long getBodyId(String name) throws Exception
    {
        if (name != null && name.startsWith("collect_"))
        {
            name = name.substring(8);

            int index = name.indexOf('_');
            if (index > 0)
            {
                String type = name.substring(index + 1);

                if ("send".equals(type))
                {
                    SendFlowInstance sendFlowInstance =
                            getDao().getSendFlowInstance(Long.valueOf(name.substring(0, index)));
                    return sendFlowInstance.getBodyId();
                }
            }
            else
            {
                return getSystemFlowDao().getBodyId(Long.valueOf(name));
            }
        }

        return super.getBodyId(name);
    }

    @NotSerialized
    public List<PageItem> getCollectPageItems() throws Exception
    {
        List<PageItem> pageItems = new ArrayList<PageItem>();

        for (OdFlowInstance instance : getCollectInstances())
        {
            Long instanceId = instance.getInstanceId();
            String type = instance.getType();
            if ("receive".equals(type) || "inner".equals(type))
            {
                SendFlowInstance sendFlowInstance = getDao().getSendFlowInstance(instanceId);
                if (sendFlowInstance != null && sendFlowInstance.isSended() != null && sendFlowInstance.isSended())
                    addPageItems(pageItems, getPageItems("collect_" + instanceId + "_send"), instance);
            }

            addPageItems(pageItems, getPageItems("collect_" + instanceId), instance);
        }

        return pageItems;
    }

    private void addPageItems(List<PageItem> pageItems, List<PageItem> collectPageItems, OdFlowInstance instance)
    {
        if (collectPageItems != null)
        {
            for (PageItem pageItem : collectPageItems)
            {
                pageItem.setTitle(instance.getDept().getDeptName() + pageItem.getTitle());

                pageItems.add(pageItem);
            }
        }
    }

    @Override
    protected List<Action> createActions() throws Exception
    {
        List<Action> actions = super.createActions();

        OdFlowInstance topInstance = getCollect().getTopInstance();
        String type = topInstance.getType();
        if ("send".equals(type))
        {
            //发文发起的会签，添加查看正文按钮
            actions.add(0, SendableFlowPage.SHOW_SEND_TEXT_ACTION);
        }
        else if ("receive".equals(type))
        {
            //收文发起的会签，添加查看来文按钮
            actions.add(0, ReceiveFlowPage.SHOW_RECEIVE_TEXT_ACTION);

            //内部公文发起的会签，如果转发文，添加查看正文按钮
            SendFlowInstance sendFlowInstance = getDao().getSendFlowInstance(topInstance.getInstanceId());
            if (sendFlowInstance != null && sendFlowInstance.isSended() != null && sendFlowInstance.isSended())
                actions.add(0, SendableFlowPage.SHOW_SEND_TEXT_ACTION);
        }
        else
        {
            boolean b = false;
            if (getDocument().getTextId() != null)
            {
                b = true;
            }
            else
            {
                SendFlowInstance sendFlowInstance = getDao().getSendFlowInstance(topInstance.getInstanceId());
                if (sendFlowInstance != null && sendFlowInstance.isSended() != null && sendFlowInstance.isSended())
                    b = true;
            }
            if (b)
                actions.add(0, SendableFlowPage.SHOW_SEND_TEXT_ACTION);
        }

        return actions;
    }

    @Override
    protected void collectData() throws Exception
    {
        super.collectData();

        for (OdFlowInstance instance : getCollectInstances())
        {
            //保存主办单位的表单
            Long instanceId = instance.getInstanceId();
            collectFormData(getFormContext("collect_" + instanceId));

            String type = instance.getType();
            if ("receive".equals(type) || "inner".equals(type))
            {
                SendFlowInstance sendFlowInstance = getDao().getSendFlowInstance(instanceId);
                if (sendFlowInstance != null && sendFlowInstance.isSended() != null && sendFlowInstance.isSended())
                    collectFormData(getFormContext("collect_" + instanceId + "_send"));
            }
        }
    }

    @Override
    protected void saveData() throws Exception
    {
        super.saveData();

        for (OdFlowInstance instance : getCollectInstances())
        {
            //保存主办单位的表单
            Long instanceId = instance.getInstanceId();
            saveForm(getFormContext("collect_" + instanceId));

            String type = instance.getType();
            if ("receive".equals(type) || "inner".equals(type))
            {
                SendFlowInstance sendFlowInstance = getDao().getSendFlowInstance(instanceId);
                if (sendFlowInstance != null && sendFlowInstance.isSended() != null && sendFlowInstance.isSended())
                    saveForm(getFormContext("collect_" + instanceId + "_send"));
            }
        }
    }

    @Override
    protected Object endOdFlow(String actionId) throws Exception
    {
        OfficeDocument document = getDocument("collect_" + getReceiveId());

        if (document.getTextId() == null)
            throw new NoErrorException("ods.flow.collectTextUnEdit");

        Object result = super.endOdFlow(actionId);

        Collect collect = getCollect();
        collect.setEndTime(new Date());
        getDao().update(collect);

        if (collect.getStepId() != null)
        {
            String stepId = collect.getStepId().toString();

            //将主办部门的流程中对应步骤表示为已完成
            OdSystemFlowDao systemFlowDao = (OdSystemFlowDao) getSystemFlowDao();
            FlowStep step = new FlowStep();
            step.setStepId(stepId);
            step.setDisposeTime(new Date());
            step.setState(FlowStep.DEALED);
            systemFlowDao.updateStep(step);

            Long collectInstanceId = null;
            List<FlowStep> preSteps = systemFlowDao.getPreSteps(stepId);
            for (FlowStep preStep : preSteps)
            {
                if (preStep.getState() == FlowStep.NODEAL || preStep.getState() == FlowStep.NODEAL_REPLYED)
                {
                    preStep.setState(FlowStep.NODEAL_REPLYED);
                    preStep.setShowTime(new Date());
                    systemFlowDao.updateStep(preStep);

                    if (collectInstanceId == null)
                        collectInstanceId = Long.valueOf(preStep.getInstanceId());
                }
            }

            if (collectInstanceId != null)
                systemFlowDao.refreshStepQ(collectInstanceId);
        }

        //通知主办单位
        exchangeNotifyServiceProvider.get().sendEndMessage(getReceiveBase());

        return result;
    }

    @Override
    public OfficeDocument getDocument(String type) throws Exception
    {
        if ("send".equals(type))
        {
            SendFlowInstance sendFlowInstance = getDao().getSendFlowInstance(getTopInstanceId());
            if (sendFlowInstance != null)
                return sendFlowInstance.getDocument();
            else
                return getDocument();
        }
        else if ("receive".equals(type))
        {
            return getDocument();
        }

        return super.getDocument(type);
    }

    @Override
    public OfficeEditType getEditType(OfficeDocument document, String type) throws Exception
    {
        if ("send".equals(type))
        {
            return OfficeEditType.readOnly;
        }
        else if ("receive".equals(type))
        {
            return OfficeEditType.readOnly;
        }

        return super.getEditType(document, type);
    }

    @Override
    public List<PrintInfo> getPrintTemplates() throws Exception
    {
        List<PrintInfo> printInfos = super.getPrintTemplates();
        PrintService printService = getPrintService();

        for (OdFlowInstance instance : getCollectInstances())
        {
            List<PrintTemplate> printTemplates =
                    printService.getPrintTemplates(instance.getBusinessId(), instance.getDeptId(), instance.getType());

            Long instanceId = instance.getInstanceId();
            String type = instance.getType();
            for (PrintTemplate template : printTemplates)
            {
                String formName = null;
                if (template.getFormType() == FormType.send)
                {
                    if ("receive".equals(type) || "inner".equals(type))
                    {
                        SendFlowInstance sendFlowInstance = getDao().getSendFlowInstance(instanceId);
                        if (sendFlowInstance != null && sendFlowInstance.isSended() != null &&
                                sendFlowInstance.isSended())
                        {
                            formName = "collect_" + instanceId + "_send";
                        }
                    }
                }
                else
                {
                    formName = "collect_" + instanceId;
                }

                if (formName != null)
                {
                    printInfos.add(new PrintInfo(template.getTemplateId(),
                            instance.getDept().getDeptName() + "" + template.getShowName(), formName));
                }
            }
        }

        return printInfos;
    }

    @Override
    public String getActionName(Action action) throws Exception
    {
        if (SEND_COLLECTS.equals(action.getActionId()))
        {
            Object name = action.getActionNameObject();
            if (name == null || "".equals(name))
            {
                return Tools.getMessage("cyan.valmiki.flow.action.sendCollectsForCollect.name");
            }
        }

        return super.getActionName(action);
    }

    @Override
    public String getActionRemark(Action action) throws Exception
    {
        if (SEND_COLLECTS.equals(action.getActionId()))
        {
            Object remark = action.getProperty("remark");
            if (remark == null || "".equals(remark))
            {
                return Tools.getMessage("cyan.valmiki.flow.action.sendCollectsForCollect.remark");
            }
        }

        return super.getActionName(action);
    }

    @Override
    public void executePageItem(PageItem item) throws Exception
    {
        String formName = item.getFormContext().getFormName();
        if (formName != null && formName.startsWith("collect_"))
        {
            String s = formName.substring(8);

            int index = s.indexOf('_');
            if (index > 0)
            {
                s = s.substring(0, index);
            }

            Long instanceId = Long.valueOf(s);

            try
            {
                OdFlowInstance collectInstance = getCollectInstance(instanceId);
                if (collectInstance != null)
                    getBusinessContext().setBusinessDeptName(collectInstance.getDept().getDeptName());

                super.executePageItem(item);
            }
            finally
            {
                getBusinessContext().setBusinessDeptName(null);
            }
        }
        else
        {
            super.executePageItem(item);
        }
    }

    @Override
    protected InputFile downFormPrint(Integer printTemplateId, String printFormName, boolean toDoc) throws Exception
    {
        if (printFormName != null && printFormName.startsWith("collect_"))
        {
            String s = printFormName.substring(8);

            int index = s.indexOf('_');
            if (index > 0)
            {
                s = s.substring(0, index);
            }

            Long instanceId = Long.valueOf(s);

            OdFlowInstance collectInstance = getCollectInstance(instanceId);

            if (collectInstance != null)
            {
                getBusinessContext().setBusinessDeptName(collectInstance.getDept().getDeptName());
            }
        }

        return super.downFormPrint(printTemplateId, printFormName, toDoc);
    }
}
