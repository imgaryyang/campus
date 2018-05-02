package com.gzzm.ods.flow;

import com.gzzm.platform.commons.Tools;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

/**
 * @author camel
 * @date 12-4-26
 */
public class SendRecordsInit implements Runnable
{
    @Inject
    private static Provider<SendRecordService> serviceProvider;

    public SendRecordsInit()
    {
    }

    public void run()
    {
        try
        {
            while (serviceProvider.get().saveRecords()) ;
        }
        catch (Throwable ex)
        {
            Tools.log(ex);
        }
    }
}
