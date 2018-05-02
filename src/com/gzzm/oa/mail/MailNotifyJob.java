package com.gzzm.oa.mail;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.event.Events;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

import java.util.List;

/**
 * @author camel
 * @date 2017/3/13
 */
public class MailNotifyJob implements Runnable
{
    @Inject
    private static Provider<MailDao> daoProvider;

    public MailNotifyJob()
    {
    }

    @Override
    public void run()
    {
        try
        {
            while (true)
            {
                List<Mail> mails = daoProvider.get().getNoNotifiedMails();
                if (mails.size() == 0)
                    break;

                for (Mail mail : mails)
                {
                    Events.invoke(mail, "receive");
                }
            }
        }
        catch (Throwable ex)
        {
            Tools.log(ex);
        }
    }
}
