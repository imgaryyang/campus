package com.gzzm.ods.flow;

import com.gzzm.platform.commons.Tools;
import net.cyan.nest.annotation.Inject;

import java.util.List;

/**
 * @author camel
 * @date 2018/4/16
 */
public class RefreshStepQService
{
    @Inject
    private OdSystemFlowDao dao;

    private Long lastInstanceId;

    public RefreshStepQService()
    {
    }

    public void refresh() throws Exception
    {
        if (lastInstanceId == null)
            lastInstanceId = Tools.getConfig("lastRefreshStepQInstanceId", Long.class);

        while (true)
        {
            List<Long> instanceIds = dao.queryInstanceIds(lastInstanceId);
            if (instanceIds.size() == 0)
                return;

            for (Long instanceId : instanceIds)
            {
                dao.refreshStepQ(instanceId);

                lastInstanceId = instanceId;
                Tools.setConfig("lastRefreshStepQInstanceId", lastInstanceId);
            }
        }
    }
}
