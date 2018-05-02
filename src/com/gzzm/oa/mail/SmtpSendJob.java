package com.gzzm.oa.mail;

import com.gzzm.platform.commons.Tools;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

/**
 * 外部邮件发送任务
 *
 * @author camel
 * @date 2010-6-14
 */
public class SmtpSendJob implements Runnable
{
    @Inject
    private static Provider<SmtpSendService> serviceProvider;

    public SmtpSendJob()
    {
    }

    public void run()
    {
        send0();
    }

    public static void send()
    {
        Tools.run(new SmtpSendJob());
    }

    private static synchronized void send0()
    {
        try
        {
            serviceProvider.get().send();
        }
        catch (Throwable ex)
        {
            Tools.log(ex);
        }
    }
}
