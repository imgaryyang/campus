package com.gzzm.ods.flow;

import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

/**
 * @author camel
 * @date 2017/4/5
 */
public class OdFlowApi
{
    @Inject
    private static Provider<OdFlowApiService> serviceProvider;

    public OdFlowApi()
    {
    }

    public static void startFlow(Integer businessId, Long sourceInstanceId, Integer userId, Integer deptId)
            throws Exception
    {
        serviceProvider.get().startFlow(businessId, sourceInstanceId, userId, deptId);
    }
}
