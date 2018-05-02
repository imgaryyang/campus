package com.gzzm.ods.flow;

import com.gzzm.platform.commons.Tools;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

/**
 * @author camel
 * @date 12-4-26
 */
public class SignsInit implements Runnable
{
    @Inject
    private static Provider<OdSignService> serviceProvider;

    public SignsInit()
    {
    }

    public void run()
    {
        try
        {
            serviceProvider.get().initSigns(null);
        }
        catch (Throwable ex)
        {
            Tools.log(ex);
        }
    }
}
