package com.gzzm.oa.leaveword;

import com.gzzm.ods.flow.*;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;
import net.cyan.valmiki.form.*;

/**
 * @author camel
 * @date 12-11-21
 */
public class LeaveWordFlowComponent extends AbstractOdFlowComponent
{
    @Inject
    private static Provider<LeaveWordDao> daoProvider;

    public LeaveWordFlowComponent()
    {
    }

    public static void initFormData(FormData formData, LeaveWord leaveWord)
    {
        formData.setValue("标题", leaveWord.getTitle());
        formData.setValue("内容", leaveWord.getContent());
        formData.setValue("留言人", leaveWord.isAnonymous() != null && leaveWord.isAnonymous() ?
                "匿名" : leaveWord.getUser().getUserName());
        formData.setValue("留言时间", leaveWord.getLeaveTime());
    }

    @Override
    public void initForm(FormContext formContext, OdFlowContext context) throws Exception
    {
        super.initForm(formContext, context);

        LeaveWordDao leaveWordDao = daoProvider.get();
        LeaveWord leaveWord = leaveWordDao.getLeaveWord(Integer.valueOf(context.getOdFlowInstance().getLinkId()));

        formContext.setAuthority("标题", FormRole.READONLY);
        formContext.setAuthority("内容", FormRole.READONLY);
        formContext.setAuthority("留言人", FormRole.READONLY);
        formContext.setAuthority("留言时间", FormRole.READONLY);

        initFormData(formContext.getFormData(), leaveWord);
    }

    @Override
    public void endFlow(OdFlowContext context) throws Exception
    {
        super.endFlow(context);

        LeaveWordDao leaveWordDao = daoProvider.get();
        LeaveWord leaveWord = leaveWordDao.getLeaveWord(Integer.valueOf(context.getOdFlowInstance().getLinkId()));

        leaveWord.setState(LeaveWordState.end);

        leaveWordDao.update(leaveWord);
    }

    @Override
    public void stopFlow(OdFlowContext context) throws Exception
    {
        super.stopFlow(context);

        LeaveWordDao leaveWordDao = daoProvider.get();
        LeaveWord leaveWord = leaveWordDao.getLeaveWord(Integer.valueOf(context.getOdFlowInstance().getLinkId()));

        leaveWord.setState(LeaveWordState.end);

        leaveWordDao.update(leaveWord);
    }
}
