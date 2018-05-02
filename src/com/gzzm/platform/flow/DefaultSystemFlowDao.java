package com.gzzm.platform.flow;

import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

/**
 * @author camel
 * @date 2014/8/21
 */
public abstract class DefaultSystemFlowDao extends SystemFlowDao
{
    @Inject
    private static Provider<DefaultSystemFlowDao> daoProvider;

    public static DefaultSystemFlowDao getInstance()
    {
        return daoProvider.get();
    }

    public DefaultSystemFlowDao()
    {
        super(SystemFlowInstance.class, SystemFlowStep.class);
    }
}
