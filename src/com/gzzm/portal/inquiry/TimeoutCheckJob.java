package com.gzzm.portal.inquiry;

import com.gzzm.platform.commons.Tools;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

/**
 * @author camel
 * @date 12-8-25
 */
public class TimeoutCheckJob implements Runnable
{
    @Inject
    private static Provider<InquiryService> serviceProvider;

    public TimeoutCheckJob()
    {
    }

    public void run()
    {
        try
        {
            serviceProvider.get().checkTimeouts();
        }
        catch (Throwable ex)
        {
            Tools.log(ex);
        }
    }
}