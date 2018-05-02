package com.gzzm.ods.exchange;

import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

/**
 * @author camel
 * @date 2017/3/13
 */
public class ExchangeNotifyJob implements Runnable
{
    @Inject
    private static Provider<ExchangeNotifyService> serviceProvider;

    public ExchangeNotifyJob()
    {
    }

    @Override
    public void run()
    {
        ExchangeNotifyService service = serviceProvider.get();

        try
        {
            while (service.notifyReceives())
                ;
        }
        catch (Throwable ex)
        {
            ExceptionUtils.logException(ex);
        }
    }
}
