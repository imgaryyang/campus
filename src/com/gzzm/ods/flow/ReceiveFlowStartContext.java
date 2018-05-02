package com.gzzm.ods.flow;

import com.gzzm.ods.document.OfficeDocument;
import com.gzzm.ods.exchange.*;
import com.gzzm.ods.receivetype.ReceiveTypeService;
import com.gzzm.platform.commons.NoErrorException;
import com.gzzm.platform.form.SystemFormContext;
import net.cyan.commons.transaction.Transactional;
import net.cyan.nest.annotation.Inject;
import net.cyan.valmiki.flow.*;
import net.cyan.valmiki.form.FormData;

import java.util.*;

/**
 * 接收收文时发起流程
 *
 * @author camel
 * @date 11-10-2
 */
public class ReceiveFlowStartContext extends OdFlowStartContext
{
    @Inject
    private ReceiveTypeService receiveTypeService;

    private Long receiveId;

    private ReceiveBase receiveBase;

    private String serial;

    public static ReceiveFlowStartContext create() throws Exception
    {
        return create(ReceiveFlowStartContext.class);
    }

    public ReceiveFlowStartContext()
    {
    }

    public Long getReceiveId()
    {
        return receiveId;
    }

    public void setReceiveId(Long receiveId)
    {
        this.receiveId = receiveId;
    }

    public ReceiveBase getReceiveBase() throws Exception
    {
        if (receiveBase == null && receiveId != null)
            receiveBase = dao.getReceiveBase(receiveId);

        return receiveBase;
    }

    public void setReceiveBase(ReceiveBase receiveBase)
    {
        this.receiveBase = receiveBase;
    }

    @Override
    public Long getDocumentId() throws Exception
    {
        return getReceiveBase().getDocumentId();
    }

    @Override
    public OfficeDocument getDocument() throws Exception
    {
        return getReceiveBase().getDocument();
    }

    @Override
    public String getFlowTag() throws Exception
    {
        String s = getReceiveBase().getType().toString();
        if ("unionseal".equals(s))
            s = "union";
        return s;
    }

    @Override
    @Transactional
    public void start() throws Exception
    {
        //锁住收文记录，避免重复接收
        dao.lockReceiveBase(receiveId);

        ReceiveBase receiveBase = getReceiveBase();
        if (receiveBase.getState() == ReceiveState.backed)
            throw new NoErrorException("ods.exchange.receiveBacked");

        if (receiveBase.getState() == ReceiveState.withdrawn)
            throw new NoErrorException("ods.exchange.receiveWithdrawn");

        if (receiveBase.getState() == ReceiveState.canceled)
            throw new NoErrorException("ods.exchange.receiveCanceled");

        if (receiveBase.getState() != ReceiveState.noAccepted)
            throw new NoErrorException("ods.exchange.receiveRepeatedly");

        setTitle(receiveBase.getDocument().getTitle());
        getBusinessContext().setBusinessDeptId(getReceiveBase().getDeptId());

        Receive receive = null;
        Long stepId = null;
        ReceiveType type = receiveBase.getType();
        if (type == ReceiveType.receive)
        {
            receive = dao.getReceive(receiveBase.getReceiveId());

            com.gzzm.ods.receivetype.ReceiveType receiveType = receiveTypeService.getDao().getReceiveType(
                    receiveBase.getDeptId(), receiveBase.getDocument().getSourceDept());

            if (receiveType != null)
            {
                serial = receiveTypeService.getSerial(receiveType, receiveBase.getDeptId());

                receive.setReceiveTypeId(receiveType.getReceiveTypeId());
                receive.setSerial(serial);
            }

            stepId = receive.getStepId();
        }
        else if (type == ReceiveType.union || type == ReceiveType.unionseal)
        {
            stepId = dao.getUnion(receiveId).getStepId();
        }
        else if (type == ReceiveType.collect)
        {
            stepId = dao.getCollect(receiveId).getStepId();
        }
        else if (type == ReceiveType.uniondeal)
        {
            stepId = dao.getUnionDeal(receiveId).getStepId();
        }

        super.start();

        receiveBase.setState(ReceiveState.accepted);
        receiveBase.setAcceptTime(new Date());
        receiveBase.setReceiver(getBusinessContext().getUserId());
        receiveBase.setReceiverName(getBusinessContext().getUserName());
        dao.update(receiveBase);

        if (receive != null)
            dao.update(receive);

        if (stepId != null)
        {
            //将主办部门的流程中相应步骤标识为已接收
            FlowStep step = new FlowStep();
            step.setStepId(stepId.toString());
            step.setAcceptTime(new Date());
            step.setState(FlowStep.NODEAL);

            getSystemFlowDao().updateStep(step);
        }
    }

