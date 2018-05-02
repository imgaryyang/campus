package com.gzzm.platform.flow;

import com.gzzm.platform.commons.Tools;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

/**
 * @author camel
 * @date 2016/9/11
 */
public class MakeDeptLastJob implements Runnable
{
    @Inject
    private static Provider<MakeDeptLastService> serviceProvider;

    public MakeDeptLastJob()
    {
    }

    @Override
    public void run()
    {
        MakeDeptLastService service = serviceProvider.get();

        try
        {
            while (service.make())
            {

            }
        }
        catch (Throwable ex)
        {
            Tools.log(ex);
        }
    }
}
