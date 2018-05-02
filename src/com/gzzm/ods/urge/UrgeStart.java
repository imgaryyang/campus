package com.gzzm.ods.urge;

import com.gzzm.platform.commons.Tools;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

/**
 * @author camel
 * @date 12-5-4
 */
public class UrgeStart implements Runnable
{
    @Inject
    private static Provider<UrgeDao> daoProvider;

    public UrgeStart()
    {
    }

    public void run()
    {
        try
        {
            for (OdFlowUrge urge : daoProvider.get().getShouldRemindFlowUrges())
            {

                UrgeJob.updateJob(urge);
            }

            for (CollectUrge urge : daoProvider.get().getShouldRemindCollectUrges())
            {

                CollectUrgeJob.updateJob(urge);
            }
        }
        catch (Throwable ex)
        {
            //已经最外层，不能继续抛出异常
            Tools.log(ex);
        }
    }
}