    @Override
    protected void initOdFlowInstance(OdFlowInstance odFlowInstance) throws Exception
    {
        super.initOdFlowInstance(odFlowInstance);

        odFlowInstance.setDocumentId(getDocumentId());
        odFlowInstance.setReceiveId(getReceiveId());
        odFlowInstance.setState(OdFlowInstanceState.unclosed);
        odFlowInstance.setPriority(getDocument().getPriority());
        odFlowInstance.setDeadline(getReceiveBase().getDeadline());
        if (serial != null)
            odFlowInstance.setSerial(serial);
    }

    @Override
    protected void initForm(SystemFormContext formContext) throws Exception
    {
        super.initForm(formContext);

        ReceiveBase receiveBase = getReceiveBase();
        OfficeDocument document = getDocument();

        FormData formData = formContext.getFormData();

        formData.setValue(Constants.Document.TITLE, document.getTitle());
        formData.setValue(Constants.Document.TITLE1, document.getTitle());
        formData.setValue(Constants.Document.SOURCEDEPT, document.getSourceDept());
        formData.setValue(Constants.Document.SENDNUMBER, document.getSendNumber());
        formData.setValue(Constants.Document.SENDNUMBER1, document.getSendNumber());
        formData.setValue(Constants.Document.SUBJECT, document.getSubject());
        formData.setValue(Constants.Document.SECRET, document.getSecret());
        formData.setValue(Constants.Document.SECRET1, document.getSecret());
        formData.setValue(Constants.Document.SECRET2, document.getSecret());
        formData.setValue(Constants.Document.PRIORITY, document.getPriority());
        formData.setValue(Constants.Document.PRIORITY1, document.getPriority());
        formData.setValue(Constants.Document.PRIORITY2, document.getPriority());
        formData.setValue(Constants.Document.PRIORITY3, document.getPriority());
        formData.setValue(Constants.Document.PRIORITY4, document.getPriority());
        formData.setValue(Constants.Document.SENDCOUNT, document.getSendCount());
        formData.setValue(Constants.Document.ATTACHMENT, document.getAttachmentId());
        formData.setValue(Constants.Document.ATTACHMENT1, document.getAttachmentId());
        formData.setValue(Constants.Receive.ACCEPTTIME, new Date());
        formData.setValue(Constants.Receive.SENDTIME, receiveBase.getSendTime());
        formData.setValue(Constants.Receive.RECEIVER, businessContext.getUserName());
        if (receiveBase.getDeadline() != null)
            formData.setValue(Constants.Flow.DEADLINE, receiveBase.getDeadline());
        if (serial != null)
            formData.setValue(Constants.Flow.SERIAL, serial);

        for (Map.Entry<String, String> entry : document.getAttributes().entrySet())
        {
            formData.setValue(entry.getKey(), entry.getValue());
        }
    }

    @Override
    protected void initFlowContext(FlowContext flowContext) throws Exception
    {
        super.initFlowContext(flowContext);

        //收文都不放草稿箱
        flowContext.getInstance().setState(0);
        flowContext.getStep().setState(FlowStep.NODEAL);
    }
}
