package com.gzzm.oa.mail;

import com.gzzm.platform.commons.Tools;

/**
 * 邮件服务器初始化
 *
 * @author camel
 * @date 2010-4-15
 */
public class MailInit implements Runnable
{
    public MailInit()
    {
    }

    public void run()
    {
        MailContext context = MailContext.getContext();

        try
        {
            if (context != null && context.isSmtp())
                context.getServer().startSmtpServer();
        }
        catch (Exception ex)
        {
            Tools.log(ex);
        }

        try
        {
            if (context != null && context.isPop3())
                context.getServer().startPop3Server();
        }
        catch (Exception ex)
        {
            Tools.log(ex);
        }
    }
}
