package com.gzzm.platform.message.sms;

import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

/**
 * @author camel
 * @date 2010-8-23
 */
public class BaseSmsSender implements SmsSender
{
    @Inject
    private static Provider<SmsDao> daoProvider;

    @Inject
    private SimpleSmsSender sender;

    public BaseSmsSender()
    {
    }

    public void send(SmsLog smsLog) throws Exception
    {
        String serial = null;

        if (!StringUtils.isEmpty(smsLog.getMessageCode()))
        {
            serial = daoProvider.get().getSmsSerial().toString();
            smsLog.setSerial(serial);
        }

        smsLog.setMessageId(sender.send(smsLog.getPhone(), smsLog.getContent(), serial));
    }
}
