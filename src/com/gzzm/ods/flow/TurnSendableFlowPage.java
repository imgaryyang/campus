package com.gzzm.ods.flow;

import com.gzzm.ods.business.BusinessModel;
import com.gzzm.ods.print.FormType;
import com.gzzm.ods.print.*;
import com.gzzm.platform.form.*;
import net.cyan.arachne.annotation.*;
import net.cyan.valmiki.flow.*;
import net.cyan.valmiki.form.*;

import java.util.List;

/**
 * @author camel
 * @date 2014/8/29
 */
@Service
public abstract class TurnSendableFlowPage extends SendableFlowPage
{
    /**
     * 发文表单ID，如果为-1表示用内部流程公用一个表单
     */
    protected Integer sendFormId;

    public TurnSendableFlowPage()
    {
    }

    @Override
    protected void initFlowContext(FlowContext flowContext) throws Exception
    {
        super.initFlowContext(flowContext);

        FormContext sendFormContext = null;

        SendFlowInstance sendFlowInstance = getSendFlowInstance(false);
        if (sendFlowInstance != null && sendFlowInstance.getBodyId() != null)
        {
            sendFormContext = getFormContext("send");
        }
        else
        {
            Integer sendFormId = createSendFormId();
            if (sendFormId != null)
            {
                sendFormContext = FormApi.newFormContext(sendFormId);
            }
        }

        if (sendFormContext != null)
        {
            ScriptContext scriptContext = flowContext.getScriptContext();
            scriptContext.addVariableContainer(new MapVariableContainer(sendFormContext.getContext()));
        }
    }

    protected Integer createSendFormId() throws Exception
    {
        BusinessModel business = getOdFlowInstance().getBusiness();
        if (business == null || business.getSendFormId() == null)
            return null;

        return FormApi.getLastFormId(business.getSendFormId());
    }

    @Override
    public boolean isSended() throws Exception
    {
        return SendFlowUtils.isSended(this);
    }

    @Override
    public boolean accept(Action action) throws Exception
    {
        String id = action.getActionId();

        if (TURN_SEND.equals(id))
            return !isSended();

        if (CANCEL_TURN_SEND.equals(id))
            return isSended();

        return super.accept(action);
    }

    @NotSerialized
    public Integer getSendFormId() throws Exception
    {
        if (sendFormId == null)
        {
            SendFlowInstance sendFlowInstance = getSendFlowInstance(false);
            if (sendFlowInstance != null)
            {
                Long bodyId = sendFlowInstance.getBodyId();
                if (bodyId != null)
                {
                    FormBody formBody = FormApi.getDao().getFormBody(bodyId);
                    sendFormId = formBody.getFormId();
                }
            }

            if (sendFormId == null)
            {
                sendFormId = createSendFormId();

                if (sendFormId == null)
                    sendFormId = -1;
            }
        }

        return sendFormId;
    }

    @Override
    protected boolean accept(PrintTemplate printTemplate) throws Exception
    {
        return isSended() && getSendFormId() != -1 || printTemplate.getFormType() != FormType.send;
    }

    @Override
    protected Long getBodyId(String name) throws Exception
    {
        if ("send".equals(name))
            return getSendFlowInstance(true).getBodyId();

        return super.getBodyId(name);
    }

    @Override
    @NotSerialized
    public SystemFormContext getSendFormContext() throws Exception
    {
        Integer sendFormId = getSendFormId();
        if (sendFormId == -1)
        {
            return getFormContext();
        }
        else
        {
            return getFormContext("send");
        }
    }

    @Override
    public SystemFormContext createSendFormContext() throws Exception
    {
        Integer sendFormId = createSendFormId();
        if (sendFormId == null)
        {
            return null;
        }
        else
        {
            SystemFormContext formContext = FormApi.newFormContext(sendFormId, "send");

            FormApi.saveFormBody(formContext);

            return formContext;
        }
    }

    @NotSerialized
    public List<PageItem> getSendPageItems() throws Exception
    {
        Integer sendFormId = getSendFormId();
        if (sendFormId == -1)
            return null;
        else
            return getPageItems("send");
    }

    @Override
    protected void collectData() throws Exception
    {
        super.collectData();

        if (isSended() && getBodyId("send") != null)
        {
            //采集发文表单
            lockFormBody("send");
            collectFormData(getSendFormContext());
        }
    }

    @Override
    protected void saveData() throws Exception
    {
        super.saveData();

        if (isSended() && getBodyId("send") != null)
        {
            //保存发文表单
            saveForm(getSendFormContext());
        }
    }

    /**
     * 取消转发文
     *
     * @throws Exception 取消转发文错误，由数据库操作错误引起
     */
    @Service
    @ObjectResult
    public void cancelTurnSend() throws Exception
    {
        SendFlowInstance sendFlowInstance = getSendFlowInstance(false);
        if (sendFlowInstance != null)
        {
            sendFlowInstance.setSended(false);
            getDao().update(sendFlowInstance);
        }

        FlowContext flowContext = getFlowContext();

        flowContext.getInstance().setTitle(getDocument().getTitle());
        flowContext.saveData();
    }
}
