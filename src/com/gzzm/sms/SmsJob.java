package com.gzzm.sms;

import com.gzzm.platform.commons.Tools;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

/**
 * @author camel
 * @date 12-10-9
 */
public class SmsJob implements Runnable
{
    @Inject
    private static Provider<SmsService> serviceProvider;

    private String content;

    private String phone;

    private Integer userId;

    public SmsJob()
    {
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

    public void run()
    {
        try
        {
            serviceProvider.get().sendSms(content, phone.split(","), null, userId, null);
        }
        catch (Throwable ex)
        {
            Tools.log(ex);
        }
    }
}
