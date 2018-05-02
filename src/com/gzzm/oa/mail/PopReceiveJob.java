package com.gzzm.oa.mail;

import com.gzzm.platform.commons.Tools;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

/**
 * pop3邮件接收任务
 *
 * @author camel
 * @date 2010-6-15
 */
public class PopReceiveJob implements Runnable
{
    @Inject
    private static Provider<PopReceiveService> serviceProvider;

    public PopReceiveJob()
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
