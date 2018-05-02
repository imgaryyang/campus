package com.gzzm.platform.message.sms;

import com.gzzm.platform.commons.Tools;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

/**
 * 短信回执接收任务
 *
 * @author camel
 * @date 2011-2-14
 */
public class ReceiptReceiverJob implements Runnable
{
    @Inject
    private static Provider<SmsService> serviceProvider;

    public ReceiptReceiverJob()
    {
    }

    public void run()
    {
        try
        {
            serviceProvider.get().receive();
        }
        catch (Throwable ex)
        {
            Tools.log(ex);
        }
    }
}
