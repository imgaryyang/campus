package com.gzzm.platform.message.sms;

import com.gzzm.platform.commons.*;
import com.gzzm.platform.message.Message;
import net.cyan.arachne.RequestContext;
import net.cyan.commons.util.StringUtils;

import javax.servlet.http.*;

/**
 * @author camel
 * @date 2016/6/24
 */
public class SmsVerifier
{
    static final String SMS_SESSION_NAME = "$$$sms_verifier_sms$$$";

    static final String PHONE_SESSION_NAME = "$$$sms_verifier_phone$$$";

    public SmsVerifier()
    {
    }

    public static void sendSms(String phone) throws Exception
    {
        sendSms(phone, null);
    }

    public static void sendSms(String phone, String msg) throws Exception
    {
        sendSms(phone, msg, RequestContext.getContext().getRequest());
    }

    public static void sendSms(String phone, String msg, HttpServletRequest request) throws Exception
    {
        if (StringUtils.isEmpty(msg))
            msg = "sms_verifier.sms";

        String sms = Double.toString(Math.random()).substring(3, 7);
        HttpSession session = request.getSession();

        session.setAttribute(SMS_SESSION_NAME, sms);
        session.setAttribute(PHONE_SESSION_NAME, phone);

        Message message = new Message();
        message.setPhone(phone);
        message.setContent(Tools.getMessage(msg, new Object[]{sms}));

        SmsService.getService().send(message);
    }

    public static void check(String phone, String sms)
    {
        check(phone, sms, RequestContext.getContext().getRequest());
    }

    public static void check(String phone, String sms, HttpServletRequest request)
    {
        String phone1 = (String) request.getSession().getAttribute(PHONE_SESSION_NAME);
        String sms1 = (String) request.getSession().getAttribute(SMS_SESSION_NAME);

        if (phone1 == null || sms1 == null)
            throw new NoErrorException("sms_verifier.timeout");

        if (!phone1.equals(phone))
            throw new NoErrorException("sms_verifier.errorphone");

        if (!sms1.equals(sms))
            throw new NoErrorException("sms_verifier.smserror");
    }
}
