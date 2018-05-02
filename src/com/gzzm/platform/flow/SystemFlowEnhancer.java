package com.gzzm.platform.flow;

import com.gzzm.platform.organ.*;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;
import net.cyan.valmiki.flow.*;


/**
 * @author camel
 * @date 2010-9-19
 */
public final class SystemFlowEnhancer extends AbstractFlowEnhancer
{
    @Inject
    private static Provider<DeptService> deptServiceProvider;

    static final SystemFlowEnhancer INSTANCE = new SystemFlowEnhancer();

    private SystemFlowEnhancer()
    {
    }

    public void initReceiver(NodeReceiver receiver, FlowProperty property) throws Exception
    {
        if (property instanceof FlowInstance)
        {
            FlowInstance instance = (FlowInstance) property;
            FlowStep firstStep = instance.getService().getFirstStep();

            if (firstStep != null)
                property = firstStep;
        }

        DeptService deptService = null;

        String receiverValue = receiver.getReceiver();
        User user = null;

        if (receiverValue.indexOf('@') < 0 && receiverValue.indexOf('#') < 0 && receiverValue.indexOf('$') < 0)
        {
            deptService = deptServiceProvider.get();
            user = deptService.getDao().getUser(Integer.valueOf(receiverValue));
        }

        Integer deptId = DataConvert.convertType(Integer.class, receiver.getProperty("deptId"));
        if (deptId == null)
        {
            receiver.setProperty("deptId", deptId = DataConvert.convertType(Integer.class,
                    property.getProperty("deptId")));
        }

        if (deptId == null)
        {
            if (user != null)
            {
                if (user.getDepts().size() > 0)
                    receiver.setProperty("deptId", deptId = user.getDepts().get(0).getDeptId());
                receiver.setProperty("stations", FlowFunctions.getStations(user, null));
            }
        }
        else if (user != null)
        {
            receiver.setProperty("stations", FlowFunctions.getStations(user, deptId));
        }

        if (deptId != null)
        {
            if (receiver.getProperty("deptName") == null)
            {
                if (deptService == null)
                    deptService = deptServiceProvider.get();

                DeptInfo dept = deptService.getDept(deptId);

                if (dept != null)
                    receiver.setProperty("deptName", dept.getDeptName());
                else
                    receiver.setProperty("deptName", "");
            }
        }
    }

    public void initStep(FlowStep step, FlowProperty property) throws Exception
    {
        Integer deptId = DataConvert.convertType(Integer.class, property.getProperty("deptId"));
        step.setProperty("deptId", deptId);
        step.setProperty("consignationId", property.getProperty("consignationId"));

        if (deptId != null)
        {
            if (step.getProperty("deptName") == null)
            {
                DeptInfo dept = deptServiceProvider.get().getDept(deptId);

                if (dept != null)
                    step.setProperty("deptName", dept.getDeptName());
                else
                    step.setProperty("deptName", "");
            }
        }
    }

    @Override
    public boolean isReceiver(String creator, String receiver) throws Exception
    {
        return (receiver.contains("@") || receiver.contains("#")) &&
                FlowApi.getReceivers(Integer.valueOf(creator)).contains(receiver);
    }
}
