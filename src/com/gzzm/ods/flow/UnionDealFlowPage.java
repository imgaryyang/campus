package com.gzzm.ods.flow;

import com.gzzm.ods.document.OfficeDocument;
import com.gzzm.ods.exchange.*;
import com.gzzm.ods.print.*;
import com.gzzm.platform.form.SystemFormContext;
import com.gzzm.platform.weboffice.OfficeEditType;
import net.cyan.arachne.annotation.*;
import net.cyan.valmiki.flow.*;
import net.cyan.valmiki.form.*;

import java.util.*;

/**
 * 联合办文流程处理
 *
 * @author camel
 * @date 12-12-12
 */
@Service(url = "/ods/flow/uniondeal")
public class UnionDealFlowPage extends OdFlowPage
{
    /**
     * 联合办文对象
     */
    @NotSerialized
    private UnionDeal unionDeal;

    /**
     * 收文对象
     */
    @NotSerialized
    private ReceiveBase receiveBase;

    public UnionDealFlowPage()
    {
    }

    public Long getReceiveId() throws Exception
    {
        OdFlowInstance odFlowInstance = getOdFlowInstance();

        return odFlowInstance.getReceiveId();
    }

    public UnionDeal getUnionDeal() throws Exception
    {
        if (unionDeal == null)
            unionDeal = getDao().getUnionDeal(getReceiveId());

        return unionDeal;
    }

    public ReceiveBase getReceiveBase() throws Exception
    {
        if (receiveBase == null)
        {
            if (unionDeal != null)
                receiveBase = unionDeal.getReceiveBase();

            if (receiveBase == null)
            {
                Long receiveId = getReceiveId();
                if (receiveId != null)
                    receiveBase = getDao().getReceiveBase(receiveId);
            }
        }

        return receiveBase;
    }

    protected OdFlowInstance getUnionInstance() throws Exception
    {
        return getUnionDeal().getUnionInstance();
    }

    protected Long getUnionInstanceId() throws Exception
    {
        return getUnionDeal().getUnionInstanceId();
    }

    @Override
    protected FormRole getRole(SystemFormContext formContext) throws Exception
    {
        if (formContext.getFormName() != null && formContext.getFormName().startsWith("union"))
        {
            return formContext.getForm().getRole(Constants.UnionDeal.UNIONDEAL);
        }

        return super.getRole(formContext);
    }

    @Override
    protected Long getBodyId(String name) throws Exception
    {
        if (name != null)
        {
            if (name.equals("union_send"))
            {
                SendFlowInstance sendFlowInstance = getDao().getSendFlowInstance(getUnionInstanceId());
                return sendFlowInstance.getBodyId();
            }
            else
            {
                return getSystemFlowDao().getBodyId(getUnionInstanceId());
            }
        }

        return super.getBodyId(name);
    }

    @NotSerialized
    public List<PageItem> getUnionPageItems() throws Exception
    {
        List<PageItem> pageItems = new ArrayList<PageItem>();

        String type = getUnionInstance().getType();
        if ("receive".equals(type) || "inner".equals(type))
        {
            SendFlowInstance sendFlowInstance = getDao().getSendFlowInstance(getUnionInstanceId());
            if (sendFlowInstance != null && sendFlowInstance.isSended() != null && sendFlowInstance.isSended())
                addUnionPageItems(pageItems, getPageItems("union_send"));
        }

        addUnionPageItems(pageItems, getPageItems("union"));

        return pageItems;
    }

    private void addUnionPageItems(List<PageItem> pageItems, List<PageItem> unionPageItems) throws Exception
    {
        for (PageItem pageItem : unionPageItems)
        {
            pageItem.setTitle(getUnionInstance().getDept().getDeptName() + pageItem.getTitle());

            pageItems.add(pageItem);
        }
    }

    @Override
    public boolean accept(Action action) throws Exception
    {
        return !SendableFlowPage.EDIT_SEND_TEXT.equals(action.getActionId());
    }

    @Override
    protected List<Action> createActions() throws Exception
    {
        List<Action> actions = super.createActions();

        OdFlowInstance unionInstance = getUnionInstance();
        String type = unionInstance.getType();
        if ("send".equals(type))
        {
            //发文发起的联合办文，添加查看正文按钮
            actions.add(0, SendableFlowPage.SHOW_SEND_TEXT_ACTION);
        }
        else if ("receive".equals(type) || "inner".equals(type))
        {
            if ("receive".equals(type))
            {
                //收文发起的会签，添加查看来文按钮
                actions.add(0, ReceiveFlowPage.SHOW_RECEIVE_TEXT_ACTION);
            }

            //内部公文发起的联合办文，如果转发文，添加查看正文按钮
            SendFlowInstance sendFlowInstance = getDao().getSendFlowInstance(unionInstance.getInstanceId());
            if (sendFlowInstance != null && sendFlowInstance.isSended() != null && sendFlowInstance.isSended())
                actions.add(0, SendableFlowPage.SHOW_SEND_TEXT_ACTION);
        }

        return actions;
    }

    @Override
    protected void collectData() throws Exception
    {
        super.collectData();

        //保存主办单位的表单
        collectFormData(getFormContext("union"));

        OdFlowInstance unionInstance = getUnionInstance();
        String type = unionInstance.getType();
        if ("receive".equals(type) || "inner".equals(type))
        {
            SendFlowInstance sendFlowInstance = getDao().getSendFlowInstance(unionInstance.getInstanceId());
            if (sendFlowInstance != null && sendFlowInstance.isSended() != null && sendFlowInstance.isSended())
                collectFormData(getFormContext("union_send"));
        }
    }

    @Override
    protected void saveData() throws Exception
    {
        super.saveData();

        saveForm(getFormContext("union"));

        OdFlowInstance unionInstance = getUnionInstance();
        String type = unionInstance.getType();
        if ("receive".equals(type) || "inner".equals(type))
        {
            SendFlowInstance sendFlowInstance = getDao().getSendFlowInstance(unionInstance.getInstanceId());
            if (sendFlowInstance != null && sendFlowInstance.isSended() != null && sendFlowInstance.isSended())
                saveForm(getFormContext("union_send"));
        }
    }

    @Override
    protected Object endOdFlow(String actionId) throws Exception
    {
        Object result = super.endOdFlow(actionId);

        UnionDeal unionDeal = getUnionDeal();
        if (unionDeal.getStepId() != null)
        {
            //将主办部门的流程中对应步骤表示为已完成
            FlowStep step = new FlowStep();
            step.setStepId(unionDeal.getStepId().toString());
            step.setDisposeTime(new Date());
            step.setState(FlowStep.DEALED);

            getSystemFlowDao().updateStep(step);
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
            return getDao().getSendFlowInstance(getUnionInstanceId()).getDocument();
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

        OdFlowInstance unionInstance = getUnionInstance();
        List<PrintTemplate> printTemplates =
                printService.getPrintTemplates(unionInstance.getBusinessId(), unionInstance.getDeptId(),
                        unionInstance.getType());

        Long instanceId = unionInstance.getInstanceId();
        String type = unionInstance.getType();
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
                        formName = "union_send";
                    }
                }
            }
            else
            {
                formName = "union";
            }

            if (formName != null)
            {
                printInfos.add(new PrintInfo(template.getTemplateId(),
                        unionInstance.getDept().getDeptName() + "" + template.getShowName(), formName));
            }
        }

        return printInfos;
    }

    @Override
    protected String[] getConsignationModules() throws Exception
    {
        return new String[]{"union"};
    }
}
