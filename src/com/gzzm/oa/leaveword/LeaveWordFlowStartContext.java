package com.gzzm.oa.leaveword;

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
public class LeaveWordFlowStartContext extends OdFlowStartContext
{
    @Inject
    private LeaveWordDao leaveWordDao;

    private Integer leaveWordId;

    private LeaveWord leaveWord;

    public static LeaveWordFlowStartContext create() throws Exception
    {
        return create(LeaveWordFlowStartContext.class);
    }

    public LeaveWordFlowStartContext()
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
        return LeaveWordFlowComponent.class;
    }

    public Integer getLeaveWordId()
    {
        return leaveWordId;
    }

    public void setLeaveWordId(Integer leaveWordId)
    {
        this.leaveWordId = leaveWordId;
    }

    public LeaveWord getLeaveWord() throws Exception
    {
        if (leaveWord == null)
            leaveWord = leaveWordDao.getLeaveWord(leaveWordId);

        return leaveWord;
    }

    @Override
    @Transactional
    public void start() throws Exception
    {
        //锁住留言记录，避免重复接收
        leaveWordDao.lockLeaveWord(leaveWordId);

        LeaveWord leaveWord = getLeaveWord();
        if (leaveWord.getState() != LeaveWordState.noAccepted)
            throw new NoErrorException("oa.leaveword.accepted");

        setTitle(leaveWord.getTitle());
        getBusinessContext().setBusinessDeptId(leaveWord.getDeptId());

        super.start();

        leaveWord.setState(LeaveWordState.accepted);
        leaveWordDao.update(leaveWord);
    }

    @Override
    protected void initOdFlowInstance(OdFlowInstance odFlowInstance) throws Exception
    {
        super.initOdFlowInstance(odFlowInstance);

        odFlowInstance.setLinkId(leaveWordId.toString());
        odFlowInstance.setState(OdFlowInstanceState.unclosed);

//        LeaveWord leaveWord = getLeaveWord();
    }

    @Override
    protected void initForm(SystemFormContext formContext) throws Exception
    {
        super.initForm(formContext);

        LeaveWordFlowComponent.initFormData(formContext.getFormData(), getLeaveWord());
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
