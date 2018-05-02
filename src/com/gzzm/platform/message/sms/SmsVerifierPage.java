package com.gzzm.platform.message.sms;

import com.gzzm.platform.commons.NoErrorException;
import net.cyan.arachne.annotation.*;

/**
 * @author camel
 * @date 2016/6/24
 */
@Service
public class SmsVerifierPage
{
    private String phone;

    private String msg;

    public SmsVerifierPage()
    {
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getMsg()
    {
        return msg;
    }

    public void setMsg(String msg)
    {
        this.msg = msg;
    }

    @Service(url = "/sms_verifier")
    @ObjectResult
    public String sendSms() throws Exception
    {
        try
        {
            SmsVerifier.sendSms(phone, msg);
        }
        catch (NoErrorException ex)
        {
            return ex.getMessage();
        }

        return null;
    }
}
