package com.gzzm.platform.flow;

import com.gzzm.platform.commons.Tools;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

/**
 * @author camel
 * @date 2014/9/2
 */
public abstract class BakSystemFlowDao extends SystemFlowDao
{
    @Inject
    private static Provider<BakSystemFlowDao> daoProvider;

    public static BakSystemFlowDao getInstance()
    {
        return daoProvider.get();
    }

    public BakSystemFlowDao()
    {
        super(FlowInstanceBak.class, FlowStepBak.class);
    }

    public void recover(Long instanceId) throws Exception
    {
        FlowInstanceBak instance1 = (FlowInstanceBak) getFlowInstance(instanceId);

        SystemFlowDao dao = (SystemFlowDao) Tools.getBean(Class.forName(instance1.getDao()));

        SystemFlowInstance instance2 = dao.getInstanceClass().newInstance();

        copyInstance(instance1, instance2);
        save(instance2);

        for (SystemFlowStep step1 : getSteps(instanceId))
        {
            SystemFlowStep step2 = dao.getStepClass().newInstance();
            copyStep(step1, step2);
            save(step2);
        }

        dao.recover(instanceId);

        deleteInstance(instanceId.toString());
    }
}
