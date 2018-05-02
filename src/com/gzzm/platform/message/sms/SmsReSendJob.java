package com.gzzm.platform.message.sms;

import com.gzzm.platform.commons.Tools;


/**
 * @author camel
 * @date 12-5-2
 */
public class SmsReSendJob implements Runnable
{
    public SmsReSendJob()
    {
    }

    public void run()
    {
        try
        {
            SmsService.getService().sendNoSendedSms();
        }
        catch (Throwable ex)
        {
            Tools.log(ex);
        }
    }
}
