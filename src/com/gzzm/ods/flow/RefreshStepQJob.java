package com.gzzm.ods.flow;

import com.gzzm.platform.commons.Tools;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

/**
 * @author camel
 * @date 2018/4/16
 */
public class RefreshStepQJob implements Runnable
{
    @Inject
    private static Provider<RefreshStepQService> serviceProvider;

    public RefreshStepQJob()
    {
    }

    @Override
    public void run()
    {
        try
        {
            Tools.log("start refresh stepq:");
            serviceProvider.get().refresh();
        }
        catch (Throwable ex)
        {
            Tools.log(ex);
        }
    }
}
