package com.gzzm.portal.inquiry;

import com.gzzm.ods.flow.*;
import com.gzzm.platform.commons.NoErrorException;
import com.gzzm.platform.form.SystemFormContext;
import net.cyan.commons.transaction.Transactional;
import net.cyan.nest.annotation.Inject;
import net.cyan.valmiki.flow.*;

/**
 * @author camel
 * @date 12-11-21
 */
public class InquiryFlowStartContext extends OdFlowStartContext
{
    @Inject
    private InquiryDao inquiryDao;

    private Long processId;

    private InquiryProcess process;

    public static InquiryFlowStartContext create() throws Exception
    {
        return create(InquiryFlowStartContext.class);
    }

    public InquiryFlowStartContext()
    {
    }

    @Override
    public void setComponent(Class<OdFlowComponent> component)
    {
        //只使用咨询投诉的流程
        throw new UnsupportedOperationException();
    }

    @Override
    public Class<? extends OdFlowComponent> getComponent()
    {
        return InquiryFlowComponent.class;
    }

    public Long getProcessId()
    {
        return processId;
    }

    public void setProcessId(Long processId)
    {
        this.processId = processId;
    }

    public InquiryProcess getProcess() throws Exception
    {
        if (process == null)
            process = inquiryDao.getProcess(processId);
        return process;
    }

    @Override
    @Transactional
    public void start() throws Exception
    {
        //锁住来信处理记录，避免重复接收
        inquiryDao.lockProcess(processId);

        InquiryProcess process = getProcess();
        if (process.getState() == ProcessState.REPLYED)
            throw new NoErrorException("portal.inquiry.processReplyed");

        if (process.getState() == ProcessState.TURNED)
            throw new NoErrorException("portal.inquiry.processTurned");

        if (process.isTurnInnered())
            throw new NoErrorException("portal.inquiry.processTurned");

        setTitle(process.getInquiry().getTitle());
        getBusinessContext().setBusinessDeptId(process.getDeptId());

        super.start();

        process.setTurnInnered(true);
        inquiryDao.update(process);
    }

    @Override
    protected void initOdFlowInstance(OdFlowInstance odFlowInstance) throws Exception
    {
        super.initOdFlowInstance(odFlowInstance);

        odFlowInstance.setLinkId(processId.toString());
        odFlowInstance.setState(OdFlowInstanceState.unclosed);

        InquiryProcess process = getProcess();
        Inquiry inquiry = process.getInquiry();

        if (inquiry.getCode() != null && inquiry.getCode().length() < 30)
            odFlowInstance.setSerial(inquiry.getCode());
    }

    @Override
    protected void initForm(SystemFormContext formContext) throws Exception
    {
        super.initForm(formContext);

        InquiryFlowComponent.initFormData(formContext.getFormData(), getProcess());
    }

    @Override
    protected void initFlowContext(FlowContext flowContext) throws Exception
    {
        super.initFlowContext(flowContext);

        //不放草稿箱
        flowContext.getInstance().setState(0);
        flowContext.getStep().setState(FlowStep.NODEAL);
    }
}
