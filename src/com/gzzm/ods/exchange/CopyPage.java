package com.gzzm.ods.exchange;

import com.gzzm.ods.document.OfficeDocument;
import com.gzzm.ods.flow.OdSystemFlowDao;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.transaction.Transactional;
import net.cyan.nest.annotation.Inject;
import net.cyan.valmiki.flow.FlowStep;

/**
 * 查看抄送公文的操作
 *
 * @author camel
 * @date 12-12-16
 */
@Service
public class CopyPage
{
    @Inject
    private ExchangeCopyDao dao;

    @NotSerialized
    private Long stepId;

    private Long receiveId;

    @NotSerialized
    private ReceiveBase receiveBase;

    @NotSerialized
    private Copy copy;

    private boolean readOnly;

    public CopyPage()
    {
    }

    public boolean isReadOnly()
    {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly)
    {
        this.readOnly = readOnly;
    }

    public Long getStepId() throws Exception
    {
        if (stepId == null && receiveId != null)
        {
            stepId = getCopy().getStepId();
        }
        return stepId;
    }

    public void setStepId(Long stepId)
    {
        this.stepId = stepId;
    }

    public Long getReceiveId() throws Exception
    {
        if (receiveId == null && stepId != null)
        {
            receiveId = getCopy().getReceiveId();
        }

        return receiveId;
    }

    public void setReceiveId(Long receiveId)
    {
        this.receiveId = receiveId;
    }

    public Copy getCopy() throws Exception
    {
        if (copy == null)
        {
            if (receiveId != null)
                copy = dao.getCopy(receiveId);
            else if (stepId != null)
                copy = dao.getCopyByStepId(stepId);
        }
        return copy;
    }

    public ReceiveBase getReceiveBase() throws Exception
    {
        if (receiveBase == null)
            receiveBase = dao.getReceiveBase(getReceiveId());
        return receiveBase;
    }

    @NotSerialized
    public OfficeDocument getDocument() throws Exception
    {
        return getReceiveBase().getDocument();
    }

    @Service(url = "/ods/copy")
    @Transactional
    public String show() throws Exception
    {
        if (!readOnly)
        {
            ReceiveBase receiveBase = getReceiveBase();
            if (receiveBase.getState() == ReceiveState.noAccepted)
            {
                FlowStep step = new FlowStep();
                step.setStepId(getStepId().toString());
                step.setState(FlowStep.PASSACCEPTED);
                OdSystemFlowDao.getInstance().updateStep(step);

                receiveBase.setState(ReceiveState.accepted);
                dao.update(receiveBase);
            }
        }

        return "copy";
    }

    @Service
    @ObjectResult
    @Transactional
    public void end() throws Exception
    {
        ReceiveBase receiveBase = getReceiveBase();
        if (receiveBase.getState().ordinal() < 3)
        {
            FlowStep step = new FlowStep();
            step.setStepId(getStepId().toString());
            step.setState(FlowStep.PASSREPLYED);
            OdSystemFlowDao.getInstance().updateStep(step);

            receiveBase.setState(ReceiveState.end);
            dao.update(receiveBase);
        }
    }
}
